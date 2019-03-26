/*
 * Copyright (c) 2019 Andrii Chubko
 */

package data.calculator.source

import dagger.Reusable
import data.calculator.repository.ExpectancyCalculatorDataStore
import javax.inject.Inject

/**
 * Create an instance of a ExpectancyCalculatorDataStore
 */
@Reusable
class ExpectancyCalculatorDataStoreFactory @Inject constructor(
  private val expectancyCalculatorCacheDataStore: ExpectancyCalculatorCacheDataStore
) {

  /**
   * Currently returns an instance of the Cache Data Store. In the future logic might be introduced
   * to return other Data Store in some cases.
   */
  fun retrieveDataStore(): ExpectancyCalculatorDataStore {
    return expectancyCalculatorCacheDataStore
  }

  /**
   * Returns an instance of the Cache Data Store
   */
  fun retrieveCacheDataStore(): ExpectancyCalculatorDataStore {
    return expectancyCalculatorCacheDataStore
  }
}