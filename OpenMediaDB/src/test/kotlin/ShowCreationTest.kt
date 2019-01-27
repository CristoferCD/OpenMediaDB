import data.FileInfo
import data.ResultType
import data.Show
import data.VideoFileInfo
import exceptions.FileParseException
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class ShowCreationTest {
    @Test
    fun createShow() {
        val show = OmdbAPI.getByTitle("The Rookie", ResultType.SERIES)
        DataManagerFactory.showDao.insert(Show(
                imdbId = show.imdbId,
                name = show.title,
                path = "/${show.title} (${show.year})",
                imgPoster = show.imgPoster
        ))
    }

    @Test
    fun fullImport() {
        val crawler = FileCrawler()
        var info: VideoFileInfo
        val show = OmdbAPI.getByTitle("Konosuba")
        var idx = 1
        File("E:\\crist\\Documents\\Workspace\\Temp").walk().forEach {
            if (it.isFile) {
                try {
                    info = crawler.parseFileInfo(it)
                    println("Successfully parsed $info")
                } catch (e: FileParseException) {
                    println(e.message)
                    val selectedName = show.title
                    val season = "2"
                    val episode = (idx).toString()
                    idx += 1
                    info = VideoFileInfo(selectedName, season, episode, "", it.path)
                    println("Created info manually $info")
                }
                val imdbInfo = OmdbAPI.getByTitle(info.name, season = info.season.toInt(), episode = info.episode.toInt())
                println("Found $imdbInfo on imdb")
                println("From show parent $show")
                val source = it.toPath()
                val renamedPath = source.resolveSibling("${show.title} ${imdbInfo.season}x0${imdbInfo.episode}.${it.extension}")
                Files.move(source, renamedPath, StandardCopyOption.REPLACE_EXISTING)
                println("Renamed file to $renamedPath")
                val importResult =  crawler.importLibrary(renamedPath.toFile())
                println("Imported ${importResult.successfulImports}")
                println("Failed to import ${importResult.failedImports}")
            }
        }

    }
}