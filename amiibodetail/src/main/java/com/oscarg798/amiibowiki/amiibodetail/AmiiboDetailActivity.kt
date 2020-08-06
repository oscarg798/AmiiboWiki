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
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.deeplinkdispatch.DeepLink
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.oscarg798.amiibowiki.amiibodetail.adapter.GameRelatedClickListener
import com.oscarg798.amiibowiki.amiibodetail.databinding.ActivityAmiiboDetailBinding
import com.oscarg798.amiibowiki.amiibodetail.di.DaggerAmiiboDetailComponent
import com.oscarg798.amiibowiki.amiibodetail.errors.AmiiboDetailFailure
import com.oscarg798.amiibowiki.amiibodetail.models.ViewGameSearchResult
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailViewState
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailWish
import com.oscarg798.amiibowiki.core.AMIIBO_DETAIL_DEEPLINK
import com.oscarg798.amiibowiki.core.ViewModelFactory
import com.oscarg798.amiibowiki.core.constants.TAIL_ARGUMENT
import com.oscarg798.amiibowiki.core.di.CoreComponentProvider
import com.oscarg798.amiibowiki.core.mvi.ViewState
import com.oscarg798.amiibowiki.core.setImage
import com.oscarg798.amiibowiki.gamedetail.GameDetailActivity
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@FlowPreview
@ExperimentalCoroutinesApi
@DeepLink(AMIIBO_DETAIL_DEEPLINK)
class AmiiboDetailActivity : AppCompatActivity(), GameRelatedClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: ActivityAmiiboDetailBinding

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    private val showGameDetailsStateFlow =
        MutableStateFlow<AmiiboDetailWish>(AmiiboDetailWish.ShowAmiiboDetail)

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
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.searchResultFragment,
                SearchResultFragment.newInstance(),
                getString(R.string.search_result_fragment_tag)
            )
            .commit()

        supportActionBar?.let {
            it.elevation = NO_ELEVATION
            with(it) {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_close)
            }
        }

        ViewCompat.isNestedScrollingEnabled(binding.searchResultFragment)

        binding.searchResultFragment.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }

        configureBackDrop()

        val viewModel =
            ViewModelProvider(this, viewModelFactory).get(AmiiboDetailViewModel::class.java)

        viewModel.state.onEach {
            when {
                it.loading == ViewState.LoadingState.Loading -> showLoading()
                it.status is AmiiboDetailViewState.Status.ShowingAmiiboDetails -> showDetail(it.status)
                it.status is AmiiboDetailViewState.Status.ShowingGameDetails -> showGameDetails(
                    it.status.gameId,
                    it.status.gameSeries
                )
                it.error != null -> showError(it.error)
            }
        }.launchIn(lifecycleScope)

        showGameDetailsStateFlow
            .onEach {
                viewModel.onWish(it)
            }.launchIn(lifecycleScope)
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    override fun onGameRelatedClick(gameSearchResult: ViewGameSearchResult) {
        showGameDetailsStateFlow.value = AmiiboDetailWish.ShowGameDetail(
            gameSearchResult.gameId,
            binding.tvSerie.text.toString()
        )
    }

    private fun showError(amiiboDetailFailure: AmiiboDetailFailure) {
        hideLoading()
        Snackbar.make(
            binding.ivImage,
            amiiboDetailFailure.message ?: getString(R.string.general_error),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showGameDetails(gameId: Int, gameSeries: String) {
        GameDetailActivity.newInstance(this, gameId, gameSeries)
    }

    private fun configureBackDrop() {
        bottomSheetBehavior = BottomSheetBehavior.from<View>(binding.searchResultFragment)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun showDetail(state: AmiiboDetailViewState.Status.ShowingAmiiboDetails) {
        hideLoading()
        val viewAmiiboDetails = state.amiiboDetails
        supportActionBar?.title = viewAmiiboDetails.name
        with(binding) {
            ivImage.setImage(viewAmiiboDetails.imageUrl)

            tvGameCharacter.setText(viewAmiiboDetails.character)
            tvSerie.setText(viewAmiiboDetails.gameSeries)
            tvType.setText(viewAmiiboDetails.type)

            if (state.isRelatedGamesSectionEnabled) {
                showRelatedGames(viewAmiiboDetails.gameSearchResults)
                searchResultFragment.visibility = View.VISIBLE
            } else {
                searchResultFragment.visibility = View.GONE
            }
        }
    }

    private fun showLoading() {
        with(binding) {
            shimmer.root.visibility = View.VISIBLE
            shimmer.shimmerViewContainer.startShimmer()
            tvCharacterTitle.visibility = View.GONE
            tvSerieTitle.visibility = View.GONE
            tvTypeTitle.visibility = View.GONE
            searchResultFragment.visibility = View.GONE
        }
    }

    private fun hideLoading() {
        with(binding) {
            shimmer.root.visibility = View.GONE
            shimmer.shimmerViewContainer.stopShimmer()
            tvCharacterTitle.visibility = View.VISIBLE
            tvSerieTitle.visibility = View.VISIBLE
            tvTypeTitle.visibility = View.VISIBLE
            searchResultFragment.visibility = View.VISIBLE
        }
    }

    private fun showRelatedGames(
        gameSearchResults: Collection<ViewGameSearchResult>
    ) {

        val searchResultFragment =
            supportFragmentManager.findFragmentByTag(getString(R.string.search_result_fragment_tag)) as? SearchResultFragment
                ?: return
        searchResultFragment.showGameResults(gameSearchResults.toList())
    }
}

private const val NO_ELEVATION = 0f
