package com.horllymobile.androidmoview.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horllymobile.androidmoview.data.ApiError
import com.horllymobile.androidmoview.data.Movie
import com.horllymobile.androidmoview.data.MovieListScreenUIState
import com.horllymobile.androidmoview.network.ApiCalls
import com.horllymobile.androidmoview.service.PaginatedResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MovieListViewModel : ViewModel() {

    private val _movieListScreenUIState = MutableStateFlow(MovieListScreenUIState())

    val movieListScreenUIState get() = _movieListScreenUIState.asStateFlow()
    private val movieListScreenUIStateInner get() = movieListScreenUIState.value

    fun updateLoading(status: Boolean) {
        _movieListScreenUIState.update { state ->
            state.copy(
                isLoading = status
            )
        }
    }

    fun updateMovies(data: PaginatedResponse<Movie>) {
        _movieListScreenUIState.update { state ->
            state.copy(
                movies = data
            )
        }
    }

    fun updateMoreMovies(data: PaginatedResponse<Movie>) {
        val newMovies = data.results
        if (newMovies != null) {
            _movieListScreenUIState.update { state ->
                state.copy(
                    movies = state.movies?.copy(
                        page = data.page,
                        results = state.movies.results?.plus(newMovies)
                    )
                )
            }
        }
    }

    fun updateError(message: String, show: Boolean) {
        Log.d("updateError", message)
        _movieListScreenUIState.update { currentState ->
            currentState.copy(
                error = ApiError(
                    status = show,
                    message = message
                ),
            )
        }
    }


    fun loadMoreMovies(
        params: Map<String, String>
    ) {
        viewModelScope.launch {
            try {
                val result = ApiCalls.movies.getPopularMovies(params)
                updateMoreMovies(result)
                updateLoading(false)
                Log.d("Get Popular Movies RESULT", "$result")
            } catch (e: HttpException) {
                updateLoading(false)
                Log.d("Get Popular Movies ERROR", e.message())
                e.message?.let {
                    updateError(
                        message = it,
                        show = true
                    )
                }
            } catch (e: IOException) {
                updateLoading(false)
                Log.d("LOGIN ERROR", "$e")
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
                Log.d("LOGIN ERROR", "$e")
            }
        }
    }

    fun searchMovie(params: Map<String, String>) {
        viewModelScope.launch {
            try {
                val result = ApiCalls.movies.searchMovies(params)
                updateMovies(result)
                Log.d("Get Popular Movies RESULT", "$result")
            } catch (e: HttpException) {
                Log.d("Get Popular Movies ERROR", "$e")
                e.message?.let {
                    updateError(
                        message = it,
                        show = true
                    )
                }
            } catch (e: IOException) {
                Log.d("LOGIN ERROR", "$e")
                e.message?.let {
                    updateError(
                        message = it,
                        show = true
                    )
                }
            }
            catch (e: Exception) {
                e.message?.let {
                    updateError(
                        message = it,
                        show = true
                    )
                }
                Log.d("LOGIN ERROR", "$e")
            }
        }
    }

    fun getPopularMovies(
        params: Map<String, String>
    ) {
        updateLoading(true)
        viewModelScope.launch {
            try {
                val result = ApiCalls.movies.getPopularMovies(params)
                updateMovies(result)
                updateLoading(false)
                Log.d("Get Popular Movies RESULT", "$result")
            } catch (e: HttpException) {
                updateLoading(false)
                Log.d("Get Popular Movies ERROR", "$e")
                e.message?.let {
                    updateError(
                        message = it,
                        show = true
                    )
                }
            } catch (e: IOException) {
                updateLoading(false)
                Log.d("LOGIN ERROR", "$e")
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
                Log.d("LOGIN ERROR", "$e")
            }
        }
    }
}