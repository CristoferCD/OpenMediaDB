package data.tables

import org.jetbrains.exposed.dao.IntIdTable

internal object VideoTokenTable : IntIdTable("VideoTokens") {
    val fileId = reference("fileId", FileInfoTable)
    val token = varchar("token", 128).uniqueIndex()
    val expires = date("expires")
}