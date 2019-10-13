import io.kotlintest.matchers.collections.shouldNotBeEmpty
import io.kotlintest.specs.StringSpec

class SearchDownloadTest : StringSpec({
    "test" {
        val result = SubtitleManager.search("game of thrones", 8, 6)
        println(result)
        val path = SubtitleManager.download(result.first())
        println("Downloaded subtitle to $path")
        path.shouldNotBeEmpty()
    }
})