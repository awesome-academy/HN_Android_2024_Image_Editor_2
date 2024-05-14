package com.example.imageEditor2.ui.authorize

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.imageEditor2.base.BaseViewModel
import com.example.imageEditor2.model.request.AuthorizeRequest
import com.example.imageEditor2.model.response.AuthorizeResponse
import com.example.imageEditor2.repository.AuthorizeRepository
import com.example.imageEditor2.utils.ACCESS_KEY
import com.example.imageEditor2.utils.REDIRECT_URI
import com.example.imageEditor2.utils.SECRET_KEY
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AuthorizeViewModel(private val authorizeRepository: AuthorizeRepository) : BaseViewModel() {
    private val _authorizeLiveData = MutableLiveData<AuthorizeResponse>()
    val authorizeLiveData: LiveData<AuthorizeResponse> = _authorizeLiveData

    fun authorize(authorizationCode: String) {
        viewModelScope.launch {
            val authorizeRequest =
                AuthorizeRequest(
                    clientId = ACCESS_KEY,
                    clientSecret = SECRET_KEY,
                    redirectUri = REDIRECT_URI,
                    code = authorizationCode,
                )
            authorizeRepository.authorize(authorizeRequest).onStart { showLoading() }
                .catch { handleApiError(it) }
                .onCompletion { hideLoading() }
                .collect {
                    _authorizeLiveData.postValue(it)
                }
        }
    }
}
