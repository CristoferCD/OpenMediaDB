package dao

import data.tables.SeenTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

internal class SeenDao(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<SeenDao>(SeenTable)

    var user by UserDao referencedOn SeenTable.userId
    var video by VideoDao referencedOn SeenTable.videoId
    var seen by SeenTable.seen
}