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
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.deeplinkdispatch.DeepLink
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.android.material.snackbar.Snackbar
import com.oscarg798.amiibowiki.amiibolist.adapter.AmiiboClickListener
import com.oscarg798.amiibowiki.amiibolist.adapter.AmiiboListAdapter
import com.oscarg798.amiibowiki.amiibolist.databinding.ActivityAmiiboListBinding
import com.oscarg798.amiibowiki.amiibolist.di.DaggerAmiiboListComponent
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListWish
import com.oscarg798.amiibowiki.core.constants.AMIIBO_DETAIL_DEEPLINK
import com.oscarg798.amiibowiki.core.constants.AMIIBO_LIST_DEEPLINK
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_TAIL
import com.oscarg798.amiibowiki.core.constants.SETTINGS_DEEPLINK
import com.oscarg798.amiibowiki.core.di.entrypoints.AmiiboListEntryPoint
import com.oscarg798.amiibowiki.core.extensions.startDeepLinkIntent
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@DeepLink(AMIIBO_LIST_DEEPLINK)
class AmiiboListActivity :
    AppCompatActivity(),
    SearchView.OnQueryTextListener,
    SearchView.OnCloseListener {

    @Inject
    lateinit var viewModel: AmiiboListViewModel

    private var skeleton: SkeletonScreen? = null
    private var filterMenuItem: MenuItem? = null
    private var searchView: SearchView? = null

    private lateinit var binding: ActivityAmiiboListBinding
    private val searchFlow = MutableStateFlow(EMPTY_SEARCH_QUERY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAmiiboListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DaggerAmiiboListComponent.factory()
            .create(
                EntryPointAccessors.fromApplication(
                    application,
                    AmiiboListEntryPoint::class.java
                )
            )
            .inject(this)

        setup()
        setupViewModelInteractions()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(
            R.menu.amiibo_list_menu,
            menu
        )

        filterMenuItem = menu.findItem(R.id.action_filter)
        setupSearchView(menu.findItem(R.id.action_search))

        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (searchView?.isIconified == false) {
            searchView?.isIconified = true
            return
        }
        super.onBackPressed()
    }

    private fun setupSearchView(searchMenuItem: MenuItem) {
        searchView = searchMenuItem.actionView as? SearchView ?: return

        searchView?.setOnCloseListener(this)
        searchView?.setOnQueryTextListener(this)
    }

    override fun onClose(): Boolean {
        viewModel.onWish(AmiiboListWish.RefreshAmiibos)

        return false
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
        } else if (item.itemId == R.id.action_settings) {
            viewModel.onWish(AmiiboListWish.OpenSettings)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setup() {
        with(binding.srlMain) {
            setColorSchemeColors(
                getColor(R.color.cinnabar),
                getColor(R.color.atlantis),
                getColor(R.color.picton_blue),
                getColor(R.color.viridian),
                getColor(R.color.tulip_tree),
                getColor(R.color.cerise)
            )

            setOnRefreshListener {
                if (searchView?.isIconified == true) {
                    viewModel.onWish(AmiiboListWish.RefreshAmiibos)
                } else {
                    isRefreshing = false
                }
            }
        }

        with(binding.rvAmiiboList) {
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
        viewModel.onScreenShown()

        viewModel.state.onEach {
            val state = it
            when {
                state.isIdling -> onIdling()
                state.isLoading -> showLoading()
                state.isShowingSettings -> navigateToSettings()
                state.error != null -> showErrors(state.error.message!!)
                state.filters != null -> showFilters(state.filters.toList())
                state.amiiboTailToShow != null -> showAmiiboDetail(state.amiiboTailToShow)
                state.amiibos != null -> showAmiibos(state.amiibos.toList())
            }
        }.launchIn(lifecycleScope)

        searchFlow.debounce(SEARCH_DEBOUNCE)
            .filterNot { it.isEmpty() && it.length < MINUMUN_SEARCH_QUERY_LENGTH }
            .onEach {
                viewModel.onWish(AmiiboListWish.Search(it))
            }
            .launchIn(lifecycleScope)

        viewModel.onWish(AmiiboListWish.GetAmiibos)
    }

    private fun navigateToSettings() {
        startDeepLinkIntent(SETTINGS_DEEPLINK)
    }

    private fun onIdling() {
        // DO_NOTHING
    }

    private fun showLoading() {
        binding.srlMain.isRefreshing = true
        binding.rvAmiiboList.isEnabled = false
        filterMenuItem?.isEnabled = false

        skeleton = Skeleton.bind(binding.rvAmiiboList)
            .adapter(binding.rvAmiiboList.adapter)
            .load(R.layout.skeleton_amiibo_list_item)
            .count(SKELETON_ANIMATION_EXAMPLES_COUNT)
            .show()
    }

    private fun hideLoading() {
        skeleton?.hide()
        skeleton = null
        filterMenuItem?.isEnabled = true
        binding.rvAmiiboList.isEnabled = true
        binding.srlMain.isRefreshing = false
    }

    private fun showFilters(filters: List<ViewAmiiboType>) {
        val adapter = ArrayAdapter<ViewAmiiboType>(
            this,
            android.R.layout.select_dialog_singlechoice,
            filters
        )
        val builder = AlertDialog.Builder(this)
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

    private fun showAmiibos(amiibos: List<ViewAmiibo>) {
        hideLoading()
        (binding.rvAmiiboList.adapter as AmiiboListAdapter).submitList(amiibos)
    }

    private fun showErrors(error: String) {
        hideLoading()
        Snackbar.make(binding.srlMain, error, Snackbar.LENGTH_LONG).show()
    }

    private fun showAmiiboDetail(tail: String) {
        startDeepLinkIntent(
            AMIIBO_DETAIL_DEEPLINK,
            Bundle().apply {
                putString(ARGUMENT_TAIL, tail)
            }
        )
    }
}

private const val MINUMUN_SEARCH_QUERY_LENGTH = 3
private const val SEARCH_DEBOUNCE = 500L
private const val EMPTY_SEARCH_QUERY = ""
private const val SKELETON_ANIMATION_EXAMPLES_COUNT = 10
private const val NUMBER_OF_COLUMNS = 2
