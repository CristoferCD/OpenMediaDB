package app.controller

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
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/episodes")
@Tag(name = "Episode", description = "Operations related to a single episode")
internal interface EpisodeController {

    @GetMapping
    @Operation(summary = "Get a list of episodes", description = "Get a list of episodes")
    fun findEpisode(@Parameter(description = "Id of the parent show") @RequestParam("show") showId: String,
                    @Parameter(description = "Season number") @RequestParam(required = false) season: Int?,
                    @Parameter(description = "Episode number") @RequestParam(required = false) episode: Int?): List<Video>

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific episode", description = "Get a specific episode")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "Episode not found", content = [Content()])
    ])
    fun getEpisode(@Parameter(description = "Episode id") @PathVariable id: Int): Video

    @GetMapping("/{id}/subtitles")
    @Operation(summary = "Lists subtitles found for episode", description = "List subtitles found for episodes")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", description = "Episode not found", content = [Content()])
    ])
    fun listAvailableSubtitles(@Parameter(description = "Episode id") @PathVariable id: Int): List<Subtitle>

    @PostMapping("/subtitle", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    @Operation(summary = "Download subtitle file", description = "Download subtitle file")
    fun getSubtitle(@RequestBody form: SubtitleDownloadForm): ByteArrayResource

    @PostMapping("/{id}/seen")
    @Operation(summary = "Mark episode as seen", description = "Mark episode as seen")
    fun markSeen(@Parameter(description = "Episode id") @PathVariable id: Int,
                 @Parameter(description = "Seen") @RequestParam value: Boolean): Boolean
}