package com.horllymobile.androidmoview.data

import com.horllymobile.androidmoview.service.PaginatedResponse

data class MovieListScreenUIState(
    val movies: PaginatedResponse<Movie>? = null,
    val isLoading: Boolean = false,
    val error: ApiError = ApiError(
        status = false,
        message = ""
    )
)

data class ApiError(
    val status: Boolean = false,
    val message: String = "",
)
