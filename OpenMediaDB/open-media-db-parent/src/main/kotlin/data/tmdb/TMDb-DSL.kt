package data.tmdb

import info.movito.themoviedbapi.TmdbApi
import java.util.*

class TMDb {
    private val CONFIG_FILE = "tmdb-config.properties"
    private val properties = Properties()
    var apikey: String = ""
    var languageCode: String = ""

    init {
        this.javaClass.getResourceAsStream(CONFIG_FILE).use {
            properties.load(it)
            apikey = properties.getProperty("apikey") ?: throw Exception("API key not set")
            languageCode = properties.getProperty("default.language") ?: "en"
        }
    }
}

fun TMDbFactory(body: TMDbConfig.() -> Unit) = body

class TMDbConfig {
    var tmdbApi: TMDb = TMDb()
    var api: TmdbApi? = null

    fun create() {
        api = TmdbApi(tmdbApi.apikey)
    }

    fun getByID(key: Int) {
        api?.tvSeries?.getSeries(key, tmdbApi.languageCode)
    }
}