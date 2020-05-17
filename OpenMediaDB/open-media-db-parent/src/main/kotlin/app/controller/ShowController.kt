package app.controller

import data.Show
import data.response.PagedResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RequestMapping("/shows")
@Tag(name = "Shows", description = "Show related operations")
internal interface ShowController {

    @PostMapping
    @Operation(summary = "Add a new show", description = "Registers a new show into the library")
    fun registerShow(@Parameter(description = "Show id from imdb.com") @RequestParam imdbId: String)

    @GetMapping
    @Operation(summary = "List all shows", description = "Returns a list of all registered shows")
    fun getList(): List<Show>

    @GetMapping("/following")
    @Operation(summary = "List following shows", description = "Lists all shows marked as 'followed' by the logged user")
    fun getFollowing(): List<Show>

    @PostMapping("/{id}/follow")
    @Operation(summary = "Follow show", description = "Mark show as followed/unfollowed")
    fun doFollow(@Parameter(description = "Show id") @PathVariable id: String,
                 @Parameter(description = "Follow/Unfollow") @RequestParam value: Boolean): Boolean

    @GetMapping("/find")
    @Operation(summary = "Find register show", description = "Find a show already registered on the library")
    fun find(@Parameter(description = "Show name to search") @RequestParam("q") query: String): PagedResponse<Show>

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific show", description = "Get a specific show")
    fun getShow(@PathVariable id: String): Show

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a show", description = "Delete a show")
    fun deleteShow(@Parameter(description = "Show id") @PathVariable id: String,
                   @Parameter(description = "Remove files saved on library path") @RequestParam deleteFiles: Boolean): Boolean

    @GetMapping("/search")
    @Operation(summary = "Search for new shows", description = "Search shows by name not already on the library")
    fun search(@RequestParam query: String, @RequestParam(required = false) page: Int?): PagedResponse<Show>
}