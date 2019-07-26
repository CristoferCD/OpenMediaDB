package app

import app.library.LibraryManager
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class Application

fun main(args: Array<String>) {
    val context  = SpringApplication.run(Application::class.java, *args)
    context.getBean(LibraryManager::class.java).refreshLibrary()
}