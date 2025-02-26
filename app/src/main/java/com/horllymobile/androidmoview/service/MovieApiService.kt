package com.horllymobile.androidmoview.service

import com.horllymobile.androidmoview.data.Movie
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface MovieApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @QueryMap params: Map<String, String> = mapOf()
    ): PaginatedResponse<Movie>


    @GET("search/movie")
    suspend fun searchMovies(
        @QueryMap params: Map<String, String> = mapOf(
            "query" to ""
        )
    ): PaginatedResponse<Movie>

    @GET("movie/{id}")
    suspend fun getMovie(
        @Path("id") id: Int,
        @QueryMap params: Map<String, String> = mapOf()
    ): Movie
}

@Serializable
data class PaginatedResponse<T>(
    val page: Int = 0,
    @Transient
    val results: List<T>? = null,
    val total_pages: Int = 0,
    val total_results: Int = 0
)