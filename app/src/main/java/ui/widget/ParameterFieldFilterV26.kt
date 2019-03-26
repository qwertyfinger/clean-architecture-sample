/*
 * Copyright (c) 2019 Andrii Chubko
 */

package ui.widget

import android.annotation.TargetApi
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.DigitsKeyListener
import java.util.Locale

@TargetApi(value = Build.VERSION_CODES.O)
class ParameterFieldFilterV26 : DigitsKeyListener(Locale.ENGLISH, false, true) {

  private val decimalPlaces = 2

  override fun filter(
    source: CharSequence,
    start: Int,
    end: Int,
    dest: Spanned,
    dstart: Int,
    dend: Int
  ): CharSequence {
    var sourceVar = source
    var startVar = start
    var endVar = end
    val out = super.filter(sourceVar, startVar, endVar, dest, dstart, dend)

    // if changed, replace the source
    if (out != null) {
      sourceVar = out
      startVar = 0
      endVar = out.length
    }

    var sourceLength = endVar - startVar

    // if deleting, source is empty
    // and deleting can't break anything
    if (sourceLength == 0) {
      return sourceVar
    }

    val dlength = dest.length

    // if '.' is the first symbol, then put a '0' before it
    if ((dest.isEmpty() || (dstart == 0 && dend == dlength - 1)) && sourceVar[startVar] == '.') {
      sourceVar = "0$sourceVar"
      endVar++
    }

    sourceLength = endVar - startVar

    // check whether source has a '.' as a first or second char
    val sourceHasDot = sourceVar[startVar] == '.'
        || (sourceLength > startVar + 1 && sourceVar[startVar+1] == '.')

    // don't allow inserting anything except the '.' if current text starts with '0'
    if (dest.isNotEmpty() && dest[0] == '0') {
      val destDotIndex = dest.indexOf('.')
      if ((dstart in 1..destDotIndex || destDotIndex == -1) && !sourceHasDot) {
        return ""
      }
    }

    // when inserting string at the beginning, remove zeros from its start
    val fieldIsNotEmpty = sourceLength > 1 || dest.isNotEmpty()
    if (dstart == 0 && fieldIsNotEmpty && sourceVar[startVar] == '0' && !sourceHasDot) {
      for (i in startVar until endVar) {
        if (sourceVar[i] != '0') {
          // add back one '0' symbol because it is followed by the '.'
          if (sourceVar[i] == '.') startVar --
          break
        }
        startVar++
      }
      // add back one '0' symbol because it is followed by the '.'
      val destEnd = if (dend == 0) dend else dend - 1
      if (dlength > 0 && dest[destEnd] == '.') startVar--
    }

    sourceLength = endVar - startVar

    // Find the position of the decimal .
    for (i in 0 until dstart) {
      if (dest[i] == '.') {
        // being here means, that a number has
        // been inserted after the dot
        // check if the amount of decimal places is right
        return if (dlength - (i + 1) + sourceLength > decimalPlaces)
          ""
        else
          SpannableStringBuilder(sourceVar, startVar, endVar)
      }
    }

    for (i in startVar until endVar) {
      if (sourceVar[i] == '.') {
        // being here means, dot has been inserted
        // check if the amount of decimal places is right
        return if (dlength - dend + (endVar - (i + 1)) > decimalPlaces)
          ""
        else
          break
      }
    }

    // if the dot is after the inserted part,
    // nothing can break
    return SpannableStringBuilder(sourceVar, startVar, endVar)
  }
}