/*
 * Copyright (c) 2019 Andrii Chubko
 */

package domain.calculator.repository

import domain.calculator.model.ExpectancyCalculationParams
import io.reactivex.Single
import java.math.BigDecimal

/**
 * Interface defining methods for how the data layer can pass data to and from the Domain layer.
 * This is to be implemented by the data layer, setting the requirements for the
 * operations that need to be implemented
 */
interface ExpectancyCalculatorRepository {

  fun getCachedCalculationParams(): Single<ExpectancyCalculationParams>

  fun calculateExpectancy(calculationParams: ExpectancyCalculationParams?): Single<BigDecimal>

}