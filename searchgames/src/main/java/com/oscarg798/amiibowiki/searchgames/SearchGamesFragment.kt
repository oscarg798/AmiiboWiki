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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.oscarg798.amiibowiki.searchgames.databinding.FragmentSearchGamesBinding
import com.oscarg798.amiibowiki.searchgames.mvi.SearchGameWish
import com.oscarg798.amiibowiki.searchgames.mvi.SearchGamesViewState
import com.oscarg798.amiibowiki.searchgamesresults.SearchResultFragment
import com.oscarg798.amiibowiki.searchgamesresults.models.GameSearchParam
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SearchGamesFragment : Fragment() {

    private val viewmodel: SearchGameViewModel by viewModels()

    private lateinit var binding: FragmentSearchGamesBinding

    private val searchFlow = MutableStateFlow<String>(EMPTY_SEARCH_QUERY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModelInteractions()
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchGamesBinding.inflate(
            LayoutInflater.from(requireContext()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        childFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                SearchResultFragment.newInstance(false),
                SearchResultFragment::class.simpleName
            )
            .commitNow()

        binding.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchFlow.value = it }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchFlow.value = it }
                return true
            }
        })
    }

    private fun setupViewModelInteractions() {
        lifecycleScope.launchWhenResumed {
            viewmodel.state.collect { state ->
                if (state is SearchGamesViewState.SearchingGames) getSearchResultFragment().search(
                    GameSearchParam.StringQueryGameSearchParam(
                        state.query
                    )
                )
            }
        }

        searchFlow
            .filterNot { it.isEmpty() && it.length < MINUMUN_SEARCH_QUERY_LENGTH }
            .debounce(SEARCH_DEBOUNCE)
            .onEach {
                viewmodel.onWish(SearchGameWish.Search(it))
            }.launchIn(lifecycleScope)
    }

    private fun getSearchResultFragment() =
        childFragmentManager.findFragmentByTag(SearchResultFragment::class.simpleName) as SearchResultFragment
}

private const val EMPTY_SEARCH_QUERY = ""
private const val MINUMUN_SEARCH_QUERY_LENGTH = 3
private const val SEARCH_DEBOUNCE = 500L
