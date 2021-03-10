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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.oscarg798.amiibowiki.amiibodetail.composeui.Screen
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailWish
import com.oscarg798.amiibowiki.amiibodetail.mvi.UIEffect
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_TAIL
import com.oscarg798.amiibowiki.core.extensions.bundle
import com.oscarg798.amiibowiki.core.extensions.showExpandedImages
import com.oscarg798.amiibowiki.core.utils.SavedInstanceViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
internal class AmiiboDetailFragment : Fragment() {

    @Inject
    lateinit var factory: AmiiboDetailViewModel.Factory

    private val tail: String by bundle(ARGUMENT_TAIL)

    private val viewModel: AmiiboDetailViewModel by viewModels {
        SavedInstanceViewModelFactory(
            factoryCreator = {
                factory.create(tail, it)
            },
            owner = this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Screen(
                    viewModel,
                    onRelatedGamesButtonClick = {
                        viewModel.onWish(AmiiboDetailWish.ShowRelatedGames)
                    },
                    onImageClick = {
                        viewModel.onWish(AmiiboDetailWish.ExpandAmiiboImage(it))
                    }
                )
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

    private fun showRelatedGames(amiiboId: String) {
        findNavController().navigate(
            AmiiboDetailFragmentDirections.actionAmiiboDetailFragmentToSearchResultFragment(
                amiiboId,
                true
            )
        )
    }
}

private const val NO_ELEVATION = 0f
