package com.horllymobile.androidmoview.data

data class MovieScreenUiState(
    val id: Int = 0,
    val movie: Movie? = null,
    val isLoading: Boolean = false,
    val error: ApiError = ApiError(
        status = false,
        message = ""
    )
)