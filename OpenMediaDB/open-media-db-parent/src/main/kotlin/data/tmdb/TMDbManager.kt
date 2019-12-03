package data.tmdb

import data.ExternalIds
import data.Show
import data.Video
import data.response.PagedResponse
import info.movito.themoviedbapi.TmdbFind
import info.movito.themoviedbapi.TmdbTV
import info.movito.themoviedbapi.TmdbTvEpisodes
import info.movito.themoviedbapi.model.MovieDb
import info.movito.themoviedbapi.model.tv.TvEpisode
import info.movito.themoviedbapi.model.tv.TvSeries
import java.time.LocalDate

object TMDbManager {
    val apiAccess by lazy {
        tmdbApi {
            defaultLanguage("en")
        }
    }

    val tmdbConfig by lazy { apiAccess.configuration ?: TODO("Init exception")}

    fun find(imdbId: String): Show? {
        apiAccess.find.find(imdbId, TmdbFind.ExternalSource.imdb_id, "en").let {
            return it.tvResults?.firstOrNull()?.let { toTVShow(it)} ?: run {
                it.movieResults?.firstOrNull()?.let { toMovie(it) }
            }
        }
    }

    fun getEpisodesFromSeason(parent: Show, seasons: IntRange): List<Video> {
        val videoList = mutableListOf<Video>()
        for (i in seasons) {
            val season = TMDbManager.apiAccess.tvSeasons.getSeason(parent.externalIds.tmdb!!, i, "en")
            videoList.addAll(season.episodes.mapNotNull {
                toVideo(it, parent.imdbId)
            })
        }
        return videoList
    }

    fun getEpisode(tmdbParentId: Int, season: Int, episode: Int): Video? {
        val episode = apiAccess.tvEpisodes.getEpisode(tmdbParentId, season, episode, "en",
                TmdbTvEpisodes.EpisodeMethod.external_ids, TmdbTvEpisodes.EpisodeMethod.images)
        val parent = apiAccess.tvSeries.getSeries(tmdbParentId, "en", TmdbTV.TvMethod.external_ids)
        return toVideo(episode, parent.externalIds.imdbId)
    }

    fun search(query: String, page: Int = 0): PagedResponse<Show> {
        val searchResult = apiAccess.search.searchMulti(query, "en", page)
        val showList = searchResult.results.mapNotNull {
            when (it) {
                is TvSeries -> toTVShow(it)
                is MovieDb -> toMovie(it)
                else -> null
            }
        }
        return PagedResponse(
                showList,
                searchResult.totalResults,
                page,
                searchResult.totalPages
        )
    }

    fun findByName(name: String): Show? {
        apiAccess.search.searchMulti(name, "en", 0).results?.firstOrNull()?.let {
            return when (it) {
                is TvSeries -> toTVShow(it)
                is MovieDb -> toMovie(it)
                else -> null
            }
        }
        return null
    }

    private fun toTVShow(tvShow: TvSeries): Show? {
        val item = apiAccess.tvSeries.getSeries(tvShow.id, "en", TmdbTV.TvMethod.external_ids)
        if (item.externalIds.imdbId == null) return null
        val poster = if (item.posterPath != null) tmdbConfig.baseUrl + tmdbConfig.posterSizes?.last() + item.posterPath else null
        val background = if (item.backdropPath != null) tmdbConfig.baseUrl + tmdbConfig.backdropSizes?.last() + item.backdropPath else null
        return Show(
                imdbId = item.externalIds.imdbId,
                name = item.name ?: item.originalName,
                sinopsis = item.overview,
                totalSeasons = item.numberOfSeasons,
                totalEpisodes = item.numberOfEpisodes,
                imgPoster = poster,
                imgBackground = background,
                path = "",
                externalIds = ExternalIds(imdb = item.externalIds?.imdbId,
                        tmdb = item.id,
                        tvdb = item.externalIds?.tvdbId?.toInt())
        )
    }

    private fun toMovie(movie: MovieDb): Show? {
        val item = apiAccess.movies.getMovie(movie.id, "en")
        if (item.imdbID == null) return null
        val poster = if (item.posterPath != null) tmdbConfig.baseUrl + tmdbConfig.posterSizes?.last() + item.posterPath else null
        val background = if (item.backdropPath != null) tmdbConfig.baseUrl + tmdbConfig.backdropSizes?.last() + item.backdropPath else null
        return Show(
                imdbId = item.imdbID,
                name = item.title ?: item.originalTitle,
                sinopsis = item.overview,
                totalSeasons = 1,
                totalEpisodes = 1,
                imgPoster = poster,
                imgBackground = background,
                path = "",
                externalIds = ExternalIds(imdb = item.imdbID,
                        tmdb = item.id)
        )
    }

    private fun toVideo(episode: TvEpisode, parentId: String): Video? {
        val image = episode.images?.stills?.firstOrNull()?.filePath
        val poster = if (image != null) tmdbConfig.baseUrl + tmdbConfig.posterSizes?.last() + image else null
        return Video(
                id = null,
                fileId = null,
                showId = parentId,
                imdbId = episode.externalIds?.imdbId,
                name = episode.name,
                season = episode.seasonNumber,
                airDate = if (episode.airDate.isNullOrBlank()) LocalDate.of(1970, 1, 1) else LocalDate.parse(episode.airDate),
                episodeNumber = episode.episodeNumber,
                sinopsis = episode.overview,
                imgPoster = poster,
                externalIds = ExternalIds(
                        imdb = episode.externalIds?.imdbId,
                        tvdb = episode.externalIds?.tvdbId?.toInt(),
                        tmdb = episode.id
                )

        )
    }
}