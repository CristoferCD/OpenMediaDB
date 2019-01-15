package data.tables

import org.jetbrains.exposed.dao.IntIdTable

object FileInfoTable: IntIdTable("FileInfo") {
    val uri = text("uri")
    val duration = integer("duration").nullable()
    val resolution = varchar("resolution", 20)
    val bitrate = varchar("bitrate", 20)
    val codec = varchar("codec", 20)
}