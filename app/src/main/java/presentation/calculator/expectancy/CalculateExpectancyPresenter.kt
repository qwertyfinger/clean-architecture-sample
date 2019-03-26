/*
 * Copyright (c) 2019 Andrii Chubko
 */

package presentation.calculator.expectancy

import data.settings.model.CalculationUnits
import data.settings.model.Nationality
import domain.calculator.interactor.CalculateExpectancy
import domain.calculator.interactor.GetCalculationParams
import domain.calculator.model.ExpectancyCalculationParams
import domain.settings.interactor.GetSettings
import domain.settings.interactor.SaveSettings
import domain.settings.model.CALCULATION_UNITS
import domain.settings.model.IS_FIRST_OPENING
import domain.settings.model.NATIONALITY
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import presentation.executor.UIThread
import timber.log.Timber
import ui.injection.scope.PerActivity
import java.math.BigDecimal
import javax.inject.Inject

@PerActivity
class CalculateExpectancyPresenter
@Inject constructor(
  private val view: CalculateExpectancyContract.View,
  private val calculateExpectancyUseCase: CalculateExpectancy,
  private val getCachedCalculationParams: GetCalculationParams,
  private val getSettings: GetSettings,
  private val saveSettings: SaveSettings,
  private val uiThread: UIThread
) : CalculateExpectancyContract.Presenter {

  private val compositeDisposable: CompositeDisposable = CompositeDisposable()

  override fun start() {
    getCalculatorData()
  }

  override fun stop() {
    compositeDisposable.clear()
  }

  override fun calculateExpectancy(expectancyCalculationParams: ExpectancyCalculationParams) {
    calculateExpectancyUseCase
        .execute(
            params = expectancyCalculationParams,
            executionThread = Schedulers.io(),
            postExecutionThread = uiThread.scheduler
        )
        .subscribeBy (
            onError = { view.showResult(BigDecimal.ZERO) },
            onSuccess = {
              view.showResult(it)
              Timber.d("Expectancy = %s", it.toEngineeringString())
            }
        )
        .addTo(compositeDisposable)
  }

  override fun saveNationality(nationality: Nationality) {
    saveSettings
        .execute(
            params = mapOf(NATIONALITY to nationality),
            executionThread = Schedulers.io(),
            postExecutionThread = uiThread.scheduler
        )
        .subscribeBy(onError = { view.showSettingsSavingError() })
        .addTo(compositeDisposable)
  }

  override fun saveCalculationUnits(calculationUnits: CalculationUnits) {
    saveSettings
        .execute(
            params = mapOf(CALCULATION_UNITS to calculationUnits),
            executionThread = Schedulers.io(),
            postExecutionThread = uiThread.scheduler
        )
        .subscribeBy(onError = { view.showSettingsSavingError() })
        .addTo(compositeDisposable)
  }

  override fun saveFirstOpening(isFirstOpening: Boolean) {
    saveSettings
        .execute(
            params = mapOf(IS_FIRST_OPENING to isFirstOpening),
            executionThread = Schedulers.io(),
            postExecutionThread = uiThread.scheduler
        )
        .subscribeBy(onError = { view.showSettingsSavingError() })
        .addTo(compositeDisposable)
  }

  private fun getCalculatorData() {
    getSettings
        .execute(
            executionThread = Schedulers.io(),
            postExecutionThread = uiThread.scheduler
        )
        .doOnSuccess {
          if (it[IS_FIRST_OPENING] as? Boolean == true) {
            view.showWelcomeDialog()
          }
          view.setNationality(it[NATIONALITY] as? Nationality, false)
          view.setCalculationUnits(it[CALCULATION_UNITS] as? CalculationUnits)
        }
        .flatMap { getCachedCalculationParams
            .execute(
                executionThread = Schedulers.io(),
                postExecutionThread = uiThread.scheduler
            )
            .doOnSuccess{ view.showCalculationParams(it) }
        }
        .subscribeBy(
            onError = {
              Timber.e(it)
              view.showCacheLoadingError()
            },
            onSuccess = { calculateExpectancy(it) }
        )
        .addTo(compositeDisposable)
  }

}