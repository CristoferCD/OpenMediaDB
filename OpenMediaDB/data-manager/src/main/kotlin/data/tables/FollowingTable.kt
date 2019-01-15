package data.tables

import org.jetbrains.exposed.dao.IntIdTable

object FollowingTable: IntIdTable("Following") {
    val userId = reference("userId", UserTable)
    val showId = reference("showId", ShowTable.imdbId)
}