package dao

import data.FileInfo
import data.tables.FileInfoTable
import exceptions.ExistingEntityException
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Paths

internal class FileInfoDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FileInfoDao>(FileInfoTable)

    var uri by FileInfoTable.uri
    var duration by FileInfoTable.duration
    var resolution by FileInfoTable.resolution
    var bitrate by FileInfoTable.bitrate
    var codec by FileInfoTable.codec

    fun toDataClass() = FileInfo(
            id = id.value,
            path = Paths.get(uri),
            duration = duration,
            resolution = resolution,
            bitrate = bitrate,
            codec = codec
    )
}

class FileInfoManager(override val dbConnection: Database) : IBaseManager<FileInfo, Int> {
    override fun get(key: Int) = transaction(dbConnection) {
        FileInfoDao.findById(key)?.toDataClass()
    }

    override fun getAll() = transaction(dbConnection) {
        FileInfoDao.all().map(FileInfoDao::toDataClass)
    }

    override fun insert(obj: FileInfo): Int {
        return transaction(dbConnection) {
            try {
                FileInfoDao.new {
                    uri = obj.path.toString()
                    duration = obj.duration
                    resolution = obj.resolution
                    bitrate = obj.bitrate
                    codec = obj.codec
                }.id.value
            } catch (e: ExposedSQLException) {
                if (e.message?.contains("Duplicate entry") == true)
                    throw ExistingEntityException("FileInfoTable", obj.path.toString(), e)
                else throw e
            }
        }
    }

    override fun update(obj: FileInfo) {
        transaction(dbConnection) {
            with(FileInfoDao[obj.id!!]) {
                uri = obj.path.toString()
                duration = obj.duration
                resolution = obj.resolution
                bitrate = obj.bitrate
                codec = obj.codec
            }
        }
    }

    override fun delete(key: Int) = transaction(dbConnection) {
        FileInfoDao[key].delete()
    }

}