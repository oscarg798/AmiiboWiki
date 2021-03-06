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

package com.oscarg798.amiibowiki.network.interceptors

import com.oscarg798.amiibowiki.network.exceptions.NetworkException
import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import okhttp3.Interceptor
import okhttp3.Response

class ErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            throw when (e) {
                is ConnectException -> NetworkException.Connection
                is TimeoutException,
                is SocketTimeoutException -> NetworkException.TimeOut
                is UnknownHostException -> NetworkException.UnknowHost(e.message)
                else -> e
            }
        }

        if (!response.isSuccessful) {
            val exception: NetworkException = when (response.code) {
                HttpURLConnection.HTTP_BAD_REQUEST -> NetworkException.BadRequest(response.message)
                HttpURLConnection.HTTP_UNAUTHORIZED -> NetworkException.Unauthorized(response.message)
                HttpURLConnection.HTTP_NOT_FOUND -> NetworkException.NotFound(response.message)
                HttpURLConnection.HTTP_FORBIDDEN -> NetworkException.Forbidden(response.message)
                HttpURLConnection.HTTP_CONFLICT -> NetworkException.APIKeyNotFound("Api key not found")
                HttpURLConnection.HTTP_INTERNAL_ERROR -> NetworkException.Internal(response.message)
                else -> NetworkException.Unknown(response.message, response.code)
            }

            throw exception
        }

        return response
    }
}
