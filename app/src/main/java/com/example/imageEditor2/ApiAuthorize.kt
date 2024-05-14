package com.example.imageEditor2

import com.example.imageEditor2.model.request.AuthorizeRequest
import com.example.imageEditor2.model.response.AuthorizeResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiAuthorize {
    @POST("oauth/token")
    suspend fun authorize(
        @Body request: AuthorizeRequest,
    ): AuthorizeResponse
}
