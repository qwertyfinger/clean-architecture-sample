/*
 * Copyright (c) 2019 Andrii Chubko
 */

package data

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import data.settings.model.CalculationUnits
import data.settings.model.CalculationUnits.MILE
import data.settings.model.Nationality
import data.settings.model.Nationality.JAP
import domain.calculator.model.ExpectancyCalculationParams
import domain.settings.model.CALCULATION_UNITS
import domain.settings.model.IS_FIRST_OPENING
import domain.settings.model.NATIONALITY
import ui.injection.scope.PerApplication
import ui.util.DAGGER_NAME_APP_CONTEXT
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Named

/**
 * General Preferences Helper class, used for storing preference values using the Preference API
 */
@PerApplication
class PreferencesHelper @Inject constructor(
  @Named(DAGGER_NAME_APP_CONTEXT) context: Context,
  moshi: Moshi
) {

  companion object {
    private const val PREF_FILE_NAME = "com.qwertyfinger.cleanarchitecturesample.preferences"
    private const val PREF_KEY_LAST_EXPECTANCY_PARAMS = "LAST_EXPECTANCY_PARAMS"
    private const val PREF_KEY_LAST_EXPECTANCY_RESULT = "LAST_EXPECTANCY_RESULT"
  }

  private val bufferPref: SharedPreferences
  private val expectancyCalculationParamsAdapter: JsonAdapter<ExpectancyCalculationParams>
  private val nationalityAdapter: JsonAdapter<Nationality>
  private val calculationUnitsAdapter: JsonAdapter<CalculationUnits>
  private val bigDecimalAdapter: JsonAdapter<BigDecimal>

  init {
    bufferPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    expectancyCalculationParamsAdapter = moshi.adapter(ExpectancyCalculationParams::class.java)
    nationalityAdapter = moshi.adapter(Nationality::class.java)
    calculationUnitsAdapter = moshi.adapter(CalculationUnits::class.java)
    bigDecimalAdapter = moshi.adapter(BigDecimal::class.java)
  }

  /**
   * Store and retrieve info regarding whether app has been launched at all previously.
   */
  var isFirstOpening: Boolean
    get() = bufferPref.getBoolean(IS_FIRST_OPENING, true)
    set(isFirstOpening) = bufferPref.save {
      putBoolean(IS_FIRST_OPENING, isFirstOpening)
    }

  /**
   * Store and retrieve last calculated expectancy.
   */
  var lastExpectancyResult: BigDecimal
    get() {
      val expectancyJson = bufferPref.getString(PREF_KEY_LAST_EXPECTANCY_RESULT, "")
      return if (expectancyJson.isNullOrEmpty()){
        BigDecimal.ZERO
      } else {
        bigDecimalAdapter.fromJson(expectancyJson) ?: BigDecimal.ZERO
      }
    }
    set(expectancyResult) = bufferPref.save {
      putString(
          PREF_KEY_LAST_EXPECTANCY_RESULT,
          bigDecimalAdapter.toJson(expectancyResult)
      )
    }

  /**
   * Store and retrieve current selected nationality.
   */
  var nationality: Nationality
    get() {
      val nationalityJson = bufferPref.getString(NATIONALITY, "")
      return if (nationalityJson.isNullOrEmpty()){
        JAP
      } else {
        nationalityAdapter.fromJson(nationalityJson) ?: JAP
      }
    }
    set(nationality) = bufferPref.save {
      putString(NATIONALITY, nationalityAdapter.toJson(nationality)
      )
    }

  /**
   * Store and retrieve current selected expectancy calculation units.
   */
  var calculationUnits: CalculationUnits
    get() {
      val calculationUnitsJson = bufferPref.getString(CALCULATION_UNITS, "")
      return if (calculationUnitsJson.isNullOrEmpty()){
        MILE
      } else {
        calculationUnitsAdapter.fromJson(calculationUnitsJson) ?: MILE
      }
    }
    set(calculationUnits) = bufferPref.save {
      putString(
          CALCULATION_UNITS,
          calculationUnitsAdapter.toJson(calculationUnits)
      )
    }

  /**
   * Store and retrieve last calculation parameters.
   */
  var lastExpectancyCalculationParams: ExpectancyCalculationParams
    get() {
      val paramsJson = bufferPref.getString(PREF_KEY_LAST_EXPECTANCY_PARAMS, "")
      return if (paramsJson.isNullOrEmpty()){
        ExpectancyCalculationParams()
      } else {
        expectancyCalculationParamsAdapter.fromJson(paramsJson) ?: ExpectancyCalculationParams()
      }
    }
    set(expectancyCalculationParams) = bufferPref.save {
      putString(
          PREF_KEY_LAST_EXPECTANCY_PARAMS,
          expectancyCalculationParamsAdapter.toJson(expectancyCalculationParams)
      )
    }
}

/**
 * Extension function to remove some boilerplate from SharedPreferences saving operations.
 */
private inline fun SharedPreferences.save(function: SharedPreferences.Editor.() -> Unit) {
  val editor = edit()
  editor.function()
  editor.apply()
}