package com.example.imageEditor2.module

import androidx.room.Room
import com.example.imageEditor2.MyPreference
import com.example.imageEditor2.download.DownloadService
import com.example.imageEditor2.download.DownloadServiceImpl
import com.example.imageEditor2.repository.authorize.AuthorizeRepository
import com.example.imageEditor2.repository.authorize.AuthorizeRepositoryImpl
import com.example.imageEditor2.repository.create.CreateImageRepository
import com.example.imageEditor2.repository.create.CreateImageRepositoryImpl
import com.example.imageEditor2.repository.detail.DetailRepository
import com.example.imageEditor2.repository.detail.DetailRepositoryImpl
import com.example.imageEditor2.repository.favorite.FavoriteRepository
import com.example.imageEditor2.repository.favorite.FavoriteRepositoryImpl
import com.example.imageEditor2.repository.home.HomeRepository
import com.example.imageEditor2.repository.home.HomeRepositoryImpl
import com.example.imageEditor2.repository.search.SearchRepository
import com.example.imageEditor2.repository.search.SearchRepositoryImpl
import com.example.imageEditor2.room.AppDatabase
import com.example.imageEditor2.ui.authorize.AuthorizeViewModel
import com.example.imageEditor2.ui.create.CreateImageViewModel
import com.example.imageEditor2.ui.detail.DetailViewModel
import com.example.imageEditor2.ui.favorite.FavoriteViewModel
import com.example.imageEditor2.ui.home.HomeViewModel
import com.example.imageEditor2.ui.main.MainViewModel
import com.example.imageEditor2.ui.search.SearchViewModel
import com.example.imageEditor2.utils.DATABASE_NAME
import com.example.imageEditor2.utils.RETROFIT
import com.example.imageEditor2.utils.RETROFIT_AUTHORIZE
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val myModule =
    module {
        // Api module
        single(named(RETROFIT)) { NetworkModule.provideRetrofitInterface(androidContext()) }
        single { NetworkModule.providePostApi(get(named(RETROFIT))) }
        single(named(RETROFIT_AUTHORIZE)) { NetworkModule.provideRetrofitAuthorizeInterface() }
        single { NetworkModule.providePostApiAuthorize(get(named(RETROFIT_AUTHORIZE))) }

        // SharePreference
        single { MyPreference(androidContext()) }

        // Room
        single {
            Room.databaseBuilder(
                androidApplication(),
                AppDatabase::class.java,
                DATABASE_NAME,
            ).build()
        }
        // HistorySearchDAO
        single { get<AppDatabase>().historySearchDao() }

        // Download
        single<DownloadService> { DownloadServiceImpl(androidContext()) }

        // Repository Module
        single<AuthorizeRepository> { AuthorizeRepositoryImpl(get()) }
        single<HomeRepository> { HomeRepositoryImpl(get()) }
        single<DetailRepository> { DetailRepositoryImpl(get()) }
        single<CreateImageRepository> { CreateImageRepositoryImpl(get()) }
        single<FavoriteRepository> { FavoriteRepositoryImpl(get()) }
        single<SearchRepository> { SearchRepositoryImpl(get(), get()) }

        viewModel {
            AuthorizeViewModel(get(), get())
        }
        viewModel { MainViewModel() }
        viewModel { HomeViewModel(get()) }
        viewModel { DetailViewModel(get()) }
        viewModel { CreateImageViewModel(get()) }
        viewModel { FavoriteViewModel(get()) }
        viewModel { SearchViewModel(get()) }
    }
