/*
 * Copyright (c) 2019 Andrii Chubko
 */

package data.calculator.repository

import domain.calculator.model.ExpectancyCalculationParams
import io.reactivex.Single
import java.math.BigDecimal

/**
 * Interface defining methods for the data operations related to expectancy.
 * This is to be implemented by external data source layers, setting the requirements for the
 * operations that need to be implemented.
 */
interface ExpectancyCalculatorDataStore {

  fun calculateExpectancy(calculationParams: ExpectancyCalculationParams): Single<BigDecimal>

}