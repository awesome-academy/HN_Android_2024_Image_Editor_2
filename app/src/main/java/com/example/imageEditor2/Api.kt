package com.example.imageEditor2

import com.example.imageEditor2.model.CollectionModel
import com.example.imageEditor2.utils.COLLECTION_ENDPOINT
import com.example.imageEditor2.utils.PAGE
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET(COLLECTION_ENDPOINT)
    suspend fun getCollections(
        @Query(PAGE) page: Int,
    ): List<CollectionModel>

    @GET
    suspend fun searchPhotos(
        @Query("page") page: String,
        @Query("query") querySearch: String,
    )

    @POST("photos/{id}/like")
    suspend fun likeImage(
        @Path("id") id: String,
    )

    @DELETE
    suspend fun dislikeImage()

    @GET
    suspend fun getFavoriteList()
}
