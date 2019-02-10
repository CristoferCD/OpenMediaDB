package data.tables

import org.jetbrains.exposed.dao.IntIdTable

internal object VideoTable: IntIdTable("Video") {
    val fileId = reference("fileId", FileInfoTable).nullable()
    val showId = reference("showId", ShowTable.id)
    val imdbId = varchar("imdbId", 15).nullable().uniqueIndex()
    val name = varchar("name", 255)
    val season = integer("season")
    val episodeNumber = integer("episodeNumber")
    val sinopsis = text("sinopsis")
    val imgPoster = text("imgPoster").nullable()
}