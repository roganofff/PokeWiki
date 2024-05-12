package com.trainee.pokewiki.app

import android.app.Application
import com.trainee.pokewiki.di.appModule
import com.trainee.pokewiki.di.dataModule
import com.trainee.pokewiki.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(listOf(appModule, dataModule, domainModule))
        }
    }
}