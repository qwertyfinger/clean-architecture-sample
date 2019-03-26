/*
 * Copyright (c) 2019 Andrii Chubko
 */

package data.calculator.repository

import domain.calculator.model.ExpectancyCalculationParams
import io.reactivex.Single
import java.math.BigDecimal

/**
 * Interface defining methods for getting calculation constants from the cache. This is to be
 * implemented by the cache layer, using this interface as a way of communicating.
 */
interface ExpectancyCalculatorCache {

  fun calculateExpectancy(calculationParams: ExpectancyCalculationParams): Single<BigDecimal>

}