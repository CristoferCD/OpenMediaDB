import io.kotlintest.matchers.collections.shouldNotBeEmpty
import io.kotlintest.specs.StringSpec

class SearchDownloadTest : StringSpec({
    "test" {
        val result = SubtitleManager.search("the rookie", 2, 10)
        println(result)
        val arrays = result.mapNotNull { SubtitleManager.get(it) }
        arrays.shouldNotBeEmpty()
    }
})