package data.tables

import org.jetbrains.exposed.dao.IdTable

internal object ShowTable: IdTable<String>("Show") {
    override val id = varchar("showId", 15).primaryKey().entityId()
    val name = varchar("name", 255)
    val sinopsis = text("sinopsis")
    val imgPoster = text("imgPoster").nullable()
    val imgBackground = text("imgBackground").nullable()
    val path = text("path")
}