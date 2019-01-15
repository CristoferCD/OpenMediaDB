package data

import com.google.gson.annotations.SerializedName

data class PagedResults (
        @SerializedName("Search")
        val results: List<ImdbTitle>,

        @SerializedName("totalResults")
        val totalResults: String,

        @SerializedName("Response")
        val responseSuccessful: String,

        @SerializedName("Error")
        val errorMsg: String?
)