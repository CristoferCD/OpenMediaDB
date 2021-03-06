package data.tables

import org.jetbrains.exposed.dao.IntIdTable

internal object NotificationTable : IntIdTable("NotificationQueue") {
    val userId = reference("userId", UserTable)
    val content = text("content")
    val read = bool("read").default(false)
}