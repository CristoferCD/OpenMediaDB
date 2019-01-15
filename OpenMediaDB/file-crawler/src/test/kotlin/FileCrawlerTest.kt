import org.junit.jupiter.api.Test
import java.io.File

internal class FileCrawlerTest {

    @Test
    fun walkTest() {
        var test = FileCrawler("")
        test.walkTest()
    }

    @Test
    fun importTest() {
        var crawler = FileCrawler("\\\\ORANGEPIZERO\\opiserver\\Series")
        val result = crawler.importLibrary(File("F:\\crist\\Downloads\\Apartamento 23"))
        println(result.successfulImports)
        println(result.failedImports)
    }
}