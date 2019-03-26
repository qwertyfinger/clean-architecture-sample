/*
 * Copyright (c) 2019 Andrii Chubko
 */

package data.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import data.settings.model.CalculationUnits
import data.settings.model.CalculationUnits.MILE
import data.settings.model.Nationality
import data.settings.model.Nationality.JAP

class EnumJsonAdapter {

  @ToJson fun nationalityToJson(nationality: Nationality) = nationality.name
  @FromJson fun nationalityFromJson(nationality: String) = Nationality.values().find {
    it.name == nationality
  } ?: JAP

  @ToJson fun calculationUnitsToJson(calculationUnits: CalculationUnits) = calculationUnits.name
  @FromJson fun calculationUnitsFromJson(calculationUnits: String) = CalculationUnits.values().find {
    it.name == calculationUnits
  } ?: MILE

}