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

import com.oscarg798.flagly.featureflag.DynamicFeatureFlagHandler
import com.oscarg798.flagly.featureflag.FeatureFlag
import com.oscarg798.flagy.exceptions.FeatureFlagNotPresentInHandlerException

class LocalFeatureFlagHandler : DynamicFeatureFlagHandler {

    private val featureMap: HashMap<String, Boolean> = HashMap()

    override fun setValue(featureFlag: FeatureFlag, value: Boolean) {
        featureMap[featureFlag.name] = value
    }

    override fun isFeatureEnabled(featureFlag: FeatureFlag): Boolean {
        return featureMap[featureFlag.name] ?: throw FeatureFlagNotPresentInHandlerException(
            featureFlag
        )
    }

    override fun isValueOverriden(featureFlag: FeatureFlag): Boolean =
        featureMap.containsKey(featureFlag.name)

    override fun removeOverridenValue(featureFlag: FeatureFlag) {
        featureMap.remove(featureFlag.name)
    }
}
