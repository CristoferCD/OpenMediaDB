package data.tmdb

import data.ExternalIds
import data.Show
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

    fun find(imdbId: String): Show? {
        apiAccess.find.find(imdbId, TmdbFind.ExternalSource.imdb_id, "en").let {
            return it.tvResults?.firstOrNull()?.let { getTVShow(it)} ?: run {
                it.movieResults?.firstOrNull()?.let { getMovie(it) }
            }
        }
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

    private fun getTVShow(tvShow: TvSeries): Show {
        val item = apiAccess.tvSeries.getSeries(tvShow.id, "en", TmdbTV.TvMethod.external_ids)
        return Show(
                imdbId = item.externalIds.imdbId,
                name = item.name ?: item.originalName,
                sinopsis = item.overview,
                imgPoster = item.posterPath,
                imgBackground = item.backdropPath,
                path = "",
                externalIds = ExternalIds(imdb = item.externalIds?.imdbId,
                        tmdb = item.id,
                        tvdb = item.externalIds?.tvdbId?.toInt())
        )
    }

    private fun getMovie(movie: MovieDb): Show {
        val item = apiAccess.movies.getMovie(movie.id, "en")
        return Show(
                imdbId = item.imdbID,
                name = item.originalTitle ?: item.title,
                sinopsis = item.overview,
                imgPoster = item.posterPath,
                imgBackground = item.backdropPath,
                path = "",
                externalIds = ExternalIds(imdb = item.imdbID,
                        tmdb = item.id)
        )
    }
}