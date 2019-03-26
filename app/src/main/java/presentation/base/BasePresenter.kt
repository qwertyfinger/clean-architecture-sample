/*
 * Copyright (c) 2019 Andrii Chubko
 */

package presentation.base

/**
 * Interface class to act as a base for any class that is to take the role of the IPresenter in the
 * Model-BaseView-IPresenter pattern.
 */
interface BasePresenter {

  fun start()

  fun stop()

}