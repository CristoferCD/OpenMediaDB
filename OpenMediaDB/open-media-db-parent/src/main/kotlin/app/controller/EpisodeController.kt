package app.controller

import SubtitleManager
import data.Subtitle
import data.SubtitleDownloadForm
import data.Video
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/episodes")
@Tag(name = "Episode", description = "Operations related to a single episode")
internal class EpisodeController : BaseController() {

    @GetMapping
    @Operation(summary = "Get a list of episodes", description = "Get a list of episodes")
    fun findEpisode(@Parameter(description = "Id of the parent show") @RequestParam("show") showId: String,
                    @Parameter(description = "Season number") @RequestParam(required = false) season: Int?,
                    @Parameter(description = "Episode number") @RequestParam(required = false) episode: Int?): List<Video> {
        val user = getAuthenticatedUser()
        return dataManagerFactory.videoDao.findFromParent(showId, season, episode, user)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific episode", description = "Get a specific episode")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "Episode not found", content = [Content()])
    ])
    fun getEpisode(@Parameter(description = "Episode id") @PathVariable id: Int): Video {
        val video = dataManagerFactory.videoDao.get(id)
        return video ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Episode not found")
    }

    @GetMapping("/{id}/subtitles")
    @Operation(summary = "Lists subtitles found for episode", description = "List subtitles found for episodes")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "Episode not found", content = [Content()])
    ])
    fun listAvailableSubtitles(@Parameter(description = "Episode id") @PathVariable id: Int): List<Subtitle> {
        val video = dataManagerFactory.videoDao.get(id)
        if (video == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Episode not found")
        } else {
            val show = dataManagerFactory.showDao.get(video.showId)
            return SubtitleManager.search(show!!.name, video.season, video.episodeNumber)
        }
    }

    @PostMapping("/subtitle", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    @Operation(summary = "Download subtitle file", description = "Download subtitle file")
    fun getSubtitle(@RequestBody form: SubtitleDownloadForm): ByteArrayResource {
        val bytes = SubtitleManager.get(form)
        return ByteArrayResource(bytes!!)
    }

    @PostMapping("/{id}/seen")
    @Operation(summary = "Mark episode as seen", description = "Mark episode as seen")
    fun markSeen(@Parameter(description = "Episode id") @PathVariable id: Int,
                 @Parameter(description = "Seen") @RequestParam value: Boolean): Boolean {
        val user = getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in")
        dataManagerFactory.videoDao.markWatched(value, user, id)
        return value
    }
}