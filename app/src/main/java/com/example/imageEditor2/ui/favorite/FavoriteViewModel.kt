package com.example.imageEditor2.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.imageEditor2.base.BaseViewModel
import com.example.imageEditor2.model.PhotoModel
import com.example.imageEditor2.repository.favorite.FavoriteRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favoriteRepository: FavoriteRepository) : BaseViewModel() {
    private val mPhotosLiveData = MutableLiveData<List<PhotoModel>>()
    val photosLiveData: LiveData<List<PhotoModel>> = mPhotosLiveData

    fun getFavoriteList(
        name: String,
        page: Int = 1,
    ) {
        viewModelScope.launch {
            favoriteRepository.getFavoriteList(name, page).onStart { showLoading() }
                .catch { handleApiError(it) }
                .onCompletion { hideLoading() }
                .collect {
                    mPhotosLiveData.postValue(it)
                }
        }
    }

    fun likeImage(id: String) {
        viewModelScope.launch {
            favoriteRepository.likeImage(id).catch {
                handleApiError(it)
            }.collect()
        }
    }

    fun dislikeImage(id: String) {
        viewModelScope.launch {
            favoriteRepository.dislikeImage(id).catch {
                handleApiError(it)
            }.collect()
        }
    }
}
