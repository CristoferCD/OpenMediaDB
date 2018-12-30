package data

import com.beust.klaxon.Json

data class ImdbTitle (
        @Json(name = "Title")
        val title: String,

        @Json(name = "Year")
        val year: String,

        @Json(name = "Runtime")
        val duration: String,

        @Json(name = "Genre")
        val genre: String,

        @Json(name = "Poster")
        val imgPoster: String,

        @Json(name = "imdbRating")
        val rating: String,

        @Json(name = "imdbID")
        val imdbId: String,

        @Json(name = "Type")
        val type: String,

        @Json(name = "Response")
        val responseError: Boolean,

        @Json(name = "Error")
        val errorMsg: String?
)