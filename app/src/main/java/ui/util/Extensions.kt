/*
 * Copyright (c) 2019 Andrii Chubko
 */

@file:JvmName("Extensions")

package ui.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.annotation.IntegerRes
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Extension function for [Context] to shorten calls to get Integer resource value.
 */
fun Context.getInt(@IntegerRes resId: Int): Int = resources.getInteger(resId)

/**
 * Extension function for [CharSequence] to copy its content to the clipboard.
 */
fun CharSequence.copyToClipboard(context: Context) {
  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  val clip = ClipData.newPlainText("", this)
  clipboard.primaryClip = clip
}

fun View.showSoftKeyboard() {
  val inputMethodManager =
    context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
  inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}