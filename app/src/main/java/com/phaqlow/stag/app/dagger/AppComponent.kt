package com.phaqlow.stag.app.dagger

import com.phaqlow.stag.app.App
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class, ActivityBuilder::class])
interface AppComponent : AndroidInjector<App> {
    @Component.Builder abstract class Builder : AndroidInjector.Builder<App>()
}