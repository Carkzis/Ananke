package com.carkzis.ananke.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * The default dispatcher used is the UnconfinedTestDispatcher, which automatically runs coroutines.
 * If you need full control over running coroutines, use the StandardTestDispatcher.
 */
@ExperimentalCoroutinesApi
class MainDispatcherRule(val dispatcher: TestDispatcher = UnconfinedTestDispatcher()): TestWatcher() {

    override fun starting(description: Description?) = Dispatchers.setMain(dispatcher)

    override fun finished(description: Description?) = Dispatchers.resetMain()

}