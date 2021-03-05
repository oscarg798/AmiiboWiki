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

package com.oscarg798.amiibowiki.testutils.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import org.junit.Assert

/**
 * Took from https://www.thuytrinh.dev/test-receiving-events-hot-flow-coroutines/
 */
class TestCollector<T> {

    private val values = mutableListOf<T>()

    fun test(scope: CoroutineScope, flow: Flow<T>, job: Job? = null): Job {
        val parentJob = job ?: Job()
        return flow.onEach {
            values.add(it)
        }.launchIn(scope + parentJob)
    }

    fun clear() {
        values.clear()
    }

    @Deprecated(
        "This is depreacted as is better to use a collection",
        ReplaceWith("wereValuesEmitted")
    )
    fun assertValues(vararg _values: T) {
        assert(values.containsAll(_values.toList()))
    }

    infix fun wereValuesEmitted(_values: Collection<T>) {
        assert(values.containsAll(_values))
    }

    fun wereValuesEmiited(_values: Collection<T>, comparator: Comparator<T>) {
        values.forEachIndexed { index, t ->
            Assert.assertEquals(0, comparator.compare(t, _values.elementAt(index)))
        }
    }

    infix fun valuesWereNotEmitted(_values: Collection<T>) {
        _values.forEach {
            valueWasNotEmitted(it)
        }
    }

    infix fun valueWasNotEmitted(value: T) {
        Assert.assertFalse(values.contains(value))
    }

    infix fun wasValueEmiited(value: T) = assert(values.contains(value))

    infix fun hasSize(size: Int) = Assert.assertEquals(size, values.size)
}
