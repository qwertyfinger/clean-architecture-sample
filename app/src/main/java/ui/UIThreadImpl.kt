/*
 * Copyright (c) 2019 Andrii Chubko
 */

package ui

import dagger.Reusable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import presentation.executor.UIThread
import javax.inject.Inject

/**
 * MainThread (UI Thread) implementation based on a [Scheduler]
 * which will execute actions on the Android UI thread
 */
@Reusable
class UIThreadImpl
@Inject internal constructor() : UIThread {

  override val scheduler: Scheduler
    get() = AndroidSchedulers.mainThread()

}