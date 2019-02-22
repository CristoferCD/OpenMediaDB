package dao

import data.ExternalIds
import data.Show
import data.Video
import org.junit.Test

import org.junit.Assert.*

class VideoDaoTest {

    @Test
    fun get() {
        DataManagerFactory.showDao.insert(Show(
                imdbId = "tt0944947",
                name = "Game of Thrones",
                sinopsis = "Seven noble families fight for control of the mythical land of Westeros. Friction between the houses leads to full-scale war. All while a very ancient evil awakens in the farthest north. Amidst the war, a neglected military order of misfits, the Night's Watch, is all that stands between the realms of men and icy horrors beyond.",
                path = "",
                externalIds = ExternalIds()
        ))
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
                        externalIds = ExternalIds()
        ))
        val video = DataManagerFactory.videoDao.get("tt2178788")
        assert(video!!.imdbId == "tt2178788")
    }
}