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

package com.oscarg798.amiibowiki.core.repositories

import com.oscarg798.amiibowiki.core.extensions.getOrTransformNetworkException
import com.oscarg798.amiibowiki.core.failures.GameAPIAuthenticationFailure
import com.oscarg798.amiibowiki.core.network.services.AuthService
import com.oscarg798.amiibowiki.core.persistence.sharepreferences.SharedPreferencesWrapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameAuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val sharedPreferencesWrapper: SharedPreferencesWrapper
) : GameAuthRepository {

    override suspend fun authenticate() {
        runCatching {
            val authResponse = authService.authenticate()
            sharedPreferencesWrapper.addStringValue(AUTH_STRING_KEY, authResponse.accessToken)
        }.getOrTransformNetworkException {
            throw GameAPIAuthenticationFailure.DataSourceError(it)
        }
    }

    override fun getToken(): String = sharedPreferencesWrapper.getStringValue(AUTH_STRING_KEY)
        ?: throw GameAPIAuthenticationFailure.TokenNotAvailable()

}
private const val AUTH_STRING_KEY = "game_api_token"


