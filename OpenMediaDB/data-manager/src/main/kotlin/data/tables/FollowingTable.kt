package data.tables

import org.jetbrains.exposed.dao.id.IntIdTable

internal object FollowingTable : IntIdTable("Following") {
    val userId = reference("userId", UserTable)
    val showId = reference("showId", ShowTable)
    var following = bool("following").default(false)
}