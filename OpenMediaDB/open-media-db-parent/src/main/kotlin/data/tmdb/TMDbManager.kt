package data.tmdb

import data.ExternalIds
import data.Show
import data.Video
import data.response.PagedResponse
import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.TmdbFind
import info.movito.themoviedbapi.TmdbTV
import info.movito.themoviedbapi.TmdbTvEpisodes
import info.movito.themoviedbapi.model.MovieDb
import info.movito.themoviedbapi.model.tv.TvEpisode
import info.movito.themoviedbapi.model.tv.TvSeries
import java.time.LocalDate

object TMDbManager {

    private val tmdbApi = TmdbApi(System.getenv("OPENMEDIADB_TMDB_API"))
    private const val language = "en"

    fun find(imdbId: String): Show? {
        tmdbApi.find.find(imdbId, TmdbFind.ExternalSource.imdb_id, language).let {
            return it.tvResults?.firstOrNull()?.let { toTVShow(it) } ?: run {
                it.movieResults?.firstOrNull()?.let { toMovie(it) }
            }
        }
    }

    fun getEpisodesFromSeason(parent: Show, seasons: IntRange): List<Video> {
        val videoList = mutableListOf<Video>()
        for (i in seasons) {
            val season = tmdbApi.tvSeasons.getSeason(parent.externalIds.tmdb!!, i, language)
            videoList.addAll(season.episodes.mapNotNull {
                toVideo(it, parent.imdbId)
            })
        }
        return videoList
    }

    fun getEpisode(tmdbParentId: Int, season: Int, episodeNumber: Int): Video? {
        val episode = tmdbApi.tvEpisodes.getEpisode(tmdbParentId, season, episodeNumber, language,
                TmdbTvEpisodes.EpisodeMethod.external_ids, TmdbTvEpisodes.EpisodeMethod.images)
        val parent = tmdbApi.tvSeries.getSeries(tmdbParentId, language, TmdbTV.TvMethod.external_ids)
        return toVideo(episode, parent.externalIds.imdbId)
    }

    fun search(query: String, page: Int = 0): PagedResponse<Show> {
        val searchResult = tmdbApi.search.searchMulti(query, language, page)
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
        tmdbApi.search.searchMulti(name, language, 0).results?.firstOrNull()?.let {
            return when (it) {
                is TvSeries -> toTVShow(it)
                is MovieDb -> toMovie(it)
                else -> null
            }
        }
        return null
    }

    private fun toTVShow(tvShow: TvSeries): Show? {
        val item = tmdbApi.tvSeries.getSeries(tvShow.id, language, TmdbTV.TvMethod.external_ids)
        if (item.externalIds.imdbId == null) return null
        val poster = if (item.posterPath != null) tmdbApi.configuration.baseUrl + tmdbApi.configuration.posterSizes?.last() + item.posterPath else null
        val background = if (item.backdropPath != null) tmdbApi.configuration.baseUrl + tmdbApi.configuration.backdropSizes?.last() + item.backdropPath else null
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
        val item = tmdbApi.movies.getMovie(movie.id, language)
        if (item.imdbID == null) return null
        val poster = if (item.posterPath != null) tmdbApi.configuration.baseUrl + tmdbApi.configuration.posterSizes?.last() + item.posterPath else null
        val background = if (item.backdropPath != null) tmdbApi.configuration.baseUrl + tmdbApi.configuration.backdropSizes?.last() + item.backdropPath else null
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
        val poster = if (image != null) tmdbApi.configuration.baseUrl + tmdbApi.configuration.posterSizes?.last() + image else null
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