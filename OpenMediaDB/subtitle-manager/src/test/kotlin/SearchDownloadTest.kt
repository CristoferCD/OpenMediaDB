import io.kotlintest.matchers.collections.shouldNotBeEmpty
import io.kotlintest.specs.StringSpec

class SearchDownloadTest : StringSpec({
    "test" {
        val result = SubtitleManager.search("the rookie", 2, 14)
        println(result)
        val arrays = result.mapNotNull { SubtitleManager.get(it) }
        arrays.shouldNotBeEmpty()
    }
})