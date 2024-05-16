package com.example.imageEditor2.ui.create

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.imageEditor2.R
import com.example.imageEditor2.base.BaseViewModel
import com.example.imageEditor2.repository.create.CreateImageRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class CreateImageViewModel(private val createImageRepository: CreateImageRepository) :
    BaseViewModel() {
    private val mMessage = MutableLiveData<Int>()
    val message: LiveData<Int> = mMessage

    private val mError = MutableLiveData<String>()
    val error: LiveData<String> = mError

    fun saveImage(
        bitmap: Bitmap,
        onSaved: () -> Unit,
    ) {
        viewModelScope.launch {
            createImageRepository.saveImage(bitmap)
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
