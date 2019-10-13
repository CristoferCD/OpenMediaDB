package dao

import data.*
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import util.DatabaseContainerManager
import java.nio.file.Paths

class VideoDaoTest : BehaviorSpec({
    val fact = DatabaseContainerManager.dataManagerFactory
    Given("Show in db") {
        fact.showDao.insert(Show(
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
        fact.videoDao.insert(Video(
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
        val userId = fact.userDao.insert(User(null, "test", "test"))
        When("retrieve the video by id") {
            val video = fact.videoDao.get("tt2178788")
            Then("it should be the one inserted") {
                video!!.imdbId shouldBe "tt2178788"
            }
        }
        When("assign a file to the video") {
            val video = fact.videoDao.get("tt2178788")
            val fileId = fact.fileInfoDao.insert(FileInfo(
                    id = null,
                    path = Paths.get("/test"),
                    resolution = "",
                    bitrate = "",
                    codec = "",
                    duration = 22
            ))
            video!!.fileId = fileId
            fact.videoDao.update(video)
            Then("it should be updated") {
                fact.videoDao.get("tt2178788")?.fileId shouldBe fileId
            }
        }
        When("change video watched") {
            val videoId = fact.videoDao.get("tt2178788")?.id!!
            val video = fact.videoDao.get(videoId, userId)!!
            fact.videoDao.markWatched(!video.seen!!, userId, video.id!!)
            Then("it should be changed") {
                val updatedVideo = fact.videoDao.get(videoId, userId)
                !video.seen!! shouldBe updatedVideo!!.seen
            }
        }
    }
})