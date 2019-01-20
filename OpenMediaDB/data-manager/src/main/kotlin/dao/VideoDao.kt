package dao

import data.Video
import data.tables.FileInfoTable
import data.tables.SeenTable
import data.tables.UserTable
import data.tables.VideoTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class VideoDao : IBaseDao<Video, Int> {
    override fun get(key: Int): Video {
        return toVideo(
                transaction {
                    VideoTable.select { VideoTable.id eq key }
                }.first()
        )
    }

    fun get(imdbId: String): Video {
        return toVideo(
                transaction {
                    VideoTable.select { VideoTable.imdbId eq imdbId }
                }.first()
        )
    }

    override fun getAll(): List<Video> {
        val videos = mutableListOf<Video>()
        transaction { VideoTable.selectAll() }.forEach { videos.add(toVideo(it)) }
        return videos
    }

    override fun insert(obj: Video): Int {
        return transaction {
            VideoTable.insertAndGetId {
                it[showId] = obj.showId
                it[imdbId] = obj.imdbId
                it[name] = obj.name
                it[season] = obj.season
                it[episodeNumber] = obj.episodeNumber
                it[sinopsis] = obj.sinopsis
                it[imgPoster] = obj.imgPoster
            }.value
        }
    }

    override fun update(obj: Video) {
        transaction {
            VideoTable.update({ VideoTable.id eq obj.id }) {
                it[fileId] = FileInfoTable.select { FileInfoTable.id eq obj.fileId }.first()[id]
                it[imdbId] = obj.imdbId
                it[name] = obj.name
                it[season] = obj.season
                it[episodeNumber] = obj.episodeNumber
                it[sinopsis] = obj.sinopsis
                it[imgPoster] = obj.imgPoster
            }
        }
    }

    fun markWatched(watched: Boolean, userId: Int, videoId: Int) {
        transaction {
            val existingEntry = SeenTable.select { (SeenTable.userId eq userId) and (SeenTable.videoId eq videoId) }.firstOrNull()
            if (existingEntry == null) {
                SeenTable.insert {
                    it[SeenTable.userId] = UserTable.select { UserTable.id eq userId }.first()[id]
                    it[SeenTable.videoId] = VideoTable.select { VideoTable.id eq videoId }.first()[id]
                    it[SeenTable.seen] = watched
                }
            } else {
                existingEntry[SeenTable.seen] = watched
            }
        }
    }

    override fun delete(key: Int) {
        transaction {
            val video = VideoTable.select { VideoTable.id eq key }.first()
            VideoTable.deleteWhere { VideoTable.id eq key }
            FileInfoTable.deleteWhere { FileInfoTable.id eq video[VideoTable.fileId]?.value }
        }

    }

    private fun toVideo(data: ResultRow): Video {
        return Video(
                id = data[VideoTable.id].value,
                fileId = data[VideoTable.fileId]?.value,
                showId = data[VideoTable.showId],
                imdbId = data[VideoTable.imdbId],
                name = data[VideoTable.name],
                season = data[VideoTable.season],
                episodeNumber = data[VideoTable.episodeNumber],
                sinopsis = data[VideoTable.sinopsis],
                imgPoster = data[VideoTable.imgPoster]
        )
    }

}