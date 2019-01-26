import data.ImportResult
import data.VideoFileInfo
import java.io.File
import java.io.FileInputStream
import java.nio.file.*
import java.util.*
import java.util.regex.Pattern

class FileCrawler {
    private val CONFIG_FILE = "config.properties"
    private val properties = Properties()
    var libraryRoot: String = ""
    var directoryPattern: String = ""
    var shownamePattern: String = ""
    //TODO: shownamePatter must at least have name, season and episode number

    init {
        this.javaClass.getResourceAsStream(CONFIG_FILE).use {
            properties.load(it)
            libraryRoot = properties.getProperty("library.root") ?: ""
            directoryPattern = properties.getProperty("pattern.directory") ?: "#(name)/#(season)ª Temp"
            shownamePattern = properties.getProperty("pattern.showname") ?: "(?<name>.+) (?<season>.+)x(?<episode>.+)"
        }
    }

    fun walkTest() {
        File("\\\\ORANGEPIZERO\\opiserver\\Anime").walk().forEach {
            println("Analyzing ${it.canonicalPath}")
            if (it.isFile) {
                //match shownamePattern
                val match = Pattern.compile(shownamePattern).matcher(it.nameWithoutExtension)
                //if got a full match, create entry and move to library
                if (match.matches()) {
                    println("Found episode ${match.group("name")} of season ${match.group("season")} and number ${match.group("episode")}")
                }

                //else ask for data and move to library
            }
        }
    }

    fun importLibrary(from: File, destructive: Boolean = false) : ImportResult {
        val result = ImportResult()
        from.walk().forEach {
            if (it.isFile) {
                try {
                    val info = importFile(it, destructive)
                    result.successfulImports.add(info)
                } catch (e: Exception) {
                    result.failedImports.add(it.absolutePath)
                }
            }
        }
        return result
    }

    fun parseFileInfo(file: File) : VideoFileInfo {
        val match = Pattern.compile(shownamePattern).matcher(file.nameWithoutExtension)
        if (match.matches()) {
            return VideoFileInfo(match.group("name"), match.group("season"),
                    match.group("episode"),
                    if (shownamePattern.contains("(?<episodeName>")) match.group("episodeName") else "",
                    file.absolutePath)
        }
        throw Exception()
    }

    private fun importFile(from: File, destructive: Boolean) : VideoFileInfo {
        val fileInfo = parseFileInfo(from)
        var directoryTarget = directoryPattern.replace("#(name)", fileInfo.name)
        directoryTarget = directoryTarget.replace("#(season)", fileInfo.season)
        directoryTarget = directoryTarget.replace("/", File.separator)
        directoryTarget = directoryTarget.replace("\\", File.separator)
        val targetPath = Paths.get(libraryRoot, directoryTarget, from.name)
        if (!Files.exists(targetPath)) Files.createDirectories(targetPath)
        if (destructive)
            Files.move(from.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING)
        else
            Files.copy(from.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING)
        fileInfo.path = targetPath.toString()
        return fileInfo
    }
}