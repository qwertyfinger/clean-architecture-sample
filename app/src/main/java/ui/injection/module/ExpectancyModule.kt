/*
 * Copyright (c) 2019 Andrii Chubko
 */

package ui.injection.module

import dagger.Binds
import dagger.Module
import presentation.calculator.expectancy.CalculateExpectancyContract
import presentation.calculator.expectancy.CalculateExpectancyPresenter
import ui.calculator.expectancy.CalculateExpectancyActivity

@Module
abstract class ExpectancyModule {

  @Binds
  abstract fun provideCalculatorView(
    activity: CalculateExpectancyActivity
  ): CalculateExpectancyContract.View

  @Binds
  abstract fun provideCalculatorPresenter(
    calculateExpectancyPresenter: CalculateExpectancyPresenter
  ) : CalculateExpectancyContract.Presenter

}