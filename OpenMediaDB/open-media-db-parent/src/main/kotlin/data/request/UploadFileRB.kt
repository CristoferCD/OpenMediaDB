package data.request

import java.io.InputStream

class UploadFileRB(var showId: String? = null,
                   var season: String? = null,
                   var episode: String? = null,
                   var file: InputStream? = null,
                   var originalFilename: String? = null) {
    fun isComplete() = showId != null && season != null && episode != null && file != null
}