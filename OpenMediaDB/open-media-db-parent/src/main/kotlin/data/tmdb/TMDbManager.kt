package data.tmdb

import data.ExternalIds
import data.Show
import data.request.SearchRB
import info.movito.themoviedbapi.TmdbFind
import info.movito.themoviedbapi.TmdbMovies
import info.movito.themoviedbapi.TmdbTV
import info.movito.themoviedbapi.model.MovieDb
import info.movito.themoviedbapi.model.tv.TvSeries

object TMDbManager {
    val apiAccess by lazy {
        tmdbApi {
            defaultLanguage("en")
        }
    }

    val tmdbConfig by lazy { apiAccess.configuration ?: TODO("Init exception")}

    fun find(imdbId: String): Show? {
        apiAccess.find.find(imdbId, TmdbFind.ExternalSource.imdb_id, "en").let {
            return it.tvResults?.firstOrNull()?.let { getTVShow(it)} ?: run {
                it.movieResults?.firstOrNull()?.let { getMovie(it) }
            }
        }
    }

    fun search(query: String, page: Int = 0): SearchRB {
        val searchResult = apiAccess.search.searchMulti(query, "en", page)
        val showList = searchResult.results.mapNotNull {
            when (it) {
                is TvSeries -> getTVShow(it)
                is MovieDb -> getMovie(it)
                else -> null
            }
        }
        return SearchRB(
                showList,
                searchResult.totalResults,
                page,
                searchResult.totalPages
        )
    }

    fun findByName(name: String): Show? {
        apiAccess.search.searchMulti(name, "en", 0).results?.firstOrNull()?.let {
            return when (it) {
                is TvSeries -> getTVShow(it)
                is MovieDb -> getMovie(it)
                else -> null
            }
        }
        return null
    }

    private fun getTVShow(tvShow: TvSeries): Show? {
        val item = apiAccess.tvSeries.getSeries(tvShow.id, "en", TmdbTV.TvMethod.external_ids)
        if (item.externalIds.imdbId == null) return null
        return Show(
                imdbId = item.externalIds.imdbId,
                name = item.name ?: item.originalName,
                sinopsis = item.overview,
                imgPoster = tmdbConfig.baseUrl + tmdbConfig.posterSizes?.last() + item.posterPath,
                imgBackground = tmdbConfig.baseUrl + tmdbConfig.backdropSizes?.last() + item.backdropPath,
                path = "",
                externalIds = ExternalIds(imdb = item.externalIds?.imdbId,
                        tmdb = item.id,
                        tvdb = item.externalIds?.tvdbId?.toInt())
        )
    }

    private fun getMovie(movie: MovieDb): Show? {
        val item = apiAccess.movies.getMovie(movie.id, "en")
        if (item.imdbID == null) return null
        return Show(
                imdbId = item.imdbID,
                name = item.originalTitle ?: item.title,
                sinopsis = item.overview,
                imgPoster = tmdbConfig.baseUrl + tmdbConfig.posterSizes?.last() + item.posterPath,
                imgBackground = tmdbConfig.baseUrl + tmdbConfig.backdropSizes?.last() + item.backdropPath,
                path = "",
                externalIds = ExternalIds(imdb = item.imdbID,
                        tmdb = item.id)
        )
    }
}