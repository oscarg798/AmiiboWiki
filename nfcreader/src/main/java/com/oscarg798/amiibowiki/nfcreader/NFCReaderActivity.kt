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

package com.oscarg798.amiibowiki.nfcreader
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.oscarg798.amiibowiki.core.constants.AMIIBO_DETAIL_DEEPLINK
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_TAIL
import com.oscarg798.amiibowiki.core.di.entrypoints.NFCReaderEntryPoint
import com.oscarg798.amiibowiki.core.extensions.setViewTreeObserver
import com.oscarg798.amiibowiki.core.extensions.startDeepLinkIntent
import com.oscarg798.amiibowiki.core.extensions.verifyNightMode
import com.oscarg798.amiibowiki.core.models.AmiiboIdentifier
import com.oscarg798.amiibowiki.nfcreader.di.DaggerNFCReaderComponent
import com.oscarg798.amiibowiki.nfcreader.mvi.ReadTagWish
import com.oscarg798.amiibowiki.nfcreader.ui.NFCReaderScreen
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject
import kotlinx.coroutines.flow.collect

internal class NFCReaderActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: NFCReaderViewModel

    @Inject
    lateinit var nfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyNightMode()
        setViewTreeObserver()

        inject()

        setContent {
            NFCReaderScreen(viewModel = viewModel, coroutineScope = lifecycleScope) {
                finish()
            }
        }

        setup()
        handleIntent(intent)
    }

    private fun inject() {
        DaggerNFCReaderComponent.factory()
            .create(
                EntryPointAccessors.fromApplication(
                    application,
                    NFCReaderEntryPoint::class.java
                )
            )
            .inject(this)
    }

    private fun setup() {
        lifecycleScope.launchWhenResumed {
            viewModel.uiEffect.collect { effect ->
                showAmiibo(effect.amiiboIdentifier)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        viewModel.onWish(ReadTagWish(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)!!))
    }

    private fun showAmiibo(amiiboIdentifier: AmiiboIdentifier) {
        startDeepLinkIntent(
            AMIIBO_DETAIL_DEEPLINK,
            Bundle().apply {
                putString(ARGUMENT_TAIL, amiiboIdentifier.tail)
            }
        )
    }
}
