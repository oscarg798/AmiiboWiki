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

package com.oscarg798.amiibowiki.searchgamesresults

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_AMIIBO_ID
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_GAME_ID
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_SHOW_AS_RELATED_GAMES_SECTION
import com.oscarg798.amiibowiki.core.constants.GAME_DETAIL_DEEPLINK
import com.oscarg798.amiibowiki.core.extensions.startDeepLinkIntent
import com.oscarg798.amiibowiki.core.utils.SavedInstanceViewModelFactory
import com.oscarg798.amiibowiki.searchgamesresults.models.GameSearchParam
import com.oscarg798.amiibowiki.searchgamesresults.mvi.SearchResultWish
import com.oscarg798.amiibowiki.searchgamesresults.mvi.UIEffect
import com.oscarg798.amiibowiki.searchgamesresults.ui.Screen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce

@AndroidEntryPoint
class SearchResultFragment : Fragment() {

    @Inject
    lateinit var factory: SearchGamesResultViewModel.Factory

    private val searchFlow = MutableStateFlow(InitialQuery)

    private val isShownAsGamesRelatedSection: Boolean by lazy(LazyThreadSafetyMode.NONE) {
        if (arguments?.containsKey(ARGUMENT_SHOW_AS_RELATED_GAMES_SECTION) == true) {
            requireArguments().getBoolean(ARGUMENT_SHOW_AS_RELATED_GAMES_SECTION)
        } else {
            false
        }
    }

    private val viewModel: SearchGamesResultViewModel by viewModels {
        SavedInstanceViewModelFactory(
            {
                factory.create(isShownAsGamesRelatedSection, it)
            },
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModelInteractions()
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
                    searchBox = !isShownAsGamesRelatedSection,
                    onSearch = {
                        searchFlow.value = it.text
                    },
                    onSearchResultClickListener = {
                        viewModel.onWish(SearchResultWish.ShowGameDetail(it.gameId))
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        arguments?.getString(ARGUMENT_AMIIBO_ID)?.let {
            search(GameSearchParam.AmiiboGameSearchParam(it))
        }
    }

    private fun setupViewModelInteractions() {
        lifecycleScope.launchWhenResumed {
            viewModel.uiEffect.collect {
                when (it) {
                    is UIEffect.ShowGameDetails -> showGameDetails(it.gameId)
                    is UIEffect.ObserveSearchResults -> observeSearchResults()
                }
            }
        }
    }

    private fun observeSearchResults() {
        lifecycleScope.launchWhenResumed {
            searchFlow
                .debounce(SearchDelay)
                .collect {
                    search(GameSearchParam.StringQueryGameSearchParam(it))
                }
        }
    }

    fun search(gameSearchGameQueryParam: GameSearchParam) {
        viewModel.onWish(SearchResultWish.SearchGames(gameSearchGameQueryParam))
    }

    private fun showGameDetails(gameId: Int) {
        startDeepLinkIntent(
            GAME_DETAIL_DEEPLINK,
            Bundle().apply {
                putInt(ARGUMENT_GAME_ID, gameId)
            }

        )
    }

    companion object {
        fun newInstance(shownAsGameRelatedSection: Boolean = false) = SearchResultFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARGUMENT_SHOW_AS_RELATED_GAMES_SECTION, shownAsGameRelatedSection)
            }
        }
    }
}

internal const val GameImageId = "gameImageId"
internal const val GameNameId = "gameNameTitleId"
internal const val GameAlternativeNameId = "gameAlternativeNameId"
private const val InitialQuery = ""
private const val SearchDelay = 350L
