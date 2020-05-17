package app.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/files")
@Tag(name = "Files", description = "File management")
internal interface FileController {

    @GetMapping("/{id}")
    @Operation(summary = "Get file token", description = "Returns token to consume file without authorization headers")
    fun getFile(@Parameter(description = "File id") @PathVariable id: Int): String

    @PostMapping
    @Operation(summary = "Upload a file", description = "Upload a file")
    fun uploadFile(@RequestParam showId: String, @RequestParam season: Int, @RequestParam episode: Int, @RequestParam file: MultipartFile): String

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a file", description = "Delete a file")
    fun deleteFile(@PathVariable id: Int)

}