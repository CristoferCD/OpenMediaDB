package dao

import data.FileInfo
import data.tables.FileInfoTable
import org.jetbrains.exposed.sql.*
import java.io.File

class FileInfoDao : IBaseDao<FileInfo, Int> {
    override fun get(key: Int): FileInfo {
        return toFileInfo(FileInfoTable.select { FileInfoTable.id eq key}.first())
    }

    override fun getAll(): List<FileInfo> {
        val files = mutableListOf<FileInfo>()
        FileInfoTable.selectAll().forEach { files.add(toFileInfo(it)) }
        return files
    }

    override fun insert(obj: FileInfo): Int {
        return FileInfoTable.insertAndGetId {
            it[uri] = obj.uri
            it[duration] = obj.duration
            it[resolution] = obj.resolution
            it[bitrate] = obj.bitrate
            it[codec] = obj.codec
        }.value
    }

    override fun update(obj: FileInfo) {
        FileInfoTable.update({FileInfoTable.id eq obj.id}) {
            it[uri] = obj.uri
            it[duration] = obj.duration
            it[resolution] = obj.resolution
            it[bitrate] = obj.bitrate
            it[codec] = obj.codec
        }
    }

    override fun delete(key: Int) {
        FileInfoTable.deleteWhere { FileInfoTable.id eq key }
    }

    private fun toFileInfo(data: ResultRow): FileInfo {
        return FileInfo(
                id = data[FileInfoTable.id].value,
                uri = data[FileInfoTable.uri],
                duration = data[FileInfoTable.duration],
                resolution = data[FileInfoTable.resolution],
                bitrate = data[FileInfoTable.bitrate],
                codec = data[FileInfoTable.codec]
        )
    }
}