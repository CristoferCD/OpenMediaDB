package dao

import data.VideoToken
import data.tables.VideoTokenTable
import exceptions.ExistingEntityException
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.time.Instant
import java.time.ZoneId

internal class VideoTokenDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<VideoTokenDao>(VideoTokenTable)

    var file by FileInfoDao referencedOn VideoTokenTable.fileId
    var token by VideoTokenTable.token
    var expires by VideoTokenTable.expires

    fun toDataClass() = VideoToken(
            id = id.value,
            fileId = file.id.value,
            token = token,
            expires = Instant.ofEpochMilli(expires.millis).atZone(ZoneId.systemDefault())
    )
}

class VideoTokenManager(override val dbConnection: Database) : IBaseManager<VideoToken, Int> {
    override fun get(key: Int) = transaction(dbConnection) {
        VideoTokenDao.findById(key)?.toDataClass()
    }

    fun get(token: String) = transaction(dbConnection) {
        VideoTokenDao.find { VideoTokenTable.token eq token }.firstOrNull()?.toDataClass()
    }

    override fun getAll() = transaction(dbConnection) {
        VideoTokenDao.all().map(VideoTokenDao::toDataClass)
    }

    override fun insert(obj: VideoToken): Int {
        return transaction(dbConnection) {
            try {
                VideoTokenDao.new {
                    file = FileInfoDao[obj.fileId]
                    token = obj.token
                    expires = DateTime(obj.expires.toInstant().toEpochMilli())
                }.id.value
            } catch (e: ExposedSQLException) {
                if (e.message?.contains("Duplicate entry") == true)
                    throw ExistingEntityException("VideoTokenTable", obj.token, e)
                else throw e
            }
        }
    }

    override fun update(obj: VideoToken) {
        transaction(dbConnection) {
            with(VideoTokenDao[obj.id!!]) {
                file = FileInfoDao[obj.fileId]
                token = obj.token
                expires = DateTime(obj.expires.toInstant().toEpochMilli())
            }
        }
    }

    override fun delete(key: Int) = transaction(dbConnection) {
        VideoTokenDao[key].delete()
    }
}