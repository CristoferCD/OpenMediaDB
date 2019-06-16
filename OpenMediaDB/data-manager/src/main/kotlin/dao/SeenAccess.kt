package dao

import data.tables.SeenTable
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

internal class SeenDao(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<SeenDao>(SeenTable)

    var user by UserDao referencedOn SeenTable.userId
    var video by VideoDao referencedOn SeenTable.videoId
    var seen by SeenTable.seen
}