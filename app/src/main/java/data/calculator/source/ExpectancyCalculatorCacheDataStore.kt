/*
 * Copyright (c) 2019 Andrii Chubko
 */

package data.calculator.source

import dagger.Reusable
import data.calculator.repository.ExpectancyCalculatorCache
import data.calculator.repository.ExpectancyCalculatorDataStore
import domain.calculator.model.ExpectancyCalculationParams
import io.reactivex.Single
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Implementation of the [ExpectancyCalculatorDataStore] interface to provide a means of communicating
 * with the local data source.
 */
@Reusable
class ExpectancyCalculatorCacheDataStore @Inject constructor(
  private val expectancyCalculatorCache: ExpectancyCalculatorCache
) : ExpectancyCalculatorDataStore {

  override fun calculateExpectancy(calculationParams: ExpectancyCalculationParams): Single<BigDecimal> =
    expectancyCalculatorCache.calculateExpectancy(calculationParams)

}