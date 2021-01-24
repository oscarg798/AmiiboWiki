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

package com.oscarg798.amiibowiki.amiibodetail

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.deeplinkdispatch.DeepLink
import com.oscarg798.amiibowiki.amiibodetail.databinding.ActivityAmiiboDetailDeeplinkBinding
import com.oscarg798.amiibowiki.core.constants.AMIIBO_DETAIL_DEEPLINK
import com.oscarg798.amiibowiki.core.constants.GAME_DETAIL_DEEPLINK
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@DeepLink(AMIIBO_DETAIL_DEEPLINK)
class AmiiboDetailDeepLinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityAmiiboDetailDeeplinkBinding.inflate(layoutInflater).root)
        supportFragmentManager.beginTransaction().replace(R.id.flMain, AmiiboDetailFragment().apply {
            arguments = intent.extras
        }).commitNow()
        Log.i("PENE", "PENE")
    }
}
