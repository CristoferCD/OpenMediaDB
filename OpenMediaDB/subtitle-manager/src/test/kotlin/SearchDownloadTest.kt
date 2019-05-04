import org.jsoup.Jsoup
import org.junit.Test

class SearchDownloadTest {
    @Test
    fun test() {
        val doc = Jsoup.connect("https://www.subdivx.com/index.php?buscar=the+magicians&accion=5&masdesc=&subtitulos=1&realiza_b=1").get()

        doc.select("#menu_titulo_buscador > .titulo_menu_izq")?.forEach {
            println(it.text())
        }
    }
}