package data.tables

import org.jetbrains.exposed.dao.IntIdTable

internal object FollowingTable: IntIdTable("Following") {
    val userId = reference("userId", UserTable)
    val showId = reference("showId", ShowTable.id)
    var following = bool("following").default(false)
}