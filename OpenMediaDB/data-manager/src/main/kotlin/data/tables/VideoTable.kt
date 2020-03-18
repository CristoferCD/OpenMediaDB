package data.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.`java-time`.date
import java.time.LocalDate

internal object VideoTable : IntIdTable("Video") {
    val fileId = reference("fileId", FileInfoTable).nullable()
    val showId = reference("showId", ShowTable)
    val imdbId = varchar("imdbId", 15).nullable().uniqueIndex()
    val name = varchar("name", 255)
    val season = integer("season")
    val airDate = date("airDate").default(LocalDate.MIN)
    val episodeNumber = integer("episodeNumber")
    val sinopsis = text("sinopsis")
    val imgPoster = text("imgPoster").nullable()
    val externalIds = reference("externalIds", ExternalIdsTable,
            onUpdate = ReferenceOption.CASCADE, onDelete = ReferenceOption.CASCADE)
}