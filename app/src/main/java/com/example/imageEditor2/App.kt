package com.example.imageEditor2

import android.app.Application
import com.example.imageEditor2.module.myModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            androidContext(this@App)
            modules(myModule)
        }
    }

    companion object {
        lateinit var instance: App
    }
}
