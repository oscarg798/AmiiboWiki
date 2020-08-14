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

import android.app.AlertDialog
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airbnb.deeplinkdispatch.DeepLink
import com.google.android.material.appbar.AppBarLayout
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.oscarg798.amiibowiki.core.ViewModelFactory
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_GAME_ID
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_GAME_SERIES
import com.oscarg798.amiibowiki.core.constants.GAME_DETAIL_DEEPLINK
import com.oscarg798.amiibowiki.core.di.CoreComponentProvider
import com.oscarg798.amiibowiki.core.extensions.isAndroidQOrHigher
import com.oscarg798.amiibowiki.core.extensions.setImage
import com.oscarg798.amiibowiki.core.failures.GameDetailFailure
import com.oscarg798.amiibowiki.core.models.AgeRating
import com.oscarg798.amiibowiki.core.models.AgeRatingCategory
import com.oscarg798.amiibowiki.core.models.Config
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.models.Rating
import com.oscarg798.amiibowiki.gamedetail.databinding.ActivityGameDetailBinding
import com.oscarg798.amiibowiki.gamedetail.di.DaggerGameDetailComponent
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailViewState
import com.oscarg798.amiibowiki.gamedetail.mvi.GameDetailWish
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

@DeepLink(GAME_DETAIL_DEEPLINK)
class GameDetailActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var config: Config

    private lateinit var binding: ActivityGameDetailBinding

    private lateinit var currentState: GameDetailViewState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DaggerGameDetailComponent.builder()
            .coreComponent((application as CoreComponentProvider).provideCoreComponent())
            .build()
            .inject(this)

        setup()
        setupViewModel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setup() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.game_detail_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow_back))

        if (isDarkModeOn()) {
            return
        }

        binding.appBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                changeGameTitleColorOnLightMode(verticalOffset < TOOLBAR_EXPANDED_OFFSET)
            }
        })
    }

    private fun isDarkModeOn() =
        isAndroidQOrHigher() && AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

    private fun setupViewModel() {
        val vm = ViewModelProvider(this, viewModelFactory).get(GameDetailViewModel::class.java)
        vm.state.onEach { state ->
            currentState = state
            onLoadingEnd()
            when {
                state.isLoading -> onLoading()
                state.error != null -> showError()
                state.gameTrailer != null -> {
                    binding.tvTrailer.setOnClickListener {
                        val intent = YouTubeStandalonePlayer.createVideoIntent(
                            this,
                            config.googleAPIKey,
                            state.gameTrailer, START_TIME, true, true
                        )
                        startActivity(intent)
                    }
                }
                state.gameDetails != null -> showGameDetails(state.gameDetails)
            }
        }.launchIn(lifecycleScope)

        merge(
            flowOf(
                GameDetailWish.ShowGameDetail(
                    intent.getIntExtra(ARGUMENT_GAME_ID, DEFAULT_GAME_ID),
                    intent.getStringExtra(ARGUMENT_GAME_SERIES)!!
                )
            ),
            getTrailerClickFlow()
        ).onEach {
            vm.onWish(it)
        }.launchIn(lifecycleScope)
    }

    private fun showError() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.error_getting_game))
            .setPositiveButton(getString(R.string.error_dialog_positive_button)) { _, _ ->
                finish()
            }.setCancelable(false)
            .show()
    }

    private fun onLoading() {
        with(binding) {
            shimmer.root.visibility = View.VISIBLE
            toolbarImageSkeleton.visibility = View.VISIBLE

            shimmer.shimmerContainerGameDetail.startShimmer()
            toolbarImageSkeleton.startShimmer()

            toolbarImage.visibility = View.GONE
            tvTrailer.visibility = View.GONE
            gameDetailContent.visibility = View.GONE
        }
    }

    private fun onLoadingEnd() {
        with(binding) {
            shimmer.root.visibility = View.GONE
            toolbarImageSkeleton.visibility = View.GONE

            shimmer.shimmerContainerGameDetail.stopShimmer()
            toolbarImageSkeleton.stopShimmer()

            tvTrailer.visibility = View.VISIBLE
            toolbarImage.visibility = View.VISIBLE
            gameDetailContent.visibility = View.VISIBLE
        }
    }

    private fun getTrailerClickFlow() = callbackFlow<GameDetailWish> {
        binding.tvTrailer.setOnClickListener {
            val game =
                currentState.gameDetails ?: throw NullPointerException("Game can not be null")
            val trailerId =
                game.videosId?.firstOrNull() ?: throw GameDetailFailure.GameDoesNotIncludeTrailer(
                    game.id
                )
            offer(GameDetailWish.PlayGameTrailer(game.id, trailerId))
        }

        awaitClose {
            binding.tvTrailer.setOnClickListener { }
        }
    }

    private fun showGameDetails(game: Game) {
        binding.tvGameName.text = game.name
        binding.tvGameSeries.text = game.gameSeries

        game.showRating()
        game.showAgeRating()
        game.showSummary()
        game.showCover()
        game.showToolbarImage()
        game.showGameTrailer()
    }

    private fun changeGameTitleColorOnLightMode(isToolbarCollapsed: Boolean) {
        val layout = binding.tvGameName.layout ?: return
        val name = binding.tvGameName.text
        val lineEnd = layout.getLineEnd(NAME_FIRST_LINE)
        val spanableText = binding.tvGameName.text.substring(NAME_LINE_START, lineEnd)
        val spanable = SpannableString(name)
        spanable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    this@GameDetailActivity,
                    getFirstLineColor(isToolbarCollapsed)
                )
            ),
            NAME_LINE_START,
            spanableText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvGameName.setText(spanable, TextView.BufferType.SPANNABLE)
    }

    private fun getFirstLineColor(isToolbarCollapsed: Boolean): Int {
        return if (isToolbarCollapsed) {
            R.color.textColor
        } else {
            R.color.white
        }
    }

    private fun Game.showGameTrailer() {
        val visibility = if (videosId.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }

        binding.tvTrailer.visibility = visibility
    }

    private fun Game.showRating() {
        val visibility = if (rating == null) {
            View.GONE
        } else {
            View.VISIBLE
        }

        binding.tvGameRating.visibility = visibility
        binding.ivGameRating.visibility = visibility
    }

    private fun Game.showAgeRating() {
        ageRating?.forEach { ageRaiting ->
            when (ageRaiting.category) {
                is AgeRatingCategory.PEGI -> setPEGI(ageRaiting)
                is AgeRatingCategory.ESRB -> setERSB(ageRaiting)
            }
        }
    }

    private fun Game.showSummary() {
        val visibility = if (summary == null) {
            View.GONE
        } else {
            View.VISIBLE
        }

        binding.tvGameSummary.visibility = visibility
        summary?.let {
            binding.tvGameSummary.text = it
        }
    }

    private fun Game.showCover() {
        if (cover == null) {
            binding.ivGameCover.setImageDrawable(
                ContextCompat.getDrawable(
                    this@GameDetailActivity,
                    R.drawable.ic_placeholder
                )
            )
        } else {
            binding.ivGameCover.setImage(cover!!)
        }
    }

    private fun Game.showToolbarImage() {
        if (artworks != null) {
            if (artworks!!.isNotEmpty()) {
                binding.toolbarImage.setImage(artworks!!.first())
            }
        } else if (screenshots != null) {
            if (screenshots!!.isNotEmpty()) {
                binding.toolbarImage.setImage(screenshots!!.first())
            }
        }
    }

    private fun setPEGI(rating: AgeRating) {
        binding.ivPegiRating.visibility = View.VISIBLE
        binding.ivPegiRating.setImageDrawable(
            getDrawable(
                when (rating.rating) {
                    is Rating.PEGI3 -> R.drawable.ic_pegi_3
                    is Rating.PEGI7 -> R.drawable.ic_pegi_7
                    is Rating.PEGI12 -> R.drawable.ic_pegi_12
                    is Rating.PEGI16 -> R.drawable.ic_pegi_16
                    else -> R.drawable.ic_pegi_18
                }
            )
        )
    }

    private fun setERSB(rating: AgeRating) {
        binding.ivEsrbRating.visibility = View.VISIBLE
        binding.ivEsrbRating.setImageDrawable(
            getDrawable(
                when (rating.rating) {
                    is Rating.ESRBEC -> R.drawable.ic_early_childhood
                    is Rating.ESRB10 -> R.drawable.ic_esrb_everyone_10
                    is Rating.ESRBTeen -> R.drawable.ic_esrb_teen
                    is Rating.ESRBMature -> R.drawable.ic_esrb_mature
                    is Rating.ESRBAdultsOnly -> R.drawable.ic_esrb_ao
                    is Rating.ESRBEveryone -> R.drawable.ic_esrb_everyone_10
                    else -> R.drawable.ic_esrb_rp
                }
            )
        )
    }
}

private const val TOOLBAR_EXPANDED_OFFSET = 0
private const val NAME_FIRST_LINE = 0
private const val NAME_LINE_START = 0
private const val START_TIME = 0
private const val DEFAULT_GAME_ID = 0
