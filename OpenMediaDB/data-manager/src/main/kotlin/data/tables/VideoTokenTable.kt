package data.tables

import org.jetbrains.exposed.dao.IntIdTable

internal object VideoTokenTable : IntIdTable("VideoTokens") {
    val fileId = reference("id", FileInfoTable)
    val token = text("token")
    val expires = date("expires")
}