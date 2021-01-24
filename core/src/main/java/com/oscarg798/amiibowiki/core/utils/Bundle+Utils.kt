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

package com.oscarg798.amiibowiki.core.extensions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.bundle(
    key: String,
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    with(requireArguments()){
        validateKeyExists(key)
        getParamFromBundle(key)
    }
}

inline fun <reified T> Activity.bundle(
    key: String,
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    with(intent.extras ?: error("Acitivity does not have extras") ){
        validateKeyExists(key)
        getParamFromBundle(key)
    }
}


inline fun <reified T> Bundle.getParamFromBundle(key: String) = when {
    String::class.java.isAssignableFrom(T::class.java) -> getString(key)
    Int::class.javaObjectType.isAssignableFrom(T::class.java) -> getInt(key)
    Double::class.javaObjectType.isAssignableFrom(T::class.java) -> getDouble(key)
    Long::class.javaObjectType.isAssignableFrom(T::class.java) -> getLong(key)
    Boolean::class.javaObjectType.isAssignableFrom(T::class.java) -> getBoolean(key)
    Parcelable::class.java.isAssignableFrom(T::class.java) -> getParcelable(key)
    else -> illegalArgumentError("Type ${T::class.java.simpleName} not supported")
} as T


fun Bundle.validateKeyExists(key: String) {
    if (!containsKey(key)) {
        illegalArgumentError("Argument does not have a value for key $key")
    }
}


public fun illegalArgumentError(message: String): Nothing = throw IllegalArgumentException(message)
public fun notFoundError(message: String): Nothing = throw NullPointerException(message)
