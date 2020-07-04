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

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.deeplinkdispatch.DeepLink
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.android.material.snackbar.Snackbar
import com.oscarg798.amiibowiki.AmiiboListViewModel
import com.oscarg798.amiibowiki.amiibolist.adapter.AmiiboClickListener
import com.oscarg798.amiibowiki.amiibolist.adapter.AmiiboListAdapter
import com.oscarg798.amiibowiki.amiibolist.databinding.ActivityAmiiboListBinding
import com.oscarg798.amiibowiki.amiibolist.di.DaggerAmiiboListComponent
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListWish
import com.oscarg798.amiibowiki.core.AMIIBO_DETAIL_DEEPLINK
import com.oscarg798.amiibowiki.core.AMIIBO_LIST_DEEPLINK
import com.oscarg798.amiibowiki.core.ViewModelFactory
import com.oscarg798.amiibowiki.core.constants.TAIL_ARGUMENT
import com.oscarg798.amiibowiki.core.di.CoreComponentProvider
import com.oscarg798.amiibowiki.core.mvi.ViewState
import com.oscarg798.amiibowiki.core.startDeepLinkIntent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@DeepLink(AMIIBO_LIST_DEEPLINK)
class AmiiboListActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var skeleton: SkeletonScreen? = null

    private lateinit var binding: ActivityAmiiboListBinding
    private lateinit var viewModel: AmiiboListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAmiiboListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DaggerAmiiboListComponent.builder()
            .coreComponent((application as CoreComponentProvider).provideCoreComponent())
            .build()
            .inject(this)

        setup()
        setupViewModelInteractions()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.amiibo_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter) {
            viewModel.onWish(AmiiboListWish.ShowFilters)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setup() {
        binding.srlMain.setColorSchemeColors(
            getColor(R.color.cinnabar), getColor(R.color.atlantis), getColor(R.color.picton_blue),
            getColor(R.color.viridian), getColor(R.color.tulip_tree), getColor(R.color.cerise)
        )
        with(binding.rvAmiiboList) {
            setHasFixedSize(false)
            layoutManager = GridLayoutManager(context, NUMBER_OF_COLUMNS)

            /**
             * TODO: this should generate a whish, and navigation should be thougth deeplink
             */
            adapter = AmiiboListAdapter(object : AmiiboClickListener {
                override fun onClick(viewAmiibo: ViewAmiibo) {
                    startDeepLinkIntent(AMIIBO_DETAIL_DEEPLINK,Bundle().apply {
                        putString(TAIL_ARGUMENT, viewAmiibo.tail)
                    })
                }
            })
        }
    }

    private fun setupViewModelInteractions() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(AmiiboListViewModel::class.java)

        viewModel.state
            .onEach {
                val state = it as AmiiboListViewState
                when {
                    state.loading is ViewState.LoadingState.Loading -> showLoading()
                    state.status is AmiiboListViewState.Status.AmiibosFetched -> showAmiibos(
                        state.status.amiibos
                    )
                    state.error != null -> showErrors(state.error.message!!)
                    state.showingFilters is AmiiboListViewState.ShowingFilters.FetchSuccess -> showFilters(
                        state.showingFilters.filters
                    )
                    state.filtering is AmiiboListViewState.Filtering.FetchSuccess -> {
                        showAmiibos(state.filtering.amiibos)
                    }
                }
            }.launchIn(lifecycleScope)


        merge(fetchAmiibos(), refresh())
            .onEach {
                viewModel.onWish(it)
            }.launchIn(lifecycleScope)
    }

    private fun showLoading() {
        binding.srlMain.isRefreshing = true
        if (skeleton == null) {
            skeleton = Skeleton.bind(binding.rvAmiiboList)
                .adapter(binding.rvAmiiboList.adapter)
                .load(R.layout.skeleton_amiibo_list_item)
                .count(SKELETON_ANIMATION_EXAMPLES_COUNT)
                .show()
        } else {
            skeleton?.show()
        }
    }

    private fun showFilters(filters: List<ViewAmiiboType>) {
        val adapter =
            ArrayAdapter<ViewAmiiboType>(
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
        builder.show()
    }

    private fun hideLoading() {
        skeleton?.hide()
        binding.srlMain.isRefreshing = false
    }

    private fun showAmiibos(amiibos: List<ViewAmiibo>) {
        hideLoading()
        (binding.rvAmiiboList.adapter as AmiiboListAdapter).submitList(amiibos)
    }

    private fun showErrors(error: String) {
        hideLoading()
        Snackbar.make(binding.srlMain, error, Snackbar.LENGTH_LONG).show()
    }

    private fun fetchAmiibos() = flowOf(AmiiboListWish.GetAmiibos)

    private fun refresh() = callbackFlow<AmiiboListWish> {
        binding.srlMain.setOnRefreshListener {
            offer(AmiiboListWish.RefreshAmiibos)
        }

        awaitClose { binding.srlMain.setOnRefreshListener(null) }
    }
}

private const val SKELETON_ANIMATION_EXAMPLES_COUNT = 10
private const val NUMBER_OF_COLUMNS = 2