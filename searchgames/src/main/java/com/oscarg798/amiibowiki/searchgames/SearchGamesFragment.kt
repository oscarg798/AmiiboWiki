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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.oscarg798.amiibowiki.core.di.entrypoints.SearchGamesEntryPoint
import com.oscarg798.amiibowiki.searchgames.databinding.FragmentSearchGamesBinding
import com.oscarg798.amiibowiki.searchgames.di.DaggerSearchGameComponent
import com.oscarg798.amiibowiki.searchgames.models.GameSearchParam
import com.oscarg798.amiibowiki.searchgames.mvi.SearchGameWish
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SearchGamesFragment : Fragment() {

    @Inject
    lateinit var viewmodel: SearchGameViewModel

    private lateinit var binding: FragmentSearchGamesBinding

    private val searchFlow = MutableStateFlow<String>(EMPTY_SEARCH_QUERY)

    override fun onAttach(context: Context) {
        super.onAttach(context)

        DaggerSearchGameComponent.factory()
            .create(
                EntryPointAccessors.fromApplication(
                    requireActivity().application,
                    SearchGamesEntryPoint::class.java
                )
            ).inject(this)

        setupViewModelInteractions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        viewmodel.state.onEach { state ->
            when {
                state.searchingGames != null -> getSearchResultFragment().search(
                    GameSearchParam.StringQueryGameSearchParam(
                        state.searchingGames
                    )
                )
            }
        }.launchIn(lifecycleScope)

        searchFlow
            .filterNot { it.isEmpty() && it.length < 3 }
            .debounce(500L)
            .onEach {
                viewmodel.onWish(SearchGameWish.Search(it))
            }.launchIn(lifecycleScope)
    }

    private fun getSearchResultFragment() =
        childFragmentManager.findFragmentByTag(SearchResultFragment::class.simpleName) as SearchResultFragment
}

private const val EMPTY_SEARCH_QUERY = ""
