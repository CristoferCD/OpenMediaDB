package app.library.exceptions

internal class MediaNotFoundException(id: String, type: String) : Exception("Unable to find $type with id $id")