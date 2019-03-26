/*
 * Copyright (c) 2019 Andrii Chubko
 */

package domain.calculator.interactor

import domain.base.interactor.SingleUseCase
import domain.calculator.model.ExpectancyCalculationParams
import domain.calculator.repository.ExpectancyCalculatorRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 * Use case used for getting current expectancy calculation units.
 */
class GetCalculationParams @Inject constructor(
  private val expectancyCalculatorRepository: ExpectancyCalculatorRepository
) : SingleUseCase<ExpectancyCalculationParams, Void?>() {

  public override fun buildUseCaseObservable(params: Void?): Single<ExpectancyCalculationParams> =
    expectancyCalculatorRepository.getCachedCalculationParams()

}