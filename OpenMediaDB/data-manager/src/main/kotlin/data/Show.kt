package data

data class Show (
        val imdbId: String,
        var name: String,
        var sinopsis: String,
        var imgPoster: String? = null,
        var imgBackground: String? = null,
        var path: String
)