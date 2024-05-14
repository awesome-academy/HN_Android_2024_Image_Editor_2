package com.example.imageEditor2.base

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.imageEditor2.R
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection

abstract class BaseViewModel() : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<Int>()
    val errorMessage: LiveData<Int> = _errorMessage

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    protected fun showLoading() {
        _isLoading.postValue(true)
    }

    protected fun hideLoading() {
        _isLoading.postValue(false)
    }

    protected fun handleApiError(error: Throwable?) {
        if (error == null) {
            _errorMessage.postValue(R.string.api_default_error)
            return
        }

        if (error is HttpException) {
            Log.w("ERROR", error.message() + error.code())
            when (error.code()) {
                HttpURLConnection.HTTP_BAD_REQUEST ->
                    try {
                        _responseMessage.postValue(error.message())
                    } catch (e: IOException) {
                        e.printStackTrace()
                        _responseMessage.postValue(error.message)
                    }

                HttpsURLConnection.HTTP_UNAUTHORIZED -> _errorMessage.postValue(R.string.str_authen)
                HttpsURLConnection.HTTP_FORBIDDEN, HttpsURLConnection.HTTP_INTERNAL_ERROR, HttpsURLConnection.HTTP_NOT_FOUND ->
                    _responseMessage.postValue(
                        error.message,
                    )

                else -> _responseMessage.postValue(error.message)
            }
        } else if (error is IOException) {
            Log.e("TAG", error.message.toString())
            _errorMessage.postValue(R.string.text_all_has_error_network)
        }
    }
}
