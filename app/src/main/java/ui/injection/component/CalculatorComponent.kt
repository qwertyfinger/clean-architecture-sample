/*
 * Copyright (c) 2019 Andrii Chubko
 */

package ui.injection.component

import dagger.BindsInstance
import dagger.Subcomponent
import ui.calculator.expectancy.CalculateExpectancyActivity
import ui.injection.module.ExpectancyModule
import ui.injection.scope.PerActivity

@PerActivity
@Subcomponent(modules = [(ExpectancyModule::class)])
interface CalculatorComponent {

  fun inject(activity: CalculateExpectancyActivity)

  @Subcomponent.Builder
  interface Builder {
    @BindsInstance fun activity(activity: CalculateExpectancyActivity): Builder
    fun build(): CalculatorComponent
  }

}