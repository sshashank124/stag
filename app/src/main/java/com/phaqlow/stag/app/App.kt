package com.phaqlow.stag.app

import com.phaqlow.stag.app.dagger.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication


// TODO: run Leak Canary before finalizing
class App : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<App> = DaggerAppComponent.builder().create(this)
}