import data.ResultType
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class OmdbAPITest: StringSpec({
    "byId" {
        val result = OmdbAPI.getById("tt0120737")
        "The Lord of the Rings: The Fellowship of the Ring" shouldBe result.title
        "tt0120737" shouldBe result.imdbId
    }
    "byIdError" {
        val result = OmdbAPI.getById("wrong_id")
        result.responseSuccessful shouldBe "False"
    }
    "search" {
        val result = OmdbAPI.search("the flash")
        result.results.any { it.title == "The Flash" }.shouldBeTrue()
    }
    "getEpisode" {
        val result = OmdbAPI.getByTitle("the flash", ResultType.EPISODE, season = 1, episode = 1)
        println(result)
    }
})