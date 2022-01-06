package com.wyre.moviefinder.models

import java.sql.Timestamp

data class Movie(
    val trackName: String,
    val releaseDate: Timestamp,
    val artworkUrl100: String,
    val shortDescription: String
)

data class MovieResponse(val results: List<Movie>)

enum class ResponseState {
    WAITING, SUCCESS, FAILURE
}