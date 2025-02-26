package com.horllymobile.androidmoview.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horllymobile.androidmoview.data.ApiError
import com.horllymobile.androidmoview.data.Movie
import com.horllymobile.androidmoview.data.MovieListScreenUIState
import com.horllymobile.androidmoview.data.MovieScreenUiState
import com.horllymobile.androidmoview.network.ApiCalls
import com.horllymobile.androidmoview.service.PaginatedResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MovieViewModel : ViewModel() {
    private val _movieScreenUIState = MutableStateFlow(MovieScreenUiState())

    val movieScreenUIState get() = _movieScreenUIState.asStateFlow()
    private val movieScreenUIStateInner get() = movieScreenUIState.value

    fun updateLoading(status: Boolean) {
        _movieScreenUIState.update { state ->
            state.copy(
                isLoading = status
            )
        }
    }

    fun updateMovie(data: Movie? = null) {
        _movieScreenUIState.update { state ->
            state.copy(
                movie = data
            )
        }
    }

    fun updateMovieId(data: Int) {
        _movieScreenUIState.update { state ->
            state.copy(
                id = data
            )
        }
    }

    private fun updateError(message: String, show: Boolean) {
        Log.d("updateError", message)
        _movieScreenUIState.update { currentState ->
            currentState.copy(
                error = ApiError(
                    status = show,
                    message = message
                ),
            )
        }
    }

    fun getMovie(
        id: Int,
        params: Map<String, String>
    ) {
        updateLoading(true)
        viewModelScope.launch {
            try {
                val result = ApiCalls.movies.getMovie(
                    id = id,
                    params = params
                )
                updateMovie(result)
                updateLoading(false)
                Log.d("Get Movie RESULT", "$result")
            } catch (e: HttpException) {
                updateLoading(false)
                Log.d("Get Movie ERROR", e.message())
                e.message?.let {
                    updateError(
                        message = it,
                        show = true
                    )
                }
            } catch (e: IOException) {
                updateLoading(false)
                Log.d("Get Movie", "$e")
                e.message?.let {
                    updateError(
                        message = it,
                        show = true
                    )
                }
            }
            catch (e: Exception) {
                updateLoading(false)
                e.message?.let {
                    updateError(
                        message = it,
                        show = true
                    )
                }
                Log.d("Get Movie", "$e")
            }
        }
    }
}