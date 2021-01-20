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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.oscarg798.amiibowiki.amiibodetail.databinding.FragmentAmiiboDetailBinding
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailWish
import com.oscarg798.amiibowiki.amiibodetail.mvi.ShowingAmiiboDetailsParams
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_TAIL
import com.oscarg798.amiibowiki.core.di.entrypoints.AmiiboDetailEntryPoint
import com.oscarg798.amiibowiki.core.extensions.setImage
import com.oscarg798.amiibowiki.core.extensions.showExpandedImages
import com.oscarg798.amiibowiki.core.failures.AmiiboDetailFailure
import com.oscarg798.amiibowiki.searchgamesresults.SearchResultFragment
import com.oscarg798.amiibowiki.searchgamesresults.models.GameSearchParam
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AmiiboDetailFragment : Fragment() {


    @Inject
    lateinit var factory: AmiiboDetailViewModel.AssistedFactory

    private lateinit var tail: String

    private val viewModel: AmiiboDetailViewModel by viewModels {
        AmiiboDetailViewModel.provideFactory(factory, tail)
    }

    private lateinit var binding: FragmentAmiiboDetailBinding

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAmiiboDetailBinding.inflate(
            LayoutInflater.from(requireContext()),
            container,
            false
        )

        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tail = arguments?.getString(ARGUMENT_TAIL, null)
            ?: throw IllegalArgumentException("Tail is required")
        setHasOptionsMenu(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        childFragmentManager.beginTransaction()
            .replace(
                R.id.searchResultFragment,
                SearchResultFragment.newInstance(),
                getString(R.string.search_result_fragment_tag)
            )
            .commit()

        ViewCompat.isNestedScrollingEnabled(binding.searchResultFragment)

        binding.searchResultFragment.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }

        configureBackDrop()

        viewModel.state.onEach {
            when {
                it.isLoading -> showLoading()
                it.error != null -> showError(it.error)
                it.imageExpanded != null -> showExpandedImages(listOf(it.imageExpanded))
                it.amiiboDetails != null -> showDetail(it.amiiboDetails)
            }
        }.launchIn(lifecycleScope)

        viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail)
    }

//    override fun onBackPressed() {
//        if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
//            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
//        } else {
//            super.onBackPressed()
//        }
//    }

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
//        supportActionBar?.title = viewAmiiboDetails.name

        with(binding) {
            ivImage.setImage(viewAmiiboDetails.imageUrl)
            tvGameCharacter.setText(viewAmiiboDetails.character)
            tvSerie.setText(viewAmiiboDetails.gameSeries)
            tvType.setText(viewAmiiboDetails.type)

            ivImage.setOnClickListener {
                viewModel.onWish(AmiiboDetailWish.ExpandAmiiboImage(viewAmiiboDetails.imageUrl))
            }
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
        return childFragmentManager.findFragmentByTag(getString(R.string.search_result_fragment_tag)) as? SearchResultFragment
    }
}

private const val NO_ELEVATION = 0f
