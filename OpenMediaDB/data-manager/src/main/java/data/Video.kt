package data

data class Video (
        val id: Int,
        var fileId: String?,
        val imdbId: String,
        var name: String,
        var season: Int,
        var episodeNumber: Int,
        var sinopsis: String,
        var imgPoster: String?
)