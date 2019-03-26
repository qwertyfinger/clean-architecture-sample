/*
 * Copyright (c) 2019 Andrii Chubko
 */

package ui

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import com.qwertyfinger.cleanarchitecturesample.BuildConfig
import com.squareup.leakcanary.LeakCanary
import data.PreferencesHelper
import timber.log.Timber
import ui.injection.component.ApplicationComponent
import ui.injection.component.DaggerApplicationComponent
import javax.inject.Inject



lateinit var injector : ApplicationComponent
  private set

class CleanArchitectureSampleApp : Application() {

  @Inject lateinit var preferencesHelper: PreferencesHelper

  override fun onCreate() {
    super.onCreate()
    setupLeakCanary()
    setupTimber()
    injectComponent()
    // Enable getting vector drawables from resources on API < 21
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
  }

  private fun injectComponent() {
    injector = DaggerApplicationComponent
        .builder()
        .application(this)
        .build()
    injector.inject(this)
  }

  private fun setupLeakCanary() {
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return
    }
    LeakCanary.install(this)
  }

  private fun setupTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }

}