package data

import java.time.ZonedDateTime

data class Session(
        val id: String,
        val userId: Int,
        var expires: ZonedDateTime
)