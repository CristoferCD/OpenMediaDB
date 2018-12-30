package data

import com.google.gson.annotations.SerializedName

data class ImdbTitle (
        @SerializedName("Title")
        val title: String,

        @SerializedName("Year")
        var year: String = "",

        @SerializedName("Runtime")
        var duration: String = "",

        @SerializedName("Genre")
        var genre: String = "",

        @SerializedName("Poster")
        var imgPoster: String = "",

        @SerializedName("imdbRating")
        var rating: String = "",

        @SerializedName("imdbID")
        val imdbId: String,

        @SerializedName("Type")
        val type: String,

        @SerializedName("Response")
        var responseSuccessful: String = "",

        @SerializedName("Error")
        var errorMsg: String = ""
)