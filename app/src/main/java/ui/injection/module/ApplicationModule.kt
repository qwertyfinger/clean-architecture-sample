/*
 * Copyright (c) 2019 Andrii Chubko
 */

package ui.injection.module

import android.app.Application
import android.content.Context
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import data.jsonadapter.ApplicationJsonAdapterFactory
import data.jsonadapter.BigDecimalJsonAdapter
import data.jsonadapter.EnumJsonAdapter
import data.calculator.ExpectancyCalculatorDataRepository
import data.calculator.cache.ExpectancyCalculatorCacheImpl
import data.calculator.repository.ExpectancyCalculatorCache
import data.settings.SettingsDataRepository
import domain.calculator.repository.ExpectancyCalculatorRepository
import domain.settings.repository.SettingsRepository
import presentation.executor.UIThread
import ui.UIThreadImpl
import ui.injection.component.CalculatorComponent
import ui.util.DAGGER_NAME_APP_CONTEXT
import javax.inject.Named

@Module(subcomponents = [(CalculatorComponent::class)])
abstract class ApplicationModule {

  @Binds
  @Named(DAGGER_NAME_APP_CONTEXT)
  abstract fun provideContext(application: Application): Context

  @Binds
  abstract fun provideCalculatorDataRepository(
    calculatorDataRepository: ExpectancyCalculatorDataRepository
  ) : ExpectancyCalculatorRepository

  @Binds
  abstract fun provideSettingsDataRepository(
    settingsDataRepository: SettingsDataRepository
  ) : SettingsRepository

  @Binds
  abstract fun provideConstantsCacheImpl(
    calculatorCacheImpl: ExpectancyCalculatorCacheImpl
  ) : ExpectancyCalculatorCache

  @Binds
  abstract fun providePostExecutionThread(uiThread: UIThreadImpl) : UIThread

  @Module(subcomponents = [(CalculatorComponent::class)])
  companion object {

    @JvmStatic
    @Reusable
    @Provides
    fun provideMoshi(): Moshi {
      return Moshi.Builder()
          .add(ApplicationJsonAdapterFactory.INSTANCE)
          .add(BigDecimalJsonAdapter())
          .add(EnumJsonAdapter())
          .build()
    }

  }

}