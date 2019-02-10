package app.library

import FileCrawler

internal object LibraryManager {
    val fileCrawler by lazy { FileCrawler() }
}