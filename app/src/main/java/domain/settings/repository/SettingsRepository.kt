/*
 * Copyright (c) 2019 Andrii Chubko
 */

package domain.settings.repository

import io.reactivex.Completable
import io.reactivex.Single

/**
 * Interface defining methods for how the data layer can pass data to and from the Domain layer.
 * This is to be implemented by the data layer, setting the requirements for the
 * operations that need to be implemented.
 */
interface SettingsRepository {

  fun saveSettings(params: Map<String, Any>): Completable

  fun getSettings(): Single<Map<String, Any>>

}