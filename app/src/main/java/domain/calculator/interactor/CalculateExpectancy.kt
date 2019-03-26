/*
 * Copyright (c) 2019 Andrii Chubko
 */

package domain.calculator.interactor

import domain.base.interactor.SingleUseCase
import domain.calculator.model.ExpectancyCalculationParams
import domain.calculator.repository.ExpectancyCalculatorRepository
import io.reactivex.Single
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Use case used for calculating expectancy of a something.
 */
class CalculateExpectancy @Inject constructor(
  private val expectancyCalculatorRepository: ExpectancyCalculatorRepository
) : SingleUseCase<BigDecimal, ExpectancyCalculationParams>() {

  public override fun buildUseCaseObservable(params: ExpectancyCalculationParams?): Single<BigDecimal> =
    expectancyCalculatorRepository.calculateExpectancy(params)

}