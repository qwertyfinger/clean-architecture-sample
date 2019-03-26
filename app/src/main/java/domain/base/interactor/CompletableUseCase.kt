/*
 * Copyright (c) 2019 Andrii Chubko
 */

package domain.base.interactor

import io.reactivex.Completable
import io.reactivex.Scheduler
import timber.log.Timber

/**
 * Abstract class for a UseCase that returns an instance of a [Completable].
 */
abstract class CompletableUseCase<in Params> protected constructor() {

  /**
   * Builds a [Completable] which will be used when the current [CompletableUseCase] is executed.
   */
  protected abstract fun buildUseCaseObservable(params: Params): Completable

  /**
   * Returns [Completable] for the current use case.
   */
  fun execute(
    params: Params,
    executionThread: Scheduler? = null,
    postExecutionThread: Scheduler? = null
  ): Completable {
    return buildUseCaseObservable(params)
        .doOnError { Timber.e(it) }
        .subscribeOnIfNotNull(executionThread)
        .observeOnIfNotNull(postExecutionThread)
  }

  private fun Completable.subscribeOnIfNotNull(scheduler: Scheduler?) : Completable {
    return if (scheduler != null) {
      subscribeOn(scheduler)
    } else {
      this
    }
  }

  private fun Completable.observeOnIfNotNull(scheduler: Scheduler?) : Completable {
    return if (scheduler != null) {
      observeOn(scheduler)
    } else {
      this
    }
  }

}