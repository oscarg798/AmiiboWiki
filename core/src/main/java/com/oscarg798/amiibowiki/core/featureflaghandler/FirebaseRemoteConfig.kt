/*
 * Copyright 2020 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.oscarg798.amiibowiki.core.featureflaghandler

import com.google.firebase.remoteconfig.FirebaseRemoteConfig as FirebaseRemote
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.oscarg798.flagly.remoteconfig.RemoteConfig
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

class FirebaseRemoteConfig : RemoteConfig {

    private val remoteConfig: FirebaseRemote = FirebaseRemote.getInstance()

    override fun activateAsync(): Deferred<Unit> {
        val deferred = CompletableDeferred<Unit>()

        setConfigSettings({
            fetchAndActivateRemoteConfig(deferred)
        }, {
            throw it
        })


        return deferred
    }

    private fun fetchAndActivateRemoteConfig(deferred: CompletableDeferred<Unit>) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    deferred.complete(Unit)
                } else {
                    // TODO check if we have defaults for FF otherwise crash
                    // check defaults deferred.completeExceptionally(FirebaseRemoteConfigInitializationException(task.exception))
                    deferred.complete(Unit)
                }
            }
    }

    private fun setConfigSettings(
        onRemoteConfigActivated: () -> Unit,
        onRemoteConfigActivationError: (Exception) -> Unit
    ) {

        remoteConfig.setConfigSettingsAsync(getConfigSettings()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onRemoteConfigActivated()
            } else {
                onRemoteConfigActivationError(IllegalArgumentException("We were not able to init firebase"))
            }
        }

    }

    private fun getConfigSettings() = FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(FETCH_INTERVAL)
        .build()

    override fun getBoolean(key: String): Boolean {
        return remoteConfig.getBoolean(key)
    }
}

private const val FETCH_INTERVAL = 3L
