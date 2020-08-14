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
import androidx.lifecycle.lifecycleScope
import com.airbnb.deeplinkdispatch.DeepLink
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.oscarg798.amiibowiki.amiibodetail.databinding.ActivityAmiiboDetailBinding
import com.oscarg798.amiibowiki.amiibodetail.di.DaggerAmiiboDetailComponent
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailWish
import com.oscarg798.amiibowiki.amiibodetail.mvi.ShowingAmiiboDetailsParams
import com.oscarg798.amiibowiki.core.constants.AMIIBO_DETAIL_DEEPLINK
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_TAIL
import com.oscarg798.amiibowiki.core.di.entrypoints.AmiiboDetailEntryPoint
import com.oscarg798.amiibowiki.core.extensions.setImage
import com.oscarg798.amiibowiki.core.failures.AmiiboDetailFailure
import com.oscarg798.amiibowiki.searchgames.SearchResultFragment
import com.oscarg798.amiibowiki.searchgames.models.GameSearchParam
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@DeepLink(AMIIBO_DETAIL_DEEPLINK)
class AmiiboDetailActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: AmiiboDetailViewModel

    private lateinit var binding: ActivityAmiiboDetailBinding

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAmiiboDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DaggerAmiiboDetailComponent.factory()
            .create(
                intent.getStringExtra(ARGUMENT_TAIL)!!,
                EntryPointAccessors.fromApplication(
                    application,
                    AmiiboDetailEntryPoint::class.java
                )
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

        viewModel.state.onEach {
            when {
                it.isLoading -> showLoading()
                it.error != null -> showError(it.error)
                it.amiiboDetails != null -> showDetail(it.amiiboDetails)
            }
        }.launchIn(lifecycleScope)

        viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail)
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    private fun showError(amiiboDetailFailure: AmiiboDetailFailure) {
        hideLoading()
        Snackbar.make(
            binding.clMain,
            amiiboDetailFailure.message ?: getString(R.string.general_error),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun configureBackDrop() {
        bottomSheetBehavior = BottomSheetBehavior.from<View>(binding.searchResultFragment)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun showDetail(
        showingAmiiboDetailsParams: ShowingAmiiboDetailsParams
    ) {
        hideLoading()
        val viewAmiiboDetails = showingAmiiboDetailsParams.amiiboDetails
        supportActionBar?.title = viewAmiiboDetails.name

        with(binding) {
            ivImage.setImage(viewAmiiboDetails.imageUrl)
            tvGameCharacter.setText(viewAmiiboDetails.character)
            tvSerie.setText(viewAmiiboDetails.gameSeries)
            tvType.setText(viewAmiiboDetails.type)
        }

        if (showingAmiiboDetailsParams.isRelatedGamesSectionEnabled) {
            showSearchResultFragment(viewAmiiboDetails.id)
        } else {
            hideSearchResultFragment()
        }
    }

    private fun showSearchResultFragment(amiiboId: String) {
        binding.searchResultFragment.visibility = View.VISIBLE
        val searchResultFragment = getSearchResultsFragment() ?: return
        searchResultFragment.search(GameSearchParam.AmiiboGameSearchParam(amiiboId))
    }

    private fun hideSearchResultFragment() {
        binding.searchResultFragment.visibility = View.GONE
    }

    private fun showLoading() {
        with(binding) {
            shimmer.root.visibility = View.VISIBLE
            shimmer.shimmerViewContainer.startShimmer()
            tvSerieTitle.visibility = View.GONE
            tvTypeTitle.visibility = View.GONE
        }
    }

    private fun hideLoading() {
        with(binding) {
            shimmer.root.visibility = View.GONE
            shimmer.shimmerViewContainer.stopShimmer()
            tvSerieTitle.visibility = View.VISIBLE
            tvTypeTitle.visibility = View.VISIBLE
        }
    }

    private fun getSearchResultsFragment(): SearchResultFragment? {
        return supportFragmentManager.findFragmentByTag(getString(R.string.search_result_fragment_tag)) as? SearchResultFragment
    }
}

private const val NO_ELEVATION = 0f
