/*
 * Copyright (c) 2019 Andrii Chubko
 */

package data.settings

import dagger.Reusable
import data.PreferencesHelper
import data.settings.model.CalculationUnits
import data.settings.model.Nationality
import domain.settings.model.CALCULATION_UNITS
import domain.settings.model.IS_FIRST_OPENING
import domain.settings.model.NATIONALITY
import domain.settings.repository.SettingsRepository
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

/**
 * Provides an implementation of the [SettingsRepository] interface for communicating to and from
 * data source.
 */
@Reusable
class SettingsDataRepository
@Inject constructor(
  private val preferences: PreferencesHelper
) : SettingsRepository {

  override fun saveSettings(params: Map<String, Any>): Completable {
    return Completable.fromAction {
      for ((key, param) in params) {
        when (key) {
          IS_FIRST_OPENING -> if (param is Boolean) setFirstOpening(param)
          NATIONALITY -> if (param is Nationality) saveNationality(param)
          CALCULATION_UNITS -> if (param is CalculationUnits) saveCalculationUnits(param)
        }
      }
    }.doOnSubscribe {
      Timber.d("Saving settings: %s", params)
      Timber.d("Thread: %s", Thread.currentThread().toString())
    }
  }

  override fun getSettings(): Single<Map<String, Any>> {
    return Single.just(
        mapOf(
            IS_FIRST_OPENING to isFirstOpening(),
            NATIONALITY to getNationality(),
            CALCULATION_UNITS to getCalculationUnits()
        )
    ).doOnSuccess {
      Timber.d("getSettings: %s", it.toString())
      Timber.d("Thread: %s", Thread.currentThread().toString())
    }
  }

  private fun getNationality(): Nationality = preferences.nationality

  private fun saveNationality(nationality: Nationality) {
    preferences.nationality = nationality
  }

  private fun getCalculationUnits(): CalculationUnits = preferences.calculationUnits

  private fun saveCalculationUnits(calculationUnits: CalculationUnits) {
    preferences.calculationUnits = calculationUnits
  }

  private fun isFirstOpening(): Boolean = preferences.isFirstOpening

  private fun setFirstOpening(isFirstOpening: Boolean) {
    preferences.isFirstOpening = isFirstOpening
  }

}