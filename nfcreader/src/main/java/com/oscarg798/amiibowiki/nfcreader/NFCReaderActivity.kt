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

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.oscarg798.amiibowiki.core.AMIIBO_DETAIL_DEEPLINK
import com.oscarg798.amiibowiki.core.AmiiboIdentifier
import com.oscarg798.amiibowiki.core.ViewModelFactory
import com.oscarg798.amiibowiki.core.constants.TAIL_ARGUMENT
import com.oscarg798.amiibowiki.core.di.CoreComponentProvider
import com.oscarg798.amiibowiki.core.startDeepLinkIntent
import com.oscarg798.amiibowiki.nfcreader.databinding.ActivityNFCReaderBinding
import com.oscarg798.amiibowiki.nfcreader.di.DaggerNFCReaderComponent
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderViewState
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderWish
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@FlowPreview
@ExperimentalCoroutinesApi
class NFCReaderActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var nfcAdapter: NfcAdapter

    private lateinit var binding: ActivityNFCReaderBinding

    private lateinit var viewModel: NFCReaderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNFCReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DaggerNFCReaderComponent.builder()
            .coreComponent((application as CoreComponentProvider).provideCoreComponent())
            .build()
            .inject(this)

        setup()
        handleIntent(intent)
    }

    private fun setup() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(NFCReaderViewModel::class.java)

        viewModel.state.onEach {
            when {
                it.adapterStatus is NFCReaderViewState.AdapterStatus.AdapterAvailable -> setupForegroundDispatch()
                it.adapterStatus is NFCReaderViewState.AdapterStatus.AdapterReadyToBeStoped -> stopForegroundDispatch()
                it.status is NFCReaderViewState.Status.ReadSuccessful -> showAmiibo(it.status.amiiboIdentifier)
                it.error != null -> showErrorMessage(getString(R.string.default_error))
            }
        }.launchIn(lifecycleScope)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        viewModel.onWish(NFCReaderWish.Read(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)!!))
    }

    private fun showAmiibo(amiiboIdentifier: AmiiboIdentifier) {
        startDeepLinkIntent(
            AMIIBO_DETAIL_DEEPLINK,
            Bundle().apply {
                putString(TAIL_ARGUMENT, amiiboIdentifier.tail)
            }
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.onWish(NFCReaderWish.ValidateAdapterAvailability)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onWish(NFCReaderWish.StopAdapter)
    }

    private fun setupForegroundDispatch() {
        nfcAdapter.enableForegroundDispatch(
            this,
            PendingIntent.getActivity(
                this, 0,
                Intent(
                    this,
                    javaClass
                ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0
            ),
            null, null
        )
    }

    private fun stopForegroundDispatch() {
        nfcAdapter.disableForegroundDispatch(this)
    }

    private fun showErrorMessage(error: String) {
        Snackbar.make(binding.tvInstruccions, error, Snackbar.LENGTH_LONG).show()
    }
}

private const val TAG_FILE_SIZE = 532
private const val PAGE_SIZE = 4
private const val BULK_READ_PAGE_COUNT = 4
private const val MIME_TEXT_PLAIN = "text/plain"
