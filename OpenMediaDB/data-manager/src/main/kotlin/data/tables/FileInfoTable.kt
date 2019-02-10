package data.tables

import org.jetbrains.exposed.dao.IntIdTable

internal object FileInfoTable: IntIdTable("FileInfo") {
    val uri = text("path")
    val duration = integer("duration").nullable()
    val resolution = varchar("resolution", 20)
    val bitrate = varchar("bitrate", 20)
    val codec = varchar("codec", 20)
}