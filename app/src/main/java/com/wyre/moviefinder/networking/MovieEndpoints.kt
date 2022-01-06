package com.wyre.moviefinder.networking

import com.wyre.moviefinder.models.MovieResponse
import retrofit2.Call
import retrofit2.http.GET

interface MovieEndpoints {

    @GET("/search?term=The+Lion+King&media=movie&limit=5")
    fun getMovies(): Call<MovieResponse>
}