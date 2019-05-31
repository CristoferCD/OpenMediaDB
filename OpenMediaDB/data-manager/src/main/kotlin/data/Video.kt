package data

import java.time.LocalDate

data class Video (
        val id: Int?,
        var fileId: Int?,
        val showId: String,
        var imdbId: String?,
        var name: String,
        var season: Int,
        var episodeNumber: Int,
        var seen: Boolean? = null,
        var airDate: LocalDate = LocalDate.of(1970, 1, 1),
        var sinopsis: String,
        var imgPoster: String?,
        var externalIds: ExternalIds
)