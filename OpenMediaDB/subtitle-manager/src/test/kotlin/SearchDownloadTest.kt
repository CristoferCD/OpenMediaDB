import org.junit.Test

class SearchDownloadTest {
    @Test
    fun test() {
        val result = SubtitleManager.search("game of thrones", 8, 6)
        println(result)
        val path = SubtitleManager.download(result.first())
        println("Downloaded subtitle to $path")
    }
}