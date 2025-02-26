package com.horllymobile.androidmoview.data

data class Movie(
    val id: Int,
    val title: String,
    val backdrop_path: String,
    val adult: Boolean,
    val genre_ids: List<Int>,
    val original_language: String,
    val original_title: String,
    val budget: Int? = null,
    val revenue: Int? = null,
    val runtime: Int? = null,
    val overview: String,
    val origin_country: List<String>,
    val tagline: String? = null,
    val popularity: Double,
    val poster_path: String,
    val release_date: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int,
)
