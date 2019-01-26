import data.ResultType
import data.Show
import org.junit.jupiter.api.Test

class ShowCreationTest {
    @Test
    fun createShow() {
        val show = OmdbAPI.getByTitle("The Rookie", ResultType.SERIES)
        DataManagerFactory.showDao.insert(Show(
                imdbId = show.imdbId,
                name = show.title,
                path = "/${show.title} (${show.year})",
                imgPoster = show.imgPoster
        ))
    }
}