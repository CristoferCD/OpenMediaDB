package data.response

data class TokenExpiredResponse(val error: String, val message: String) {
    fun json() = """
            {
                "error": "$error",
                "message": "$message"
            }
        """.trimIndent().toByteArray()

}