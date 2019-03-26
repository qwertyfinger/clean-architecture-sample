/*
 * Copyright (c) 2019 Andrii Chubko
 */

package ui.injection.component

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ui.CleanArchitectureSampleApp
import ui.injection.module.ApplicationModule
import ui.injection.scope.PerApplication
import ui.util.DAGGER_NAME_APP_CONTEXT
import javax.inject.Named

@PerApplication
@Component(modules = [(ApplicationModule::class)])
interface ApplicationComponent {

  fun inject(application: CleanArchitectureSampleApp)

  @Named(DAGGER_NAME_APP_CONTEXT)
  fun appContext(): Context

  fun calculatorComponentBuilder(): CalculatorComponent.Builder

  @Component.Builder
  interface Builder {
    @BindsInstance fun application(application: Application): Builder
    fun build(): ApplicationComponent
  }

}