package data

import com.beust.klaxon.Json

data class PagedResults (
        @Json(name = "Search")
        val results: List<ImdbTitle>,

        @Json(name = "totalResults")
        val totalResults: Int,

        @Json(name = "Response")
        val responseError: Boolean,

        @Json(name = "Error")
        val errorMsg: String?
)