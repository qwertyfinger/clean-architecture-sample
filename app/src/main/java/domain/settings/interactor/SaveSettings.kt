/*
 * Copyright (c) 2019 Andrii Chubko
 */

package domain.settings.interactor

import domain.base.interactor.CompletableUseCase
import domain.settings.repository.SettingsRepository
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Use case used for getting current settings.
 */
class SaveSettings @Inject constructor(
  private val settingsRepository: SettingsRepository
) : CompletableUseCase<Map<String, Any>>() {

  public override fun buildUseCaseObservable(params: Map<String, Any>): Completable =
    settingsRepository.saveSettings(params)

}