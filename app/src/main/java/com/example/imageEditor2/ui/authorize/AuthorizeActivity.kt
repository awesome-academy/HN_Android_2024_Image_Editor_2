package com.example.imageEditor2.ui.authorize

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.imageEditor.base.BaseActivity
import com.example.imageEditor2.databinding.ActivityAuthorizeBinding
import com.example.imageEditor2.utils.SIGN_OF_AUTHORIZE
import com.example.imageEditor2.utils.authorizeUrl
import com.example.imageEditor2.utils.toAuthorizationCode
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthorizeActivity : BaseActivity<ActivityAuthorizeBinding>() {
    private val viewModel: AuthorizeViewModel by viewModel()

    override fun getViewBinding(): ActivityAuthorizeBinding {
        return ActivityAuthorizeBinding.inflate(layoutInflater)
    }

    override fun initViewModel() = viewModel

    override fun initView() {
        binding.webView.loadUrl(authorizeUrl())
        binding.webView.webViewClient =
            object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?,
                ): Boolean {
                    if (request?.url.toString().contains(SIGN_OF_AUTHORIZE)) {
                        viewModel.authorize(request?.url.toString().toAuthorizationCode())
                    }
                    return false
                }
            }
    }

    override fun initListener() {
        // No-op
    }

    override fun observeData() {
        viewModel.authorizeLiveData.observe(this) {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}
