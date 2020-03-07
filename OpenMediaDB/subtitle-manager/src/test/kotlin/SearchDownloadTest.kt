import io.kotlintest.matchers.collections.shouldNotBeEmpty
import io.kotlintest.specs.StringSpec

class SearchDownloadTest : StringSpec({
    "test" {
        val result = SubtitleManager.search("the magicians", 5, 8)
        println(result)
        val path = SubtitleManager.download(result.first())
        println("Downloaded subtitle to $path")
        path.shouldNotBeEmpty()
    }
})