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

package com.oscarg798.amiibowiki.splash.usecases

import com.oscarg798.amiibowiki.core.usecases.AuthenticateApplicationUseCase
import com.oscarg798.amiibowiki.updatechecker.UpdateCheckerUseCase
import com.oscarg798.amiibowiki.core.usecases.UpdateAmiiboTypeUseCase
import com.oscarg798.amiibowiki.navigation.UpdateStatus
import com.oscarg798.amiibowiki.splash.failures.OutdatedAppException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

/**
 * In the future we might need to refactor this in order to pass a list
 * of commands of actions that we need to execute to initialize the app instead of
 * specific usecases. As for now we do not have anything else to add to the pipeline
 * we are not going to implemented thay way. YAGNI
 */
class InitializeApplicationUseCase @Inject constructor(
    private val updateCheckerUseCase: UpdateCheckerUseCase,
    private val updateAmiiboTypeUseCase: UpdateAmiiboTypeUseCase,
    private val activateRemoteConfigUseCase: ActivateRemoteConfigUseCase,
    private val authenticateApplicationUseCase: AuthenticateApplicationUseCase,
) {

    fun execute(): Flow<Unit> = getRemoteConfigActivationFlow().map {
        val updateStatus = updateCheckerUseCase.execute()
        if (updateStatus is UpdateStatus.UpdateAvailable.Immediate) {
            throw OutdatedAppException()
        }
    }.flatMapMerge {
        getAuthFlow().zip(getRefreshTypesFlow()) { _, _ ->  }
    }

    private fun getAuthFlow() = callToUnitFlow { authenticateApplicationUseCase.execute() }

    private fun getRemoteConfigActivationFlow() =
        callToUnitFlow { activateRemoteConfigUseCase.execute() }

    private fun getRefreshTypesFlow(): Flow<Unit> =
        callToUnitFlow { updateAmiiboTypeUseCase.execute() }

    private fun callToUnitFlow(call: suspend () -> Any) = flow<Unit> {
        call.invoke()
        emit(Unit)
    }


}
