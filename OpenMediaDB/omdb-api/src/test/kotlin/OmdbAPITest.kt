import data.ImdbTitle
import data.ResultType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OmdbAPITest {

    @Test
    fun byId() {
        val result = OmdbAPI.getById("tt0120737")
        val expected = ImdbTitle(genre = "Adventure, Drama, Fantasy",
                responseSuccessful = "True",
                year = "2001",
                imgPoster = "https://m.media-amazon.com/images/M/MV5BN2EyZjM3NzUtNWUzMi00MTgxLWI0NTctMzY4M2VlOTdjZWRiXkEyXkFqcGdeQXVyNDUzOTQ5MjY@._V1_SX300.jpg",
                duration = "178 min",
                title = "The Lord of the Rings: The Fellowship of the Ring",
                imdbId = "tt0120737",
                type = "movie",
                rating = "8.8")
        assertEquals(expected, result)
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