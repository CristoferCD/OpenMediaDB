package dao

import data.FileInfo
import data.tables.FileInfoTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Path

class FileInfoDao(override val dbConnection: Database) : IBaseDao<FileInfo, Int> {
    override fun get(key: Int): FileInfo? {
        var fileInfo: FileInfo? = null
        transaction(dbConnection) {
            fileInfo = FileInfoTable.select { FileInfoTable.id eq key }
                    .limit(1).firstOrNull()?.toDataClass()
        }
        return fileInfo
    }

    override fun getAll(): List<FileInfo> {
        var files = listOf<FileInfo>()
        transaction(dbConnection) {
            files = FileInfoTable.selectAll().toDataClass()
        }
        return files
    }

    override fun insert(obj: FileInfo): Int {
        return transaction(dbConnection) {
            FileInfoTable.insertAndGetId {
                it[uri] = obj.path.toString()
                it[duration] = obj.duration
                it[resolution] = obj.resolution
                it[bitrate] = obj.bitrate
                it[codec] = obj.codec
            }.value
        }
    }

    override fun update(obj: FileInfo) {
        transaction(dbConnection) {
            FileInfoTable.update({ FileInfoTable.id eq obj.id }) {
                it[uri] = obj.path.toString()
                it[duration] = obj.duration
                it[resolution] = obj.resolution
                it[bitrate] = obj.bitrate
                it[codec] = obj.codec
            }
        }
    }

    override fun delete(key: Int) {
        transaction(dbConnection) {
            FileInfoTable.deleteWhere { FileInfoTable.id eq key }
        }
    }

    private fun ResultRow.toDataClass() = FileInfo(
            id = this[FileInfoTable.id].value,
            path = Path.of(this[FileInfoTable.uri]),
            duration = this[FileInfoTable.duration],
            resolution = this[FileInfoTable.resolution],
            bitrate = this[FileInfoTable.bitrate],
            codec = this[FileInfoTable.codec]
    )

    private fun Iterable<ResultRow>.toDataClass() = map { it.toDataClass() }

}