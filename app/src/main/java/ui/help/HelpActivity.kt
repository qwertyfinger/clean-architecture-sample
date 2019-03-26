/*
 * Copyright (c) 2019 Andrii Chubko
 */

package ui.help

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.qwertyfinger.cleanarchitecturesample.R
import kotlinx.android.synthetic.main.activity_help.toolbar

class HelpActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_help)
    setSupportActionBar(toolbar)
    supportActionBar?.setTitle(R.string.title_activity_help)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
  }

}
