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

package com.oscarg798.amiibowiki.amiibodetail

import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.deeplinkdispatch.DeepLink
import com.google.android.material.snackbar.Snackbar
import com.oscarg798.amiibowiki.amiibodetail.adapter.GamesRelatedAdapter
import com.oscarg798.amiibowiki.amiibodetail.databinding.ActivityAmiiboDetailBinding
import com.oscarg798.amiibowiki.amiibodetail.di.DaggerAmiiboDetailComponent
import com.oscarg798.amiibowiki.amiibodetail.models.ViewAmiiboDetails
import com.oscarg798.amiibowiki.core.AMIIBO_DETAIL_DEEPLINK
import com.oscarg798.amiibowiki.core.TextWatcherAdapter
import com.oscarg798.amiibowiki.core.ViewModelFactory
import com.oscarg798.amiibowiki.core.constants.TAIL_ARGUMENT
import com.oscarg798.amiibowiki.core.di.CoreComponentProvider
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.setImage
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

@FlowPreview
@ExperimentalCoroutinesApi
@DeepLink(AMIIBO_DETAIL_DEEPLINK)
class AmiiboDetailActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: ActivityAmiiboDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAmiiboDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DaggerAmiiboDetailComponent.factory()
            .create(
                intent.getStringExtra(TAIL_ARGUMENT)!!,
                (application as CoreComponentProvider).provideCoreComponent()
            )
            .inject(this)

        setup()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setup() {
        supportActionBar?.let {
            with(it) {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_close)
            }
        }

        with(binding.rvGamesRelated) {
            layoutManager = LinearLayoutManager(context)
            adapter = GamesRelatedAdapter()
        }

        val vm = ViewModelProvider(this, viewModelFactory).get(AmiiboDetailViewModel::class.java)
        vm.state.onEach {
            when {
                it.status is AmiiboDetailViewState.Status.ShowingDetail -> showDetail(it.status.amiiboDetails)
                it.error != null -> {
                    Snackbar.make(
                        binding.ivImage,
                        it.error.message ?: "Error",
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                }
            }
        }.launchIn(lifecycleScope)

        vm.onWish(AmiiboDetailWish.ShowDetail)
    }

    private fun showDetail(viewAmiiboDetails: ViewAmiiboDetails) {
        supportActionBar?.title = viewAmiiboDetails.name
        with(binding) {
            ivImage.setImage(viewAmiiboDetails.imageUrl)
            tvCharacter.setText(
                String.format(
                    getString(R.string.character_string_format),
                    viewAmiiboDetails.character
                )
            )
            tvSerie.setText(
                String.format(
                    getString(R.string.game_series_string_format),
                    viewAmiiboDetails.gameSeries
                )
            )
            tvType.setText(
                String.format(
                    getString(R.string.type_string_format),
                    viewAmiiboDetails.type
                )
            )

            (rvGamesRelated.adapter as GamesRelatedAdapter).submitList(viewAmiiboDetails.gameSearchResults.toList())
        }
    }
}
