import data.Show
import data.VideoFileInfo
import exceptions.FileParseException
import info.movito.themoviedbapi.*
import info.movito.themoviedbapi.model.tv.TvSeries
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDate

class ShowCreationTest {
    @Test
    fun createShow() {
        val tmdbApi = TmdbApi("")
        var show = TvSeries()
        val searchResults = tmdbApi.search.searchTv("The Rookie", "en", 0)
        searchResults?.let {
            if (it.totalResults > 0)
                show = it.results?.first()!!
        }

        val fullInfo = tmdbApi.tvSeries.getSeries(show.id, "en", TmdbTV.TvMethod.external_ids)

        println(fullInfo)
        var showDate = LocalDate.parse(fullInfo.firstAirDate)
        DataManagerFactory.showDao.insert(Show(
                imdbId = fullInfo.externalIds?.imdbId ?: throw Exception(),
                name = fullInfo.name,
                sinopsis = fullInfo.overview,
                path = "/${fullInfo.name} (${showDate.year})",
                imgPoster = fullInfo.posterPath,
                imgBackground = fullInfo.backdropPath
        ))
    }

    @Test
    fun fullImport() {
        val crawler = FileCrawler()
        val tmdbApi = TmdbApi("612ee3337f576410479963d2067a6143")
        var info: VideoFileInfo
        val show = OmdbAPI.getByTitle("Rascal Does Not Dream of Bunny Girl Senpai")
        var tmdbShow = TvSeries()
        tmdbApi.find.find(show.imdbId, TmdbFind.ExternalSource.imdb_id, "en")?.let {
            tmdbShow = it.tvResults?.first() ?: throw Exception()
        }
        var idx = 1
        File("E:\\crist\\Documents\\Workspace\\Temp").walk().forEach {
            if (it.isFile) {
                try {
                    info = crawler.parseFileInfo(it)
                    println("Successfully parsed $info")
                } catch (e: FileParseException) {
                    println(e.message)
                    val selectedName = show.title
                    val season = "1"
                    val episode = (idx).toString()
                    idx += 1
                    info = VideoFileInfo(selectedName, season, episode, "", it.path)
                    println("Created info manually $info")
                }
                val episodeInfo = tmdbApi.tvEpisodes.getEpisode(tmdbShow.id, info.season.toInt(), info.episode.toInt(), "en")
                println("Found $episodeInfo on imdb")
                println("From show parent $tmdbShow")
                val source = it.toPath()
                val renamedPath = source.resolveSibling("${tmdbShow.name} ${episodeInfo.seasonNumber}x0${episodeInfo.episodeNumber}.${it.extension}")
                Files.move(source, renamedPath, StandardCopyOption.REPLACE_EXISTING)
                println("Renamed file to $renamedPath")
                val importResult = crawler.importLibrary(renamedPath.toFile())
                println("Imported ${importResult.successfulImports}")
                println("Failed to import ${importResult.failedImports}")
            }
        }

    }
}