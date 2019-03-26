/*
 * Copyright (c) 2019 Andrii Chubko
 */

package presentation.calculator.expectancy

import data.settings.model.CalculationUnits
import data.settings.model.Nationality
import domain.calculator.model.ExpectancyCalculationParams
import presentation.base.BasePresenter
import presentation.base.BaseView
import java.math.BigDecimal

/**
 * Defines a contract of operations between the Calculate Expectancy Presenter and Calculate Expectancy View
 */
interface CalculateExpectancyContract {

  interface View: BaseView {

    fun showWelcomeDialog()

    fun showCalculationParams(params: ExpectancyCalculationParams)

    fun showResult(result: BigDecimal)

    fun showCalculationError()

    fun showCacheLoadingError()

    fun showSettingsSavingError()

    fun setNationality(nationality: Nationality?, notifyListener: Boolean)

    fun setCalculationUnits(calculationUnits: CalculationUnits?)

  }

  interface Presenter : BasePresenter {

    fun calculateExpectancy(expectancyCalculationParams: ExpectancyCalculationParams)

    fun saveNationality(nationality: Nationality)

    fun saveCalculationUnits(calculationUnits: CalculationUnits)

    fun saveFirstOpening(isFirstOpening: Boolean)

  }

}