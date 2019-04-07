package data.tables

import org.jetbrains.exposed.dao.IntIdTable

internal object VideoTokenTable : IntIdTable("VideoTokens") {
    val fileId = reference("fileId", FileInfoTable)
    val token = text("token").uniqueIndex()
    val expires = date("expires")
}