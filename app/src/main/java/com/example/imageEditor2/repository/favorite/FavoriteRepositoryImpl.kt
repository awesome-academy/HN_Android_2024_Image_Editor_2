package com.example.imageEditor2.repository.favorite

import com.example.imageEditor2.Api
import com.example.imageEditor2.model.PhotoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class FavoriteRepositoryImpl(private val api: Api) : FavoriteRepository {
    override suspend fun getFavoriteList(
        name: String,
        page: Int,
    ): Flow<List<PhotoModel>> {
        return withContext(Dispatchers.IO) {
            flow { emit(api.getFavoriteList(name, page)) }
        }
    }

    override suspend fun likeImage(id: String): Flow<Unit> {
        return withContext(Dispatchers.IO) {
            flow { emit(api.likeImage(id)) }
        }
    }

    override suspend fun dislikeImage(id: String): Flow<Unit> {
        return withContext(Dispatchers.IO) {
            flow { emit(api.dislikeImage(id)) }
        }
    }
}
