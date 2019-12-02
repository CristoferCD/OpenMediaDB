import data.ImportResult
import data.VideoFileInfo
import exceptions.FileParseException
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.concurrent.CompletableFuture

class FileCrawler {
    private val CONFIG_FILE = "config.properties"
    private val properties = Properties()
    var libraryRoot: String = ""
    var directoryPattern: String = ""
    var shownamePattern: String = ""
    //TODO: shownamePattern must at least have name, season and episode number

    init {
        this.javaClass.getResourceAsStream(CONFIG_FILE).use {
            properties.load(it)
            libraryRoot = properties.getProperty("library.root") ?: ""
            directoryPattern = properties.getProperty("pattern.directory") ?: "#(name)/#(season)ª Temp"
            shownamePattern = properties.getProperty("pattern.showname")
                    ?: "(?<name>.+) (?<season>[0-9]+)x(?<episode>[0-9]+) - (?<epName>.+)"
        }
    }

    fun importLibrary(from: File, destructive: Boolean = false): ImportResult {
        val result = ImportResult()
        from.walk().filter { it.isFile }.forEach {
            try {
                val info = parseFileInfo(it)
                result.successfulImports.add(info)
            } catch (e: Exception) {
                result.failedImports.add(it.absolutePath)
            }
        }
        return result
    }

    fun parseFileInfo(file: File): VideoFileInfo {
        return parseFileName(file.nameWithoutExtension).copy(path = file.toPath())
    }

    fun parseFileName(name: String): VideoFileInfo {
        val match = Regex(shownamePattern).matchEntire(name)
        if (match != null) {
            return VideoFileInfo(match.groups["name"]!!.value,
                    match.groups["season"]!!.value.toInt(),
                    match.groups["episode"]!!.value.toInt(),
                    if (shownamePattern.contains("(?<epName>")) match.groups["epName"]!!.value else "")
        }
        throw FileParseException(name, shownamePattern)
    }

    fun importData(info: VideoFileInfo, fileExtension: String, data: InputStream): Path {
        var directoryTarget = directoryPattern.replace("#(name)", info.name.replace("[:/*\"?|<>] ?".toRegex(), " "))
        directoryTarget = directoryTarget.replace("#(season)", info.season.toString())
        directoryTarget = directoryTarget.replace("/", File.separator)
        directoryTarget = directoryTarget.replace("\\", File.separator)
        val targetPath = Paths.get(libraryRoot, directoryTarget, "${createCorrectName(info)}.$fileExtension")
        CompletableFuture.runAsync {
            Files.createDirectories(targetPath.parent)
            Files.copy(data, targetPath, StandardCopyOption.REPLACE_EXISTING)
        }
        return targetPath
    }

    private fun createCorrectName(info: VideoFileInfo): String {
        var name = shownamePattern.replace("(?<name>.+)", info.name)
        name = name.replace("(?<season>[0-9]+)", if (info.season < 10) "0${info.season}" else info.season.toString())
        name = name.replace("(?<episode>[0-9]+)", if (info.episode < 10) "0${info.episode}" else info.episode.toString())
        name = name.replace("(?<epName>.+)", info.episodeName.replace("/", "_"))
        return name.replace("[:/*\"?|<>] ?".toRegex(), " ")
    }
}