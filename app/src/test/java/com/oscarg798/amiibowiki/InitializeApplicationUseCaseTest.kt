/*
 * Copyright 2021 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.oscarg798.amiibowiki

import com.oscarg798.amiibowiki.core.failures.AmiiboTypeFailure
import com.oscarg798.amiibowiki.core.failures.GameAPIAuthenticationFailure
import com.oscarg798.amiibowiki.core.usecases.UpdateAmiiboTypeUseCase
import com.oscarg798.amiibowiki.splash.usecases.ActivateRemoteConfigUseCase
import com.oscarg798.amiibowiki.splash.usecases.AuthenticateApplicationUseCase
import com.oscarg798.amiibowiki.splash.usecases.InitializeApplicationUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.amiibowiki.testutils.utils.TestCollector
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.After
import org.junit.Before
import org.junit.Test

class InitializeApplicationUseCaseTest {

    private lateinit var testCollector: TestCollector<Unit>
    private val dispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope()

    private lateinit var usecase: InitializeApplicationUseCase

    private val updateAmiiboTypeUseCase: UpdateAmiiboTypeUseCase = relaxedMockk()
    private val activateRemoteConfigUseCase: ActivateRemoteConfigUseCase = relaxedMockk()
    private val authenticateApplicationUseCase: AuthenticateApplicationUseCase = relaxedMockk()

    @Before
    fun setup() {
        testCollector = TestCollector()

        coEvery { activateRemoteConfigUseCase.execute() } just Runs
        coEvery { updateAmiiboTypeUseCase.execute() } just Runs
        coEvery { authenticateApplicationUseCase.execute() } just Runs

        usecase = InitializeApplicationUseCase(
            updateAmiiboTypeUseCase,
            activateRemoteConfigUseCase,
            authenticateApplicationUseCase
        )
    }

    @After
    fun tearDown() {
        dispatcher.cancel()
    }

    @Test
    fun when_its_executed_and_all_bootstrapactions_got_executed_correctly_then_it_should_emit_unit() {
        testCollector.test(testCoroutineScope, usecase.execute())

        testCollector wasValueEmiited Unit
    }

    @Test
    fun when_its_executed_and_there_is_an_error_in_activity_authentication_usecase_then_it_should_not_emit_unit() {
        coEvery { authenticateApplicationUseCase.execute() } throws GameAPIAuthenticationFailure.TokenNotAvailable()

        testCollector.test(testCoroutineScope, usecase.execute())

        testCollector valueWasNotEmitted Unit
        testCoroutineScope.uncaughtExceptions.size shouldBeEqualTo 1
        testCoroutineScope.uncaughtExceptions.first() shouldBeInstanceOf GameAPIAuthenticationFailure.TokenNotAvailable::class.java
    }

    @Test
    fun when_its_executed_and_there_is_an_error_in_activity_remote_config_usecase_then_it_should_not_emit_unit() {
        coEvery { activateRemoteConfigUseCase.execute() } throws IllegalArgumentException()

        testCollector.test(testCoroutineScope, usecase.execute())

        testCollector valueWasNotEmitted Unit
        testCoroutineScope.uncaughtExceptions.size shouldBeEqualTo 1
        testCoroutineScope.uncaughtExceptions.first() shouldBeInstanceOf IllegalArgumentException::class.java
    }

    @Test
    fun when_its_executed_and_there_is_an_error_in_activity_update_amiibos_usecase_then_it_should_not_emit_unit() {
        coEvery { updateAmiiboTypeUseCase.execute() } throws AmiiboTypeFailure.FetchTypesFailure()

        testCollector.test(testCoroutineScope, usecase.execute())

        testCollector valueWasNotEmitted Unit
        testCoroutineScope.uncaughtExceptions.size shouldBeEqualTo 1
        testCoroutineScope.uncaughtExceptions.first() shouldBeInstanceOf AmiiboTypeFailure.FetchTypesFailure::class.java
    }
}
