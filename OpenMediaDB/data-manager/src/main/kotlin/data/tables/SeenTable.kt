package data.tables

import org.jetbrains.exposed.dao.IntIdTable

object SeenTable: IntIdTable("Seen") {
    val userId = reference("userId", UserTable)
    val videoId = reference("videoId", VideoTable)
}