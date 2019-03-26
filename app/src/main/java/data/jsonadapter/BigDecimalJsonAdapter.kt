/*
 * Copyright (c) 2019 Andrii Chubko
 */

package data.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.math.BigDecimal

class BigDecimalJsonAdapter {

  @ToJson fun toJson(bigDecimal: BigDecimal): String {
    return bigDecimal.toEngineeringString()
  }

  @FromJson fun fromJson(bigDecimalJson: String): BigDecimal {
    return BigDecimal(bigDecimalJson)
  }

}