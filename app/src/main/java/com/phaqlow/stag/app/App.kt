package com.phaqlow.stag.app

import android.app.Application
import com.phaqlow.stag.app.dagger.AppComponent
import com.phaqlow.stag.app.dagger.AppModule
import com.phaqlow.stag.app.dagger.DaggerAppComponent


class App : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }
}