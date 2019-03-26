/*
 * Copyright (c) 2019 Andrii Chubko
 */

package data.jsonadapter

import com.squareup.moshi.JsonAdapter
import se.ansman.kotshi.KotshiJsonAdapterFactory

@KotshiJsonAdapterFactory
abstract class ApplicationJsonAdapterFactory : JsonAdapter.Factory {
  companion object {
    val INSTANCE: ApplicationJsonAdapterFactory = KotshiApplicationJsonAdapterFactory()
  }
}