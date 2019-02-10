package data

data class ImportResult (
        val successfulImports: MutableCollection<VideoFileInfo> = mutableListOf(),
        val failedImports: MutableCollection<String> = mutableListOf()
)

data class VideoFileInfo (
        var name: String,
        var season: String,
        var episode: String,
        var episodeName: String,
        var path: String
)