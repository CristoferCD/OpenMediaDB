package data.tmdb

object TMDbManager {
    val apiAccess by lazy {
        tmdbApi {
            defaultLanguage("en")
        }
    }
}