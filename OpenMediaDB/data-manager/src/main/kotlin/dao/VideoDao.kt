package dao

import data.Video
import data.tables.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class VideoDao(override val dbConnection: Database) : IBaseDao<Video, Int> {
    override fun get(key: Int): Video? {
        var video: Video? = null
        transaction(dbConnection) {
            VideoTable.select { VideoTable.id eq key }
                    .first().let { video = toVideo(it) }
        }
        return video
    }

    fun get(imdbId: String): Video? {
        var video: Video? = null
        transaction(dbConnection) {
            VideoTable.select { VideoTable.imdbId eq imdbId }
                    .first().let { video = toVideo(it) }
        }
        return video
    }

    override fun getAll(): List<Video> {
        val videos = mutableListOf<Video>()
        transaction(dbConnection) {
            VideoTable.selectAll()
                    .forEach { videos.add(toVideo(it)) }
        }
        return videos
    }

    override fun insert(obj: Video): Int {
        return transaction(dbConnection) {
            VideoTable.insertAndGetId {
                it[showId] = ShowTable.select { ShowTable.id eq obj.showId }.first()[ShowTable.id]
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
        transaction(dbConnection) {
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
        transaction(dbConnection) {
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
        transaction(dbConnection) {
            val video = VideoTable.select { VideoTable.id eq key }.first()
            VideoTable.deleteWhere { VideoTable.id eq key }
            FileInfoTable.deleteWhere { FileInfoTable.id eq video[VideoTable.fileId]?.value }
        }

    }

    fun findFromParent(showId: String, season: Int? = null, episode: Int? = null): List<Video> {
        val found = mutableListOf<Video>()
        transaction(dbConnection) {
            val query = VideoTable.select { VideoTable.showId eq showId }
            season?.let {
                query.andWhere { VideoTable.season eq season }
            }
            episode?.let {
                query.andWhere { VideoTable.episodeNumber eq episode }
            }
            query.forEach {
                found.add(toVideo(it))
            }
        }
        return found
    }

    private fun toVideo(resultRow: ResultRow): Video {
        return Video(
                id = resultRow[VideoTable.id].value,
                fileId = resultRow[VideoTable.fileId]?.value,
                showId = resultRow[VideoTable.showId].value,
                imdbId = resultRow[VideoTable.imdbId],
                name = resultRow[VideoTable.name],
                season = resultRow[VideoTable.season],
                episodeNumber = resultRow[VideoTable.episodeNumber],
                sinopsis = resultRow[VideoTable.sinopsis],
                imgPoster = resultRow[VideoTable.imgPoster]
        )
    }

}