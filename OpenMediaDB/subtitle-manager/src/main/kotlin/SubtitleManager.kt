import org.jsoup.Jsoup
import java.io.File
import java.io.FileInputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


object SubtitleManager {
    private const val baseUrl = "https://www.subdivx.com"

    fun search(show: String, season: Int?, episode: Int?): List<Subtitle> {
        var episodeStr = ""
        if (season != null && episode != null) {
            episodeStr = "S" + if (season < 10) "0$season" else season
            episodeStr += "E" + if (episode < 10) "0$episode" else episode
        }

        val doc = Jsoup.connect("$baseUrl/index.php?buscar=$show $episodeStr&accion=5&masdesc=&subtitulos=1&realiza_b=1").get()

        val subtitles = mutableListOf<Subtitle>()

        val currentSubtitle = Subtitle()
        doc.select("#menu_titulo_buscador > .titulo_menu_izq, #buscador_detalle > #buscador_detalle_sub")?.forEach {
            if (it.`is`(".titulo_menu_izq")) {
                currentSubtitle.title = it.text()
                currentSubtitle.url = it.attr("href")
            } else {
                currentSubtitle.description = it.text()
                subtitles.add(currentSubtitle.copy())
            }
        }

        return subtitles
    }

    fun download(subtitle: Subtitle): Path {
        val doc = Jsoup.connect(subtitle.url).get()
        val link = doc.select("#detalle_datos .link1")?.first()?.attr("href")
        println("Link: $link")
        val file = Files.createTempFile(subtitle.title, ".zip")
        Files.copy(URL(link).openStream(), file, StandardCopyOption.REPLACE_EXISTING)
        unzip(file)
        return file.toAbsolutePath()
    }

    private fun unzip(path: Path) {
        val destDir = Files.createTempDirectory("omediadb-subs")
        val zis = ZipInputStream(FileInputStream(path.toFile()))
        var zipEntry: ZipEntry? = zis.nextEntry
        while (zipEntry != null) {
            val newFile = File(destDir.toFile(), zipEntry.name)
            Files.copy(zis, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            zipEntry = zis.nextEntry
        }
        zis.closeEntry()
        zis.close()
    }
}

data class Subtitle(var title: String = "", var description: String = "", var url: String = "")