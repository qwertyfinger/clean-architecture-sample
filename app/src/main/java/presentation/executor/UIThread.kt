/*
 * Copyright (c) 2019 Andrii Chubko
 */

package presentation.executor

import io.reactivex.Scheduler

/**
 * Thread abstraction created to change the execution context from any thread to any other thread.
 * Useful to encapsulate a UI Thread for example, since some job will be done in background, an
 * implementation of this interface will change context and update the UI.
 */
interface UIThread {
  val scheduler: Scheduler
}