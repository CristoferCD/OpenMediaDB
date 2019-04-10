package dao

import data.ExternalIds
import data.Video
import data.tables.*
import exceptions.ExistingEntityException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class VideoDao(override val dbConnection: Database) : IBaseDao<Video, Int> {
    override fun get(key: Int): Video? {
        var video: Video? = null
        transaction(dbConnection) {
            (VideoTable innerJoin ExternalIdsTable)
                    .select { VideoTable.id eq key }
                    .limit(1).firstOrNull()?.let {
                        video = toVideo(it)
                    }
        }
        return video
    }

    fun get(imdbId: String): Video? {
        var video: Video? = null
        transaction(dbConnection) {
            (VideoTable innerJoin ExternalIdsTable)
                    .select { VideoTable.imdbId eq imdbId }
                    .firstOrNull()?.let { video = toVideo(it) }
        }
        return video
    }

    override fun getAll(): List<Video> {
        val videos = mutableListOf<Video>()
        transaction(dbConnection) {
            (VideoTable innerJoin ExternalIdsTable).selectAll()
                    .forEach { videos.add(toVideo(it)) }
        }
        return videos
    }

    override fun insert(obj: Video): Int {
        var videoId = 0
        try {
            transaction(dbConnection) {
                val extId = ExternalIdsTable.insertAndGetId {
                    it[imdbId] = obj.imdbId
                    it[tmdbId] = obj.externalIds.tmdb
                    it[traktId] = obj.externalIds.trakt
                    it[tvdbId] = obj.externalIds.tvdb
                }
                videoId = VideoTable.insertAndGetId {
                    it[showId] = ShowTable.select { ShowTable.id eq obj.showId }.limit(1).first()[ShowTable.id]
                    it[imdbId] = obj.imdbId
                    it[fileId] = if (obj.fileId != null)
                        FileInfoTable.select { FileInfoTable.id eq obj.fileId }.limit(1).first()[FileInfoTable.id]
                        else null
                    it[name] = obj.name
                    it[season] = obj.season
                    it[episodeNumber] = obj.episodeNumber
                    it[sinopsis] = obj.sinopsis
                    it[imgPoster] = obj.imgPoster
                    it[externalIds] = extId
                }.value
            }
        } catch (e: ExposedSQLException) {
            if (e.toString().contains(Regex("\\[SQLITE_CONSTRAINT\\].*UNIQUE")))
                throw ExistingEntityException("VideoTable", videoId.toString(), e)
            else throw e
        }
        return videoId
    }

    override fun update(obj: Video) {
        transaction(dbConnection) {
            VideoTable.update({ VideoTable.id eq obj.id }) {
                it[fileId] = FileInfoTable.select { FileInfoTable.id eq obj.fileId }.firstOrNull()?.get(FileInfoTable.id)
                it[imdbId] = obj.imdbId
                it[name] = obj.name
                it[season] = obj.season
                it[episodeNumber] = obj.episodeNumber
                it[sinopsis] = obj.sinopsis
                it[imgPoster] = obj.imgPoster
            }
            ExternalIdsTable.update({ ExternalIdsTable.id eq obj.externalIds.id }) {
                it[imdbId] = obj.externalIds.imdb
                it[tmdbId] = obj.externalIds.tmdb
                it[traktId] = obj.externalIds.trakt
                it[tvdbId] = obj.externalIds.tvdb
            }
        }
    }

    fun markWatched(watched: Boolean, userId: Int, videoId: Int) {
        transaction(dbConnection) {
            val existingEntry = SeenTable.select { (SeenTable.userId eq userId) and (SeenTable.videoId eq videoId) }.firstOrNull()
            if (existingEntry == null) {
                SeenTable.insert {
                    it[SeenTable.userId] = UserTable.select { UserTable.id eq userId }.first()[UserTable.id]
                    it[SeenTable.videoId] = VideoTable.select { VideoTable.id eq videoId }.first()[VideoTable.id]
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

    fun findFromParent(showId: String, season: Int? = null, episode: Int? = null, userId: Int?): List<Video> {
        val found = mutableListOf<Video>()
        transaction(dbConnection) {
            val query = (VideoTable innerJoin ExternalIdsTable innerJoin SeenTable).select { VideoTable.showId eq showId }
            season?.let {
                query.andWhere { VideoTable.season eq season }
            }
            episode?.let {
                query.andWhere { VideoTable.episodeNumber eq episode }
            }
            userId?.let {
                query.andWhere { SeenTable.userId eq userId }
            }
            query.forEach {
                found.add(toVideo(it))
            }
        }
        return found
    }

    private fun toVideo(data: ResultRow): Video {
        return Video(
                id = data[VideoTable.id].value,
                fileId = data[VideoTable.fileId]?.value,
                showId = data[VideoTable.showId].value,
                imdbId = data[VideoTable.imdbId],
                name = data[VideoTable.name],
                seen = data.tryGet(SeenTable.seen) ?: false,
                season = data[VideoTable.season],
                episodeNumber = data[VideoTable.episodeNumber],
                sinopsis = data[VideoTable.sinopsis],
                imgPoster = data[VideoTable.imgPoster],
                externalIds = toExternalIds(data)
        )
    }

    private fun toExternalIds(data: ResultRow): ExternalIds {
        return ExternalIds(
                id = data[ExternalIdsTable.id].value,
                imdb = data[ExternalIdsTable.imdbId],
                trakt = data[ExternalIdsTable.traktId],
                tmdb = data[ExternalIdsTable.tmdbId],
                tvdb = data[ExternalIdsTable.tvdbId]
        )
    }

}