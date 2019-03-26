/*
 * Copyright (c) 2019 Andrii Chubko
 */

package domain.settings.interactor

import domain.base.interactor.SingleUseCase
import domain.settings.repository.SettingsRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 * Use case used for getting current settings.
 */
class GetSettings @Inject constructor(
  private val settingsRepository: SettingsRepository
) : SingleUseCase<Map<String, Any>, Void?>() {

  public override fun buildUseCaseObservable(params: Void?): Single<Map<String, Any>> =
    settingsRepository.getSettings()

}