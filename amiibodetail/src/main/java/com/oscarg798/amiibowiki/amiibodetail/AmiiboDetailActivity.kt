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

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.deeplinkdispatch.DeepLink
import com.google.android.material.snackbar.Snackbar
import com.oscarg798.amiibowiki.amiibodetail.databinding.ActivityAmiiboDetailBinding
import com.oscarg798.amiibowiki.amiibodetail.di.DaggerAmiiboDetailComponent
import com.oscarg798.amiibowiki.core.AMIIBO_DETAIL_DEEPLINK
import com.oscarg798.amiibowiki.core.ViewModelFactory
import com.oscarg798.amiibowiki.core.constants.TAIL_ARGUMENT
import com.oscarg798.amiibowiki.core.di.CoreComponentProvider
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.setImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@ExperimentalCoroutinesApi
@DeepLink(AMIIBO_DETAIL_DEEPLINK)
class AmiiboDetailActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: ActivityAmiiboDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAmiiboDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DaggerAmiiboDetailComponent.factory()
            .create(
                intent.getStringExtra(TAIL_ARGUMENT)!!,
                (application as CoreComponentProvider).provideCoreComponent()
            )
            .inject(this)

        setup()
    }

    private fun setup() {
        val vm = ViewModelProvider(this, viewModelFactory).get(AmiiboDetailViewModel::class.java)
        vm.state.onEach {
            if (it.status is AmiiboDetailListViewState.Status.ShowingDetail) {
                showDetail(it.status.amiibo)
            } else if (it.error != null) {
                Snackbar.make(binding.ivImage, it.error.message ?: "Error", Snackbar.LENGTH_LONG)
                    .show()
            }
        }.launchIn(lifecycleScope)

        vm.onWish(AmiiboDetailWish.ShowDetail)
    }

    private fun showDetail(amiibo: Amiibo) {
        binding.ivImage.setImage(amiibo.image)
    }
}