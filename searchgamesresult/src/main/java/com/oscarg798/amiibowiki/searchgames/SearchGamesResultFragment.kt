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

package com.oscarg798.amiibowiki.searchgames

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_GAME_ID
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_SHOW_AS_RELATED_GAMES_SECTION
import com.oscarg798.amiibowiki.core.constants.GAME_DETAIL_DEEPLINK
import com.oscarg798.amiibowiki.core.di.entrypoints.SearchGamesResultEntryPoint
import com.oscarg798.amiibowiki.core.extensions.startDeepLinkIntent
import com.oscarg798.amiibowiki.core.failures.SearchGameFailure
import com.oscarg798.amiibowiki.searchgames.adapter.SearchResultAdapter
import com.oscarg798.amiibowiki.searchgames.adapter.SearchResultClickListener
import com.oscarg798.amiibowiki.searchgames.databinding.FragmentSearchResultBinding
import com.oscarg798.amiibowiki.searchgames.di.DaggerSearchResultComponent
import com.oscarg798.amiibowiki.searchgames.models.GameSearchParam
import com.oscarg798.amiibowiki.searchgames.models.ViewGameSearchResult
import com.oscarg798.amiibowiki.searchgames.mvi.SearchResultWish
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SearchResultFragment : Fragment(), SearchResultClickListener {

    @Inject
    lateinit var viewModel: SearchGamesResultViewModel

    private lateinit var binding: FragmentSearchResultBinding

    private var skeletonScreen: SkeletonScreen? = null
    private var gameSearchResultCoverImageView: ImageView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        DaggerSearchResultComponent.factory()
            .create(
                EntryPointAccessors.fromApplication(
                    requireActivity().application,
                    SearchGamesResultEntryPoint::class.java
                )
            )
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val newContext = ContextThemeWrapper(requireActivity(), R.style.AppTheme_SearchGames)
        val layoutInflater = inflater.cloneInContext(newContext)
        binding = FragmentSearchResultBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()
        setupViewModelInteractions()
    }

    private fun setup() {
        val shownAsGameRelatedSection =
            arguments?.getBoolean(ARGUMENT_SHOW_AS_RELATED_GAMES_SECTION)
                ?: throw IllegalArgumentException("ARGUMENT_SHOW_AS_RELATED_GAMES_SECTION should be provided as arguments")

        val relatedGamesViewsVisibility = if (shownAsGameRelatedSection) {
            View.VISIBLE
        } else {
            View.GONE
        }

        binding.tvSlideUpIndicator.visibility = relatedGamesViewsVisibility
        binding.tvTitle.visibility = relatedGamesViewsVisibility

        with(binding.rvGamesRelated) {
            layoutManager = LinearLayoutManager(context)
            adapter = SearchResultAdapter(this@SearchResultFragment)
        }
    }

    override fun onResultClicked(
        gameSearchResult: ViewGameSearchResult,
        coverImageView: ImageView
    ) {
        viewModel.onWish(SearchResultWish.ShowGameDetail(gameSearchResult.gameId))
        this.gameSearchResultCoverImageView = coverImageView
    }

    private fun setupViewModelInteractions() {
        viewModel.state.onEach {
            when {
                it.isIdling -> onIdle()
                it.isLoading -> showLoading()
                it.error != null -> showError(it.error)
                it.showingGameDetails != null -> showGameDetails(
                    it.showingGameDetails.gameId
                )
                it.gamesSearchResults != null -> showGameResults(it.gamesSearchResults.toList())
            }
        }.launchIn(lifecycleScope)
    }

    private fun hideError() {
        binding.tvError.visibility = View.GONE
    }

    fun search(gameSearchGameQueryParam: GameSearchParam) {
        viewModel.onWish(SearchResultWish.SearchGames(gameSearchGameQueryParam))
    }

    private fun onIdle() {
        hideError()
    }

    private fun showGameDetails(gameId: Int) {
        hideError()
        startDeepLinkIntent(
            GAME_DETAIL_DEEPLINK,
            gameSearchResultCoverImageView!!,
            Bundle().apply {
                putInt(ARGUMENT_GAME_ID, gameId)
            }
        )
    }

    private fun showGameResults(gameResults: List<ViewGameSearchResult>) {
        hideError()
        hideLoading()
        (binding.rvGamesRelated.adapter as SearchResultAdapter).updateProducts(gameResults)
    }

    private fun showLoading() {
        hideError()
        skeletonScreen = Skeleton.bind(binding.rvGamesRelated)
            .adapter((binding.rvGamesRelated.adapter as SearchResultAdapter))
            .load(R.layout.game_related_item_skeleton)
            .count(SHIMMER_ELEMENTS_COUNT)
            .shimmer(true)
            .show()
    }

    private fun showError(searchGameFailure: SearchGameFailure) {
        hideLoading()
        with(binding.tvError) {
            text = when (searchGameFailure) {
                is SearchGameFailure.DataSourceNotAvailable,
                is SearchGameFailure.DateSourceError -> context.getString(R.string.data_not_available_error)
                else -> context.getString(R.string.generic_error)
            }
        }
        binding.tvError.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        skeletonScreen?.hide()
        skeletonScreen = null
    }

    companion object {
        fun newInstance(shownAsGameRelatedSection: Boolean = true) = SearchResultFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARGUMENT_SHOW_AS_RELATED_GAMES_SECTION, shownAsGameRelatedSection)
            }
        }
    }
}

private const val SHIMMER_ELEMENTS_COUNT = 10
