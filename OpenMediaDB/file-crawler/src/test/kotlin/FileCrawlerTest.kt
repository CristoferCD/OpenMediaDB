import org.junit.jupiter.api.Test
import java.io.File

internal class FileCrawlerTest {

    @Test
    fun walkTest() {
        var test = FileCrawler()
        test.walkTest()
    }

    @Test
    fun importTest() {
        var crawler = FileCrawler()
        val result = crawler.importLibrary(File("\\\\ORANGEPIZERO\\opiserver\\Anime\\Rascal Does Not Dream of Bunny Girl Senpai"))
        println(result.successfulImports)
        println(result.failedImports)
    }

    @Test
    fun parserTest() {
//        val file = File.createTempFile("KonoSuba 2x01", ".mp4")
        val file = File.createTempFile("KonoSuba - God's Blessing on This Wonderful World! 2x01", ".mp4")
        val crawler = FileCrawler()
        println(crawler.parseFileInfo(file))
    }
}