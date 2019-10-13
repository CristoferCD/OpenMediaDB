package data.tables

import org.jetbrains.exposed.dao.IdTable
import org.jetbrains.exposed.sql.ReferenceOption

internal object ShowTable : IdTable<String>("Show") {
    override val id = varchar("showId", 15).primaryKey().entityId()
    val name = varchar("name", 255)
    val sinopsis = text("sinopsis")
    val totalSeasons = integer("totalSeasons").default(0)
    val totalEpisodes = integer("totalEpisodes").default(0)
    val imgPoster = text("imgPoster").nullable()
    val imgBackground = text("imgBackground").nullable()
    val path = text("path")
    val externalIds = reference("externalIds", ExternalIdsTable,
            onUpdate = ReferenceOption.CASCADE, onDelete = ReferenceOption.CASCADE)
}