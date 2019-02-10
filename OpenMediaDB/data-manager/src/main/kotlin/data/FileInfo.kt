package data

import java.nio.file.Path

data class FileInfo (
        val id: Int?,
        val path: Path,
        var duration: Int?,
        var resolution: String,
        var bitrate: String,
        var codec: String
)