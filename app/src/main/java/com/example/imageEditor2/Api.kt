package com.example.imageEditor2

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Api {
    @GET()
    suspend fun getCollections(
        @Query("page") page: Int,
    )

    @GET
    suspend fun searchPhotos(
        @Query("page") page: String,
        @Query("query") querySearch: String,
    )

    @POST
    suspend fun likeImage()

    @DELETE
    suspend fun dislikeImage()

    @GET
    suspend fun getFavoriteList()
}
