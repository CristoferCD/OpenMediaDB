package data.response

open class PagedResponse<T>(
        val data: List<T>,
        val totalResults: Int,
        val page: Int,
        val totalPages: Int
)