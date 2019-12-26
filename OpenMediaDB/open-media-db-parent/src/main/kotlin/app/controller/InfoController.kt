package app.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

@Controller
internal class InfoController : BaseController() {

    @Value("classpath:/API.json")
    private lateinit var apiJson: org.springframework.core.io.Resource

    @GetMapping("/api")
    fun apiDocs() = "swagger.html"

    @GetMapping("/api/json")
    @ResponseBody
    fun jsonFile() : String = BufferedReader(InputStreamReader(apiJson.inputStream)).lines()
            .collect(Collectors.joining(System.lineSeparator()))
}