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
package com.oscarg798.amiibowiki.network.exceptions

import java.io.IOException

sealed class NetworkException(
    override val message: String?,
    open val code: Int?
) : IOException(message) {

    data class BadRequest(override val message: String?) :
        NetworkException(message, 400)

    data class Unauthorized(override val message: String?) :
        NetworkException(message, 401)

    data class Forbidden(override val message: String?) :
        NetworkException(message, 403)

    data class NotFound(override val message: String?) :
        NetworkException(message, 404)

    data class APIKeyNotFound(override val message: String?) :
        NetworkException(message, 409)

    data class Internal(override val message: String?) :
        NetworkException(message, 500)

    object Connection : NetworkException("Verify internet connection or host", null)
    object TimeOut : NetworkException("Timeout", null)

    data class UnknowHost(override val message: String?, override val code: Int? = null) :
        NetworkException(message, code)

    data class Unknown(override val message: String?, override val code: Int?) :
        NetworkException(message, code)

}