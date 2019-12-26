package dao

import data.Video
import data.tables.ExternalIdsTable
import data.tables.SeenTable
import data.tables.VideoTable
import exceptions.ExistingEntityException
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.time.ZoneId

internal class VideoDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<VideoDao>(VideoTable)

    var file by FileInfoDao optionalReferencedOn VideoTable.fileId
    var show by ShowDao referencedOn VideoTable.showId
    var imdbId by VideoTable.imdbId
    var name by VideoTable.name
    var season by VideoTable.season
    var airDate by VideoTable.airDate
    var episodeNumber by VideoTable.episodeNumber
    var sinopsis by VideoTable.sinopsis
    var imgPoster by VideoTable.imgPoster
    var externalIds by ExternalIdsDao referencedOn VideoTable.externalIds
    val seen by SeenDao referrersOn SeenTable.videoId

    fun toDataClass() = Video(
            id = id.value,
            fileId = file?.id?.value,
            showId = show.id.value,
            imdbId = imdbId,
            name = name,
            season = season,
            airDate = airDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            episodeNumber = episodeNumber,
            sinopsis = sinopsis,
            seen = seen.firstOrNull()?.seen,
            imgPoster = imgPoster,
            externalIds = externalIds.toDataClass()
    )
}

class VideoManager(override val dbConnection: Database) : IBaseManager<Video, Int> {
    override fun get(key: Int) = transaction(dbConnection) {
        VideoDao.findById(key)?.toDataClass()
    }

    fun get(key: Int, userId: Int) = transaction(dbConnection) {
        val video = VideoDao.findById(key)
        val seen = video?.seen?.find { it.user.id.value == userId }?.seen ?: false
        video?.toDataClass()?.copy(seen = seen)
    }

    fun get(imdbId: String) = transaction(dbConnection) {
        VideoDao.find { VideoTable.imdbId eq imdbId }.firstOrNull()?.toDataClass()
    }

    override fun getAll() = transaction(dbConnection) {
        VideoDao.all().map(VideoDao::toDataClass)
    }

    override fun insert(obj: Video): Int {
        return transaction(dbConnection) {
            try {
                val extId = ExternalIdsDao.new {
                    imdbId = obj.imdbId
                    tmdbId = obj.externalIds.tmdb
                    traktId = obj.externalIds.trakt
                    tvdbId = obj.externalIds.tvdb
                }
                VideoDao.new {
                    show = ShowDao[obj.showId]
                    imdbId = obj.imdbId
                    file = if (obj.fileId != null) FileInfoDao[obj.fileId as Int] else null
                    name = obj.name
                    season = obj.season
                    airDate = DateTime(obj.airDate.year, obj.airDate.monthValue, obj.airDate.dayOfMonth, 0, 0)
                    episodeNumber = obj.episodeNumber
                    sinopsis = obj.sinopsis
                    imgPoster = obj.imgPoster
                    externalIds = extId
                }.id.value
            } catch (e: ExposedSQLException) {
                if (e.message?.contains("Duplicate entry") == true)
                    throw ExistingEntityException("VideoTable", obj.imdbId ?: "null", e)
                else throw e
            }
        }
    }

    override fun update(obj: Video) {
        transaction(dbConnection) {
            with(VideoDao[obj.id!!]) {
                file = if (obj.fileId != null) FileInfoDao[obj.fileId!!] else null
                imdbId = obj.imdbId
                name = obj.name
                season = obj.season
                airDate = DateTime(obj.airDate.year, obj.airDate.monthValue, obj.airDate.dayOfMonth, 0, 0)
                episodeNumber = obj.episodeNumber
                sinopsis = obj.sinopsis
                imgPoster = obj.imgPoster
                externalIds.imdbId = obj.externalIds.imdb
                externalIds.tmdbId = obj.externalIds.tmdb
                externalIds.traktId = obj.externalIds.trakt
                externalIds.tvdbId = obj.externalIds.tvdb
            }
        }
    }

    fun markWatched(watched: Boolean, userId: Int, videoId: Int) {
        transaction(dbConnection) {
            val existingEntry = SeenDao.find { (SeenTable.userId eq userId) and (SeenTable.videoId eq videoId) }.firstOrNull()
            if (existingEntry != null) {
                existingEntry.seen = watched
            } else {
                SeenDao.new {
                    user = UserDao[userId]
                    video = VideoDao[videoId]
                    seen = watched
                }
            }
        }
    }

    override fun delete(key: Int) {
        transaction(dbConnection) {
            val video = VideoDao[key]
            video.file?.delete()
            video.delete()
        }
    }

    fun findFromParent(showId: String, season: Int? = null, episode: Int? = null, userId: Int? = null): List<Video> {
        return transaction(dbConnection) {
            val query = (VideoTable innerJoin ExternalIdsTable).select { VideoTable.showId eq showId }
            season?.let {
                query.andWhere { VideoTable.season eq season }
            }
            episode?.let {
                query.andWhere { VideoTable.episodeNumber eq episode }
            }
            userId?.let {
                query.adjustColumnSet {
                    join(SeenTable, joinType = JoinType.LEFT,
                            additionalConstraint = { (VideoTable.id eq SeenTable.videoId) and (SeenTable.userId eq userId) })
                }
            }
            query.orderBy(VideoTable.season to SortOrder.ASC, VideoTable.episodeNumber to SortOrder.ASC)
            VideoDao.wrapRows(query).map(VideoDao::toDataClass)
        }
    }
}