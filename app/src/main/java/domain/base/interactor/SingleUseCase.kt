/*
 * Copyright (c) 2019 Andrii Chubko
 */

package domain.base.interactor

import io.reactivex.Scheduler
import io.reactivex.Single
import timber.log.Timber

/**
 * Abstract class for a UseCase that returns an instance of a [Single].
 */
abstract class SingleUseCase<T, in Params> {

  /**
   * Builds a [Single] which will be used when the current [SingleUseCase] is executed.
   */
  protected abstract fun buildUseCaseObservable(params: Params? = null): Single<T>

  /**
   * Returns [Single] for the current use case.
   */
  fun execute(
    params: Params? = null,
    executionThread: Scheduler? = null,
    postExecutionThread: Scheduler? = null
  ): Single<T> {
    return buildUseCaseObservable(params)
        .doOnError { Timber.e(it) }
        .subscribeOnIfNotNull(executionThread)
        .observeOnIfNotNull(postExecutionThread)
  }

  private fun Single<T>.subscribeOnIfNotNull(scheduler: Scheduler?) : Single<T> {
    return if (scheduler != null) {
      subscribeOn(scheduler)
    } else {
      this
    }
  }

  private fun Single<T>.observeOnIfNotNull(scheduler: Scheduler?) : Single<T> {
    return if (scheduler != null) {
      observeOn(scheduler)
    } else {
      this
    }
  }

}