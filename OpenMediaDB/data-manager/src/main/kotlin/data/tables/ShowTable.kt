package data.tables

import org.jetbrains.exposed.sql.Table

object ShowTable: Table("Show") {
    val imdbId = varchar("imdbId", 15).primaryKey()
    val name = varchar("name", 255)
    val imgPoster = text("imgPoster").nullable()
    val imgBackground = text("imgBackground").nullable()
    val path = text("path")
}