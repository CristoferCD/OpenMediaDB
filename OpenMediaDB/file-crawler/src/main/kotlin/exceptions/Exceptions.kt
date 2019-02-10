package exceptions

class FileParseException(fileName: String, matchPattern: String)
    : Exception("Failed to parse $fileName using pattern $matchPattern")