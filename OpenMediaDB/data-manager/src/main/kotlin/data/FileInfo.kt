package data

data class FileInfo (
        val id: Int,
        val uri: String,
        var duration: Int?,
        var resolution: String,
        var bitrate: String,
        var codec: String
)