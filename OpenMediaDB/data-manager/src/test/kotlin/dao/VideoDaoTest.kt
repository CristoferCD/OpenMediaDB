package dao

import DataManagerFactory
import data.*
import exceptions.ExistingEntityException
import mu.KotlinLogging
import org.junit.Test
import java.nio.file.Files
import kotlin.test.assertEquals

class VideoDaoTest {
    private val log = KotlinLogging.logger {}

    @Test
    fun get() {
        try {
            DataManagerFactory.showDao.insert(Show(
                    imdbId = "tt0944947",
                    name = "Game of Thrones",
                    sinopsis = "Seven noble families fight for control of the mythical land of Westeros. Friction between the houses leads to full-scale war. All while a very ancient evil awakens in the farthest north. Amidst the war, a neglected military order of misfits, the Night's Watch, is all that stands between the realms of men and icy horrors beyond.",
                    totalSeasons = 8,
                    totalEpisodes = 76,
                    path = "",
                    externalIds = ExternalIds(
                            id = null,
                            imdb = "tt0944947",
                            tmdb = 22983
                    )
            ))
        } catch (e: ExistingEntityException) {
            println("Test show already exists ${e.message}")
        } catch (e: Exception) {
            println("dddd")
        }
        try {
            DataManagerFactory.videoDao.insert(Video(
                    id = null,
                    fileId = null,
                    showId = "tt0944947",
                    imdbId = "tt2178788",
                    name = "Test",
                    season = 0,
                    episodeNumber = 0,
                    sinopsis = "",
                    imgPoster = "",
                    externalIds = ExternalIds(
                            id = null,
                            imdb = "tt2178788",
                            trakt = 34
                    )
            ))
        } catch (e: ExistingEntityException) {
            println("Test video already exists ${e.message}")
        }
        val video = DataManagerFactory.videoDao.get("tt2178788")
        assert(video!!.imdbId == "tt2178788")
    }

    @Test
    fun update() {
        val video = DataManagerFactory.videoDao.get(1)
        val testFile = Files.createTempFile("test", "")
        val fileId = DataManagerFactory.fileInfoDao.insert(FileInfo(
                id = null,
                path = testFile,
                resolution = "",
                bitrate = "",
                codec = "",
                duration = 22
        ))
        video!!.fileId = fileId
        DataManagerFactory.videoDao.update(video)
    }

    @Test
    fun markWatched() {
        val user = try {
            val userId = DataManagerFactory.userDao.insert(User(null, "test", "test"))
            DataManagerFactory.userDao.get(userId)
        } catch (e: ExistingEntityException) {
            DataManagerFactory.userDao.getAll().first()
        }

        val videoId = DataManagerFactory.videoDao.getAll().first().id
        val video = DataManagerFactory.videoDao.get(videoId!!, user?.id!!)
        val seen = video?.seen!!
        log.debug { "Previous state of ${video.id} - ${video.name}: $seen" }
        DataManagerFactory.videoDao.markWatched(!seen, user.id!!, video.id!!)
        val updatedVideo = DataManagerFactory.videoDao.get(video.id!!, user.id!!)
        log.debug { "After changing: ${updatedVideo?.seen}" }

        assertEquals(!seen, updatedVideo?.seen, "Seen property not changed")
    }
}