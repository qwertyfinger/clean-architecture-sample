/*
 * Copyright (c) 2019 Andrii Chubko
 */

package data.calculator

import dagger.Reusable
import data.PreferencesHelper
import data.calculator.source.ExpectancyCalculatorDataStoreFactory
import domain.calculator.model.ExpectancyCalculationParams
import domain.calculator.repository.ExpectancyCalculatorRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Provides an implementation of the [ExpectancyCalculatorRepository] interface for communicating to and from
 * data sources.
 */
@Reusable
class ExpectancyCalculatorDataRepository
@Inject constructor(
  private val factoryCalculator: ExpectancyCalculatorDataStoreFactory,
  private val preferencesHelper: PreferencesHelper
) : ExpectancyCalculatorRepository {

  override fun getCachedCalculationParams(): Single<ExpectancyCalculationParams> {
    return Single.just(preferencesHelper.lastExpectancyCalculationParams)
        .doOnSuccess {
          Timber.d("getCachedCalculationParams = %s", it.toString())
          Timber.d("Thread: %s", Thread.currentThread().toString())
        }
  }

  override fun calculateExpectancy(calculationParams: ExpectancyCalculationParams?): Single<BigDecimal> {
    return if (calculationParams == null) {
      Single.just(BigDecimal.ZERO)
    } else {
      getCachedCalculationParams()
          .flatMap {
            if (it == calculationParams) {
              Single.just(getLastExpectancyResult())
            } else {
              factoryCalculator.retrieveDataStore()
                  .calculateExpectancy(calculationParams)
                  .observeOn(Schedulers.io())
                  .doOnSubscribe { saveLastCalculationParams(calculationParams) }
                  .doOnSuccess { saveLastExpectancyResult(it) }
            }
          }
          .doOnError { saveLastExpectancyResult(BigDecimal.ZERO) }
    }
  }

  private fun getLastExpectancyResult(): BigDecimal {
    val lastResult = preferencesHelper.lastExpectancyResult
    Timber.d("getLastExpectancyResult = %s", lastResult.toEngineeringString())
    Timber.d("Thread: %s", Thread.currentThread().toString())
    return lastResult
  }

  private fun saveLastExpectancyResult(result: BigDecimal) {
    Timber.d("saveLastExpectancyResult = %s", result.toEngineeringString())
    Timber.d("Thread: %s", Thread.currentThread().toString())
    preferencesHelper.lastExpectancyResult = result
  }

  private fun saveLastCalculationParams(params: ExpectancyCalculationParams?) {
    Timber.d("saveLastCalculationParams = %s", params.toString())
    Timber.d("Thread: %s", Thread.currentThread().toString())
    params?.let { preferencesHelper.lastExpectancyCalculationParams = params }
  }

}