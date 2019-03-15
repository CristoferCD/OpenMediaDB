package data.request

import data.Show

data class SearchRB (
        val results: List<Show>,
        val totalResults: Int,
        val page: Int,
        val totalPages: Int
)