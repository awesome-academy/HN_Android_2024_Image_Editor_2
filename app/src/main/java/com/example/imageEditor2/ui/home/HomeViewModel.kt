package com.example.imageEditor2.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.imageEditor2.base.BaseViewModel
import com.example.imageEditor2.model.CollectionModel
import com.example.imageEditor2.repository.home.HomeRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class HomeViewModel(private val homeRepository: HomeRepository) : BaseViewModel() {
    private val _collectionsLiveData = MutableLiveData<List<CollectionModel>>()
    val collectionsLiveData: LiveData<List<CollectionModel>> = _collectionsLiveData

    init {
        getCollections()
    }

    fun getCollections(page: Int = 0) {
        viewModelScope.launch {
            homeRepository.getCollections(page).onStart { showLoading() }
                .catch { handleApiError(it) }.onCompletion { hideLoading() }
                .collect {
                    _collectionsLiveData.postValue(it)
                }
        }
    }

    fun likeImage(id: String) {
        viewModelScope.launch {
            homeRepository.likeImage(id).catch {
                handleApiError(it)
            }.collect()
        }
    }
}
