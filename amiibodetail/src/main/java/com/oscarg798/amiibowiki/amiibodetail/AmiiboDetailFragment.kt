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

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.oscarg798.amiibowiki.amiibodetail.composeui.Detail
import com.oscarg798.amiibowiki.amiibodetail.composeui.ImageDetail
import com.oscarg798.amiibowiki.amiibodetail.composeui.Loading
import com.oscarg798.amiibowiki.amiibodetail.composeui.RelatedGamesButton
import com.oscarg798.amiibowiki.amiibodetail.databinding.FragmentAmiiboDetailBinding
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailViewState
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailWish
import com.oscarg798.amiibowiki.amiibodetail.mvi.UIEffect
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_TAIL
import com.oscarg798.amiibowiki.core.extensions.bundle
import com.oscarg798.amiibowiki.core.extensions.setImage
import com.oscarg798.amiibowiki.core.extensions.showExpandedImages
import com.oscarg798.amiibowiki.core.failures.AmiiboDetailFailure
import com.oscarg798.amiibowiki.core.utils.provideFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AmiiboDetailFragment : Fragment() {

    @Inject
    lateinit var factory: AmiiboDetailViewModel.Factory

    private val tail: String by bundle(ARGUMENT_TAIL)

    private val viewModel: AmiiboDetailViewModel by viewModels {
        provideFactory(factory, tail)
    }

    private lateinit var binding: FragmentAmiiboDetailBinding

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DetailScreen(viewModel, onRelatedGamesButtonClick = {
                    viewModel.onWish(AmiiboDetailWish.ShowRelatedGames)
                }, onImageClick = {
                    viewModel.onWish(AmiiboDetailWish.ExpandAmiiboImage(it))
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        setupViewModelInteractions()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail)
    }

    private fun setupViewModelInteractions() {
        lifecycleScope.launchWhenResumed {
            viewModel.uiEffect.collect { state ->
                when (state) {
                    is UIEffect.ShowAmiiboImage -> showExpandedImages(listOf(state.url))
                    is UIEffect.ShowRelatedGames -> showRelatedGames(state.amiiboId)
                }
            }
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

    private fun showRelatedGames(amiiboId: String) {
        findNavController().navigate(
            AmiiboDetailFragmentDirections.actionAmiiboDetailFragmentToSearchResultFragment(
                amiiboId,
                true
            )
        )
    }

    private fun showLoading() {
        with(binding) {
            shimmer.root.visibility = View.VISIBLE
            shimmer.shimmerViewContainer.startShimmer()
        }
    }

    private fun hideLoading() {
        with(binding) {
            shimmer.root.visibility = View.GONE
            shimmer.shimmerViewContainer.stopShimmer()
        }
    }
}


@Composable
fun DetailScreen(
    viewModel: AmiiboDetailViewModel,
    onImageClick: (String) -> Unit,
    onRelatedGamesButtonClick: () -> Unit
) {
    val state by viewModel.state.collectAsState(initial = AmiiboDetailViewState())

    Box {
        when {
            state.loading -> Loading()
            state.showingDetails != null -> Detail(
                amiibo = state.showingDetails!!,
                relatedGamesSectionEnabled = state.relatedGamesSectionEnabled,
                onImageClick = onImageClick,
                onRelatedGamesButtonClick = onRelatedGamesButtonClick
            )
        }
    }
}


private const val NO_ELEVATION = 0f
