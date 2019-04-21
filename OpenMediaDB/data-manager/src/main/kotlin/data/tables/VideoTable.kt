package data.tables

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.joda.time.DateTime
import org.joda.time.LocalDate

internal object VideoTable : IntIdTable("Video") {
    val fileId = reference("fileId", FileInfoTable).nullable()
    val showId = reference("showId", ShowTable)
    val imdbId = varchar("imdbId", 15).nullable().uniqueIndex()
    val name = varchar("name", 255)
    val season = integer("season")
    val airDate = date("airDate").default(DateTime(1970, 1, 1, 0, 0, 0))
    val episodeNumber = integer("episodeNumber")
    val sinopsis = text("sinopsis")
    val imgPoster = text("imgPoster").nullable()
    val externalIds = reference("externalIds", ExternalIdsTable,
            onUpdate = ReferenceOption.CASCADE, onDelete = ReferenceOption.SET_NULL)
}