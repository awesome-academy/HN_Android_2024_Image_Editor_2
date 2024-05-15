package com.example.imageEditor2.ui.detail

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.imageEditor2.R
import com.example.imageEditor2.base.BaseViewModel
import com.example.imageEditor2.repository.detail.DetailRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class DetailViewModel(private val detailRepository: DetailRepository) : BaseViewModel() {
    private val mMessage = MutableLiveData<Int>()
    val message: LiveData<Int> = mMessage

    private val mError = MutableLiveData<String>()
    val error: LiveData<String> = mError

    fun downloadImage(url: String) {
        viewModelScope.launch {
            detailRepository.downloadImage(url)
                .catch { mError.postValue(it.message) }
                .collect()
        }
    }

    fun saveImage(
        bitmap: Bitmap,
        onSaved: () -> Unit,
    ) {
        viewModelScope.launch {
            detailRepository.saveImage(bitmap)
                .onStart { mMessage.postValue(R.string.downloading) }
                .catch { mError.postValue(it.message) }
                .onCompletion {
                    mMessage.postValue(R.string.save_success)
                    onSaved.invoke()
                }
                .collect()
        }
    }
}
