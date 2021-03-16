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
package com.oscarg798.amiibowiki.gamedetail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import com.airbnb.deeplinkdispatch.DeepLink
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_GAME_ID
import com.oscarg798.amiibowiki.core.constants.GAME_DETAIL_DEEPLINK
import com.oscarg798.amiibowiki.core.extensions.showExpandedImages
import com.oscarg798.amiibowiki.core.models.Config
import com.oscarg798.amiibowiki.core.utils.SavedInstanceViewModelFactory
import com.oscarg798.amiibowiki.core.utils.bundle
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailWish
import com.oscarg798.amiibowiki.gamedetail.mvi.UiEffect
import com.oscarg798.amiibowiki.gamedetail.ui.Screen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
@DeepLink(GAME_DETAIL_DEEPLINK)
internal class GameDetailActivity : AppCompatActivity() {

    @Inject
    lateinit var config: Config

    @Inject
    lateinit var factory: GameDetailViewModel.Factory

    private val gameId: Int by bundle(ARGUMENT_GAME_ID)

    private val viewModel: GameDetailViewModel by viewModels {
        SavedInstanceViewModelFactory(
            {
                factory.create(gameId)
            },
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            ComposeView(this).apply {
                setContent {
                    Screen(
                        viewModel = viewModel,
                        coroutineScope = lifecycleScope,
                        onTrailerClicked = {
                            viewModel.onWish(GameDetailWish.PlayGameTrailer)
                        }
                    ) {
                        onBackPressed()
                    }
                }
            }
        )

        setupViewModel()
        viewModel.onWish(GameDetailWish.ShowGameDetail)
    }

    private fun setupViewModel() {
        lifecycleScope.launchWhenResumed {
            viewModel.uiEffect.collect {
                when (it) {
                    is UiEffect.ShowingGameImages -> showExpandedImages(it.images)
                    is UiEffect.ShowingGameTrailer -> showGameTrailer(it.trailer)
                }
            }
        }
    }

    private fun showGameTrailer(trailer: String) {
        val intent = YouTubeStandalonePlayer.createVideoIntent(
            this,
            config.googleAPIKey,
            trailer, START_TIME, true, true
        )
        startActivity(intent)
    }
}

private const val START_TIME = 0
