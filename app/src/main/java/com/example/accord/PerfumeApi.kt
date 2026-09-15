package com.example.accord

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PerfumeApi {

    @GET("perfumes")
    suspend fun getPerfumes(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PerfumeListResponse

    @GET("perfumes/search/{query}")
    suspend fun searchPerfumes(
        @Path("query") query: String,
        @Query("limit") limit: Int = 20
    ): List<Perfume>   // note: raw array, no wrapper
}