import data.Subtitle
import mu.KotlinLogging
import org.jsoup.Jsoup
import org.springframework.util.MimeType
import org.springframework.util.MimeTypeUtils
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


object SubtitleManager {
    private val log = KotlinLogging.logger {}
    private const val baseUrl = "https://www.subdivx.com"

    fun search(show: String, season: Int?, episode: Int?): List<Subtitle> {
        var episodeStr = ""
        if (season != null && episode != null) {
            episodeStr = "S" + if (season < 10) "0$season" else season
            episodeStr += "E" + if (episode < 10) "0$episode" else episode
        }

        val url = "$baseUrl/index.php?buscar=$show $episodeStr&accion=5&masdesc=&subtitulos=1&realiza_b=1"

        log.debug { "Searching subtitle in: $url" }

        val doc = Jsoup.connect(url).get()

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

    fun download(subtitle: Subtitle): List<Path> {
        val doc = Jsoup.connect(subtitle.url).get()
        val link = doc.select("#detalle_datos_derecha > .detalle_link")?.map { it.attr("href") }?.firstOrNull { it.contains("bajar.php") }
        println("Link: $link")
        val file = manageDownload(URL("$baseUrl/$link"))
        return unzip(file)
    }

    private fun manageDownload(url: URL): Path {
        var connection = url.openConnection() as HttpURLConnection

        if (connection.responseCode in listOf(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_SEE_OTHER)) {
            val redirect = connection.getHeaderField("Location")
            connection = URL(redirect).openConnection() as HttpURLConnection
        }

        val extension = when(connection.contentType) {
            "application/zip" -> ".zip"
            "application/x-rar-compressed" -> ".rar"
            else -> ""
        }
        val file = createTempFile("sub", extension).toPath()
        Files.copy(connection.inputStream, file, StandardCopyOption.REPLACE_EXISTING)
        return file
    }

    private fun unzip(path: Path): List<Path> {
        val extractedFiles = mutableListOf<Path>()
        val destDir = Files.createTempDirectory("omediadb-subs")
        val zis = ZipInputStream(FileInputStream(path.toFile()))
        var zipEntry: ZipEntry? = zis.nextEntry
        while (zipEntry != null) {
            val newFile = File(destDir.toFile(), zipEntry.name)
            Files.copy(zis, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            zipEntry = zis.nextEntry
            extractedFiles.add(newFile.toPath())
        }
        zis.closeEntry()
        zis.close()
        return extractedFiles
    }
}