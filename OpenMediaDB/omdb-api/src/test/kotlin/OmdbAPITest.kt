import data.ImdbTitle
import data.ResultType
import org.junit.Test
import kotlin.test.assertEquals

class OmdbAPITest {

    @Test
    fun byId() {
        val result = OmdbAPI.getById("tt0120737")
        assertEquals("The Lord of the Rings: The Fellowship of the Ring", result.title)
        assertEquals("tt0120737", result.imdbId)
    }

    @Test
    fun byIdError() {
        val result = OmdbAPI.getById("wrong_id")
        assert(result.responseSuccessful == "False")
    }

    @Test
    fun search() {
        val result = OmdbAPI.search("the flash")
        assert(result.results.any { it.title == "The Flash" })
    }

    @Test
    fun getEpisode() {
        val result = OmdbAPI.getByTitle("the flash", ResultType.EPISODE, season = 1, episode = 1)
        println(result)
    }

}