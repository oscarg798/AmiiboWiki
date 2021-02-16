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

package com.oscarg798.amiibowiki.amiibolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.deeplinkdispatch.DeepLink
import com.google.android.material.snackbar.Snackbar
import com.oscarg798.amiibowiki.amiibolist.adapter.AmiiboClickListener
import com.oscarg798.amiibowiki.amiibolist.adapter.AmiiboListAdapter
import com.oscarg798.amiibowiki.amiibolist.databinding.FragmentAmiiboListBinding
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListViewState
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListWish
import com.oscarg798.amiibowiki.core.constants.AMIIBO_LIST_DEEPLINK
import com.oscarg798.amiibowiki.core.logger.MixpanelLogger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
@DeepLink(AMIIBO_LIST_DEEPLINK)
class AmiiboListFragment :
    Fragment(),
    SearchView.OnQueryTextListener,
    MenuItem.OnActionExpandListener {

    @Inject
    lateinit var mixpanelLogger: MixpanelLogger

    private val viewModel: AmiiboListViewModel by viewModels()

    private var filterMenuItem: MenuItem? = null
    private var searchView: SearchView? = null

    private lateinit var binding: FragmentAmiiboListBinding

    private val searchFlow = MutableStateFlow(EMPTY_SEARCH_QUERY)

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (searchView?.isIconified == false) {
                searchView?.isIconified = true
                return
            }
            this.remove()
            requireActivity().onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setupViewModelInteractions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAmiiboListBinding.inflate(
            LayoutInflater.from(requireContext()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setup()
        viewModel.onScreenShown()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onWish(AmiiboListWish.GetAmiibos)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(
            R.menu.amiibo_list_menu,
            menu
        )

        filterMenuItem = menu.findItem(R.id.action_filter)
        setupSearchView(menu.findItem(R.id.action_search))

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroy() {
        mixpanelLogger.flush()
        super.onDestroy()
    }

    private fun setupSearchView(searchMenuItem: MenuItem) {
        searchView = searchMenuItem.actionView as? SearchView ?: return

        searchMenuItem.setOnActionExpandListener(this)
        searchView?.setOnQueryTextListener(this)
    }

    override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
        viewModel.onWish(AmiiboListWish.RefreshAmiibos)
        backPressedCallback.remove()
        return true
    }

    override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText ?: return false
        searchFlow.value = newText
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query ?: return false
        searchFlow.value = query
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter) {
            viewModel.onWish(AmiiboListWish.ShowFilters)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun FragmentAmiiboListBinding.setup() {
        val context = requireContext()
        with(srlMain) {
            setColorSchemeColors(
                context.getColor(R.color.cinnabar),
                context.getColor(R.color.atlantis),
                context.getColor(R.color.picton_blue),
                context.getColor(R.color.viridian),
                context.getColor(R.color.tulip_tree),
                context.getColor(R.color.cerise)
            )

            setOnRefreshListener {
                if (searchView?.isIconified == true) {
                    viewModel.onWish(AmiiboListWish.RefreshAmiibos)
                } else {
                    isRefreshing = false
                }
            }
        }

        with(rvAmiiboList) {
            setHasFixedSize(false)
            layoutManager = GridLayoutManager(context, NUMBER_OF_COLUMNS)
            adapter = AmiiboListAdapter(object : AmiiboClickListener {
                override fun onClick(viewAmiibo: ViewAmiibo) {
                    viewModel.onWish(AmiiboListWish.ShowAmiiboDetail(viewAmiibo))
                }
            })
        }
    }

    private fun setupViewModelInteractions() {
        lifecycleScope.launchWhenResumed {
            viewModel.state.collect { state ->
                when (state) {
                    AmiiboListViewState.Loading -> binding.showLoading()
                    is AmiiboListViewState.ShowingAmiibos -> binding.showAmiibos(state.amiibos.toList())
                    is AmiiboListViewState.ShowingFilters -> showFilters(state.filters.toList())
                    is AmiiboListViewState.ShowingAmiiboDetails -> showAmiiboDetail(state.amiiboId)
                    is AmiiboListViewState.Error -> binding.showErrors(state.error.message!!)
                }
            }
        }
        searchFlow.debounce(SEARCH_DEBOUNCE)
            .filterNot { it.isEmpty() && it.length < MINUMUN_SEARCH_QUERY_LENGTH }
            .onEach {
                viewModel.onWish(AmiiboListWish.Search(it))
            }.launchIn(lifecycleScope)
    }

    private fun onIdling() {
        // DO_NOTHING
    }

    private fun FragmentAmiiboListBinding.showLoading() {
        srlMain.isRefreshing = true
        rvAmiiboList.isEnabled = false
        filterMenuItem?.isEnabled = false

        rvAmiiboList.visibility = View.GONE
        listAnimation.shimmerLoadingView.visibility = View.VISIBLE
        listAnimation.shimmerLoadingView.startShimmer()
    }

    private fun FragmentAmiiboListBinding.hideLoading() {
        binding.rvAmiiboList.visibility = View.VISIBLE
        binding.listAnimation.shimmerLoadingView.stopShimmer()
        binding.listAnimation.shimmerLoadingView.visibility = View.GONE

        filterMenuItem?.isEnabled = true
        binding.rvAmiiboList.isEnabled = true
        binding.srlMain.isRefreshing = false
    }

    private fun showFilters(filters: List<ViewAmiiboType>) {
        val adapter = ArrayAdapter<ViewAmiiboType>(
            requireContext(),
            android.R.layout.select_dialog_singlechoice,
            filters
        )
        val builder = AlertDialog.Builder(requireContext())
        builder.setAdapter(adapter) { _, which ->
            val filter = adapter.getItem(which)
            require(filter != null)
            viewModel.onWish(AmiiboListWish.FilterAmiibos(filter))
        }
        builder.setOnCancelListener {
            viewModel.onWish(AmiiboListWish.FilteringCancelled)
        }
        builder.show()
    }

    private fun FragmentAmiiboListBinding.showAmiibos(amiibos: List<ViewAmiibo>) {
        hideLoading()
        (rvAmiiboList.adapter as AmiiboListAdapter).submitList(amiibos)
    }

    private fun FragmentAmiiboListBinding.showErrors(error: String) {
        hideLoading()
        Snackbar.make(srlMain, error, Snackbar.LENGTH_LONG).show()
    }

    private fun showAmiiboDetail(tail: String) {
        view?.findNavController()
            ?.navigate(AmiiboListFragmentDirections.actionNavigationListToAmiiboDetail(tail))
    }
}

private const val MINUMUN_SEARCH_QUERY_LENGTH = 3
private const val SEARCH_DEBOUNCE = 500L
private const val EMPTY_SEARCH_QUERY = ""
private const val SKELETON_ANIMATION_EXAMPLES_COUNT = 10
private const val NUMBER_OF_COLUMNS = 2
