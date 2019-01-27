package exceptions

import java.io.File

class FileParseException(file: File, matchPattern: String)
    : Exception("Failed to parse ${file.nameWithoutExtension} using pattern $matchPattern")