package dao

import data.VideoToken
import data.tables.FileInfoTable
import data.tables.VideoTokenTable
import exceptions.ExistingEntityException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.time.Instant
import java.time.ZoneId

class VideoTokenDao(override val dbConnection: Database) : IBaseDao<VideoToken, Int> {
    override fun get(key: Int): VideoToken? {
        var token: VideoToken? = null
        transaction(dbConnection) {
            token = VideoTokenTable.select { VideoTokenTable.id eq key }
                    .limit(1).firstOrNull()?.toDataClass()
        }
        return token
    }

    fun get(token: String): VideoToken? {
        var entity: VideoToken? = null
        transaction(dbConnection) {
            entity = VideoTokenTable.select { VideoTokenTable.token eq token}
                    .limit(1).firstOrNull()?.toDataClass()
        }
        return entity
    }

    override fun getAll(): List<VideoToken> {
        var tokens = listOf<VideoToken>()
        transaction(dbConnection) {
            tokens = VideoTokenTable.selectAll().toDataClass()
        }
        return tokens
    }

    override fun insert(obj: VideoToken): Int {
        var id = 0
        try {
            transaction(dbConnection) {
                id = VideoTokenTable.insertAndGetId {
                    it[VideoTokenTable.fileId] = FileInfoTable.select { FileInfoTable.id eq obj.fileId }.limit(1).first()[FileInfoTable.id]
                    it[VideoTokenTable.token] = obj.token
                    it[VideoTokenTable.expires] = DateTime(obj.expires.toInstant().toEpochMilli())
                }.value
            }
        } catch (e: ExposedSQLException) {
            if (e.toString().contains(Regex("\\[SQLITE_CONSTRAINT\\].*UNIQUE")))
                throw ExistingEntityException("VideoTokenTable", id.toString(), e)
            else throw e
        }
        return id
    }

    override fun update(obj: VideoToken) {
        transaction(dbConnection) {
            VideoTokenTable.update({ VideoTokenTable.id eq obj.id }) {
                it[fileId] = FileInfoTable.select { FileInfoTable.id eq obj.fileId }.limit(1).first()[FileInfoTable.id]
                it[token] = obj.token
                it[expires] = DateTime(obj.expires.toInstant().toEpochMilli())
            }
        }
    }

    override fun delete(key: Int) {
        transaction(dbConnection) {
            VideoTokenTable.deleteWhere { VideoTokenTable.id eq key }
        }
    }

    private fun ResultRow.toDataClass() = VideoToken(
            id = this[VideoTokenTable.id].value,
            fileId = this[VideoTokenTable.fileId].value,
            token = this[VideoTokenTable.token],
            expires = Instant.ofEpochMilli(this[VideoTokenTable.expires].millis).atZone(ZoneId.systemDefault())
    )

    private fun Iterable<ResultRow>.toDataClass() = map { it.toDataClass() }
}