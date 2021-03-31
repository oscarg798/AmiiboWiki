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

import com.oscarg798.amiibowiki.core.featureflaghandler.AmiiboWikiFeatureFlag
import com.oscarg798.amiibowiki.core.usecases.IsFeatureEnableUseCase
import com.oscarg798.amiibowiki.dashboard.UpdateStatus
import com.oscarg798.amiibowiki.updatechecker.UpdateCheckerUseCase
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldBe
import org.junit.Before
import org.junit.Test

class UpdateCheckerUseCaseTest {

    private val isFeatureEnableUseCase: IsFeatureEnableUseCase = mockk()
    private lateinit var usecase: UpdateCheckerUseCase

    @Before
    fun setup() {
        usecase = UpdateCheckerUseCase(isFeatureEnableUseCase)
    }

    @Test
    fun `when immediate update feature flag is enabled but flexible not the it should return immediate update status`() {
        every { isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.ImmediateUpdate) } answers { true }
        every { isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.FlexibleUpdate) } answers { false }

        usecase.execute() shouldBe UpdateStatus.UpdateAvailable.Immediate
    }

    @Test
    fun `when immediate and flexible update feature flag is enabled then it should return immediate update status`() {
        every { isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.ImmediateUpdate) } answers { true }
        every { isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.FlexibleUpdate) } answers { true }

        usecase.execute() shouldBe UpdateStatus.UpdateAvailable.Immediate
    }

    @Test
    fun `when flexible update feature flag is enabled but inmmediate  not then it should return flexible update status`() {
        every { isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.ImmediateUpdate) } answers { false }
        every { isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.FlexibleUpdate) } answers { true }

        usecase.execute() shouldBe UpdateStatus.UpdateAvailable.Flexible
    }

    @Test
    fun `if immediate and flexible feature flags are not enabled then it should return already updated`(){
        every { isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.ImmediateUpdate) } answers { false }
        every { isFeatureEnableUseCase.execute(AmiiboWikiFeatureFlag.FlexibleUpdate) } answers { false }

        usecase.execute() shouldBe UpdateStatus.AlreadyUpdated
    }
}
