package providers

import data.Subtitle
import download.DownloadManager
import mu.KotlinLogging
import org.jsoup.Jsoup
import java.net.URL

class SubdivxSubtitleProvider : SubtitleProvider {
    override val id: SubtitleProviderId = SubtitleProviderId.SUBDIVX
    private val log = KotlinLogging.logger {}
    private val baseUrl = "https://www.subdivx.com/index.php"

    override fun search(name: String): List<Subtitle> {
        val url = "$baseUrl?buscar=$name&${getCommonParams()}"
        return findSubtitlesFromSearchResults(url)
    }

    override fun search(name: String, season: Int, episode: Int): List<Subtitle> {
        val episodeStr = "S${season.toString().padStart(2, '0')}E${episode.toString().padStart(2, '0')}"
        val url = "$baseUrl?buscar=$name $episodeStr&${getCommonParams()}"
        return findSubtitlesFromSearchResults(url)
    }

    private fun findSubtitlesFromSearchResults(url: String): List<Subtitle> {
        log.debug { "Searching subtitle in $url" }
        val doc = Jsoup.connect(url).get()
        val subtitles = mutableListOf<Subtitle>()

        val currentSubtitle = Subtitle(id)
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

    private fun getCommonParams() = "accion=5&masdesc=&subtitulos=1&realiza_b=1"

    override fun get(subtitle: Subtitle): ByteArray? {
        log.debug { "Requested download of subtitle $subtitle" }
        val doc = Jsoup.connect(subtitle.url).get()
        val link = doc.select("#detalle_datos a.link1")?.map { it.attr("href") }?.firstOrNull { it.contains("bajar.php") }
        println("Link: $link")
        val downloadedFiles = DownloadManager.manageDownload(URL(link))
        return downloadedFiles.firstOrNull()?.toFile()?.readBytes()
    }


}