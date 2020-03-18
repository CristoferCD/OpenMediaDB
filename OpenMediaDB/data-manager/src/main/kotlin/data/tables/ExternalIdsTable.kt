package data.tables

import org.jetbrains.exposed.dao.id.IntIdTable

internal object ExternalIdsTable : IntIdTable("ExternalIds") {
    val imdbId = varchar("imdbId", 15).nullable()
    val tmdbId = integer("tmdbId").nullable()
    val traktId = integer("traktId").nullable()
    val tvdbId = integer("tvdbId").nullable()
}

