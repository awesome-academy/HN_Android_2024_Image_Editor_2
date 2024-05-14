package com.example.imageEditor2.module

import com.example.imageEditor2.repository.AuthorizeRepository
import com.example.imageEditor2.ui.authorize.AuthorizeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.get
import org.koin.dsl.module

val myModule =
    module {
        // Api module
        single { NetworkModule.provideRetrofitInterface(androidContext()) }
        single { NetworkModule.providePostApi(get()) }
        single { NetworkModule.provideRetrofitAuthorizeInterface() }
        single { NetworkModule.providePostApiAuthorize(get()) }

        // Repository Module
        single { AuthorizeRepository(get()) }

        viewModel {
            AuthorizeViewModel(get())
        }
    }
