import data.ImportResult
import data.VideoFileInfo
import exceptions.FileParseException
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

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

    fun importLibrary(from: File, destructive: Boolean = false): ImportResult {
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

    fun parseFileInfo(file: File): VideoFileInfo {
        val match = Regex(shownamePattern).matchEntire(file.nameWithoutExtension)
        if (match != null) {
            return VideoFileInfo(match.groups["name"]!!.value, match.groups["season"]!!.value,
                    match.groups["episode"]!!.value,
                    if (shownamePattern.contains("(?<episodeName>")) match.groups["episodeName"]!!.value else "",
                    file.absolutePath)
        }
        throw FileParseException(file, shownamePattern)
    }

    private fun importFile(from: File, destructive: Boolean): VideoFileInfo {
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