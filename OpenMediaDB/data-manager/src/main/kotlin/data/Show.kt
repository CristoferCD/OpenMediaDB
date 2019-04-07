package data

data class Show (
        val imdbId: String,
        var name: String,
        var sinopsis: String,
        var totalSeasons: Int,
        var totalEpisodes: Int,
        var imgPoster: String? = null,
        var imgBackground: String? = null,
        var path: String,
        var externalIds: ExternalIds
)