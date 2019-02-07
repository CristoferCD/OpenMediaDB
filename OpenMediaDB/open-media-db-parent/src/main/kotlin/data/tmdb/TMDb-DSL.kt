package data.tmdb

import info.movito.themoviedbapi.TmdbApi
import java.util.*

//TODO: return custom wrapper
class TMDbBuilder {
    private var configFile = "tmdb-config.properties"
    private val properties = Properties()
    private var apikey: String? = null
    private var languageCode = "en"

    init {
        this.javaClass.getResourceAsStream(configFile).use {
            properties.load(it)
            apikey = properties.getProperty("apikey") ?: throw TMDbNotInitializedException("API key not set")
            languageCode = properties.getProperty("default.language") ?: "en"
        }
    }

    fun create(): TmdbApi {
        return TmdbApi(apikey)
    }

    fun apikey(key: String) {
        apikey = key
    }

    fun configFile(path: String) {
        configFile = path
    }

    fun defaultLanguage(code: String) {
        languageCode = code
    }
}

class TMDbNotInitializedException(msg: String) : Exception(msg)

fun tmdbApi(setup: TMDbBuilder.() -> Unit): TmdbApi {
    val builder = TMDbBuilder()
    builder.setup()
    return builder.create()
}