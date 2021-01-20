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

package com.oscarg798.amiibowiki.core

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.oscarg798.amiibowiki.core.di.modules.FeatureFlagHandlerModule
import com.oscarg798.amiibowiki.core.di.modules.LoggerModule
import com.oscarg798.amiibowiki.core.di.modules.PersistenceModule
import com.oscarg798.amiibowiki.core.extensions.bundle
import com.oscarg798.amiibowiki.network.di.NetworkModule
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import kotlinx.parcelize.Parcelize
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@UninstallModules(
    PersistenceModule::class,
    FeatureFlagHandlerModule::class,
    NetworkModule::class,
    LoggerModule::class
)
@HiltAndroidTest
class BundleUtilsTest {
    private val fragment: Fragment = relaxedMockk()
    private val bundle: Bundle = relaxedMockk()

    @Before
    fun setup() {
        every { fragment.requireArguments() } answers { bundle }
        every { bundle.containsKey(KEY) } answers { true }
    }

    @Test(expected = IllegalStateException::class)
    fun given_a_fragment_that_does_not_have_arguments_when_bundle_is_invoke_then_it_should_throw() {
        every { fragment.requireArguments() } throws (IllegalStateException())

        fragment.bundle<String>(KEY).value
    }

    @Test(expected = IllegalArgumentException::class)
    fun given_a_fragment_and_non_existen_key_when_bundle_is_invoke_then_it_should_throw() {
        every { bundle.containsKey(KEY) } answers { false }

        fragment.bundle<String>(KEY).value
    }

    @Test
    fun given_a_key_for_a_string_when_bundle_is_invoke_then_it_should_then_it_should_return_expected_value() {
        every { bundle.getString(KEY) } answers { STRING_VALUE }
        val value = fragment.bundle<String>(KEY).value
        Assert.assertEquals(STRING_VALUE, value)
    }

    @Test
    fun given_a_key_for_an_int_when_bundle_is_invoke_then_it_should_then_it_should_return_expected_value() {
        every { bundle.getInt(KEY) } answers { INT_VALUE }
        val value = fragment.bundle<Int>(KEY).value
        Assert.assertEquals(INT_VALUE, value)
    }

    @Test
    fun given_a_key_for_a_boolean_when_bundle_is_invoke_then_it_should_then_it_should_return_expected_value() {
        every { bundle.getBoolean(KEY) } answers { BOOLEAN_VALUE }
        val value = fragment.bundle<Boolean>(KEY).value
        Assert.assertEquals(BOOLEAN_VALUE, value)
    }

    @Test
    fun given_a_key_for_a_double_when_bundle_is_invoke_then_it_should_then_it_should_return_expected_value() {
        every { bundle.getDouble(KEY) } answers { DOUBLE_VALUE }
        val value = fragment.bundle<Double>(KEY).value
        Assert.assertEquals(DOUBLE_VALUE, value, DELTA)
    }

    @Test
    fun given_a_key_for_a_long_when_bundle_is_invoke_then_it_should_then_it_should_return_expected_value() {
        every { bundle.getParcelable<MyParcelable>(KEY) } answers { PARCELABLE_VALUE }
        val value = fragment.bundle<MyParcelable>(KEY).value
        Assert.assertEquals(PARCELABLE_VALUE, value)
        Assert.assertEquals(PARCELABLE_NAME, value.name)
        Assert.assertEquals(PARCELABLE_AGE, value.age)
    }

    @Test(expected = IllegalArgumentException::class)
    fun given_a_key_for_a_unsupported_type_when_bundle_is_invoke_then_it_should_then_it_should_thown() {
        fragment.bundle<ATestClass>(KEY).value
    }

}

private const val BOOLEAN_VALUE = true
private const val PARCELABLE_AGE =35
private const val PARCELABLE_NAME = "Mario Bross"
private val PARCELABLE_VALUE = MyParcelable("Mario Bross", 35)
private const val DELTA = 0.0
private const val LONG_VALUE = 1L
private const val DOUBLE_VALUE = 0.5
private const val INT_VALUE = 1
private const val STRING_VALUE = "1"
private const val KEY = "String"

private class ATestClass

@Parcelize
private class MyParcelable(val name: String, val age: Int): Parcelable
