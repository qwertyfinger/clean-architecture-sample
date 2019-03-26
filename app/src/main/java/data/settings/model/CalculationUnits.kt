/*
 * Copyright (c) 2019 Andrii Chubko
 */

package data.settings.model

import com.qwertyfinger.cleanarchitecturesample.R

enum class CalculationUnits(
  val resultValueResId: Int,
  val resultUnitResId: Int,
  val menuId: Int
) {

  MILE(
      R.string.expectancy_result,
      R.string.kilometer_hour,
      R.id.mileHour
  ),

  KILOMETER(
      R.string.expectancy_result,
      R.string.mile_hour,
      R.id.kilometerHour
  )

}