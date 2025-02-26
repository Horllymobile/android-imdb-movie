package com.horllymobile.androidmoview.network

import com.horllymobile.androidmoview.service.MovieApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.themoviedb.org/3/"

val client = OkHttpClient()

val clientBuilder: OkHttpClient.Builder = client.newBuilder()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(clientBuilder.build())
    .build()

object ApiCalls {
    val movies : MovieApiService by lazy {
        retrofit.create(MovieApiService::class.java)
    }
}