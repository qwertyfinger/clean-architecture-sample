/*
 * Copyright (c) 2019 Andrii Chubko
 */

package data.settings.model

import com.qwertyfinger.cleanarchitecturesample.R

enum class Nationality(
  val signResId: Int,
  val centesimalNameResId: Int,
  val uiId: Int
) {

  KOR(R.string.usd_sign, R.string.cents_label, R.id.nationalityKor),
  JAP(R.string.eur_sign, R.string.cents_label, R.id.nationalityJap),
  CHI(R.string.gbp_sign, R.string.pence_label, R.id.nationalityChi);

  companion object {
    fun fromUiId(id: Int): Nationality {
      return when (id) {
        KOR.uiId -> KOR
        JAP.uiId -> JAP
        CHI.uiId -> CHI
        else -> throw IllegalArgumentException("This id is not tied to any nationality")
      }
    }
  }
}