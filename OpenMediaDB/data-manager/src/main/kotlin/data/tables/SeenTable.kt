package data.tables

import org.jetbrains.exposed.dao.IntIdTable

internal object SeenTable: IntIdTable("Seen") {
    val userId = reference("userId", UserTable)
    val videoId = reference("videoId", VideoTable)
    var seen = bool("seen").default(false)
}