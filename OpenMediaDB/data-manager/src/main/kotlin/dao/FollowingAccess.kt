package dao

import data.tables.FollowingTable
import data.tables.ShowTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

internal class FollowingDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FollowingDao>(FollowingTable)

    var user by UserDao referencedOn FollowingTable.userId
    var show by ShowDao referencedOn FollowingTable.showId
    var following by FollowingTable.following
}