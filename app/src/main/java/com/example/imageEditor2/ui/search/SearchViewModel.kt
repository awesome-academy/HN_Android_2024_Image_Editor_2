package com.example.imageEditor2.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.imageEditor2.base.BaseViewModel
import com.example.imageEditor2.model.PhotoSearchModel
import com.example.imageEditor2.model.QueryModel
import com.example.imageEditor2.repository.search.SearchRepository
import com.example.imageEditor2.utils.ALL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class SearchViewModel(private val searchRepository: SearchRepository) : BaseViewModel() {
    private val mPhotosLiveData = MutableLiveData<PhotoSearchModel>()
    val photosLiveData: LiveData<PhotoSearchModel> = mPhotosLiveData

    private val mListQuery = mutableListOf<QueryModel>()
    val listQuery get() = mListQuery

    private val mError = MutableLiveData<String>()
    val error: LiveData<String> = mError

    private var mQuery = ALL

    init {
        searchPhotos(query = ALL)
        getAllQueryFromLocal()
    }

    fun searchPhotos(
        page: Int = 0,
        query: String? = null,
    ) {
        viewModelScope.launch {
            query?.let { mQuery = it }
            searchRepository.searchPhotos(page, mQuery)
                .catch { handleApiError(it) }
                .collect {
                    mPhotosLiveData.postValue(it)
                }
        }
    }

    fun getAllQueryFromLocal() {
        viewModelScope.launch {
            searchRepository.getAllQuery().catch { mError.postValue(it.message) }
                .collect {
                    mListQuery.clear()
                    mListQuery.addAll(it)
                }
        }
    }

    fun insertQuery(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (mListQuery.find { it.content == query } == null) {
                searchRepository.saveQueryToLocal(query).catch { mError.postValue(it.message) }
                    .onCompletion { getAllQueryFromLocal() }
                    .collect()
            }
        }
    }

    fun deleteQuery(queryModel: QueryModel) {
        viewModelScope.launch(Dispatchers.IO) {
            searchRepository.deleteQuery(queryModel).catch { mError.postValue(it.message) }
                .onCompletion { getAllQueryFromLocal() }
                .collect()
        }
    }
}
