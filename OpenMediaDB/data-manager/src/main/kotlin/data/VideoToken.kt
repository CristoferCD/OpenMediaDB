package data

import java.time.ZonedDateTime

data class VideoToken(
        var id: Int? = null,
        val fileId: Int,
        val token: String,
        var expires: ZonedDateTime
)