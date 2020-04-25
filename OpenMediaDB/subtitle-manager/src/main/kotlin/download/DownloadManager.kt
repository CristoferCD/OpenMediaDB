package download

import com.github.junrar.Archive
import com.github.junrar.exception.RarException
import mu.KotlinLogging
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

object DownloadManager {
    private val log = KotlinLogging.logger {}
    private const val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4115.0 Safari/537.36 Edg/84.0.488.1"

    fun manageDownload(url: URL): List<Path> {
        var connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("User-Agent", userAgent)

        if (connection.responseCode in listOf(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_SEE_OTHER)) {
            val redirect = connection.getHeaderField("Location")
            connection = URL(redirect).openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", userAgent)
        }

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            log.error {
                """
                Error donwloading subtitle from url $url.
                Response code [${connection.responseCode}]. Message: ${connection.responseMessage}
            """.trimIndent()
            }
            throw SubtitleDownloadException()
        }

        return when (connection.contentType) {
            "application/zip" -> createZip(connection.inputStream)
            "application/x-rar-compressed" -> createRar(connection.inputStream)
            "text/plain", "text/plain; charset=UTF-8" -> plainText(connection.inputStream)
            else -> emptyList()
        }
    }

    private fun createZip(inputStream: InputStream): List<Path> {
        val file = createTempFile("sub", ".zip").toPath()
        Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING)
        return unzip(file)
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

    private fun createRar(inputStream: InputStream): List<Path> {
        val file = createTempFile("sub", ".rar").toPath()
        Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING)
        return try {
            unrar(file)
        } catch (ex: RarException) {
            log.error { ex }
            return emptyList()
        }
    }

    private fun unrar(path: Path): List<Path> {
        val extractedFiles = mutableListOf<Path>()
        val archive = Archive(path.toFile().inputStream())
        val destDir = Files.createTempDirectory("omediadb-subs")
        archive.fileHeaders.forEach { fh ->
            val newFile = File(destDir.toFile(), fh.fileNameString)
            newFile.outputStream().use { os ->
                archive.extractFile(fh, os)
            }
            extractedFiles.add(newFile.toPath())
        }
        return extractedFiles
    }

    private fun plainText(inputStream: InputStream): List<Path> {
        val file = createTempFile("sub", ".srt").toPath()
        Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING)
        return listOf(file)
    }
}