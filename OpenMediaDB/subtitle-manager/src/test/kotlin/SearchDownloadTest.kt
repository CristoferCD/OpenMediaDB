import data.SubtitleDownloadForm
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotBeEmpty

class SearchDownloadTest : StringSpec({
    "test" {
        val result = SubtitleManager.search("the rookie", 2, 10)
        println(result)
        val arrays = result.mapNotNull { SubtitleManager.get(SubtitleDownloadForm(it.origin, it.url)) }
        arrays.shouldNotBeEmpty()
    }
})