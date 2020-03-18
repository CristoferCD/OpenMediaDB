package data.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime

internal object VideoTokenTable : IntIdTable("VideoTokens") {
    val fileId = reference("fileId", FileInfoTable)
    val token = varchar("token", 128).uniqueIndex()
    val expires = datetime("expires")
}