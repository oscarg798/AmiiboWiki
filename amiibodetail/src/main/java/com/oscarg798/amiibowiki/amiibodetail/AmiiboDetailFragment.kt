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

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.oscarg798.amiibowiki.amiibodetail.databinding.FragmentAmiiboDetailBinding
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailViewState
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailWish
import com.oscarg798.amiibowiki.amiibodetail.mvi.ShowingAmiiboDetailsParams
import com.oscarg798.amiibowiki.core.constants.ARGUMENT_TAIL
import com.oscarg798.amiibowiki.core.extensions.bundle
import com.oscarg798.amiibowiki.core.extensions.setImage
import com.oscarg798.amiibowiki.core.extensions.showExpandedImages
import com.oscarg798.amiibowiki.core.failures.AmiiboDetailFailure
import com.oscarg798.amiibowiki.core.utils.provideFactory
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import java.lang.Exception

@AndroidEntryPoint
class AmiiboDetailFragment : Fragment() {

    @Inject
    lateinit var factory: AmiiboDetailViewModel.Factory

    private val tail: String by bundle(ARGUMENT_TAIL)

    private val viewModel: AmiiboDetailViewModel by viewModels {
        provideFactory(factory, tail)
    }

    private lateinit var binding: FragmentAmiiboDetailBinding

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent { Greeting(viewModel) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        setupViewModelInteractions()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail)
    }

    private fun setupViewModelInteractions() {
        lifecycleScope.launchWhenResumed {
            viewModel.state.collect {
                when (it) {
                    is AmiiboDetailViewState.ShowingAmiiboImage -> showExpandedImages(listOf(it.imageUrl))
                    is AmiiboDetailViewState.ShowingRelatedGames -> showRelatedGames(it.amiiboId)
                }
            }
        }
    }

    private fun showError(amiiboDetailFailure: AmiiboDetailFailure) {
        hideLoading()
        Snackbar.make(
            binding.clMain,
            amiiboDetailFailure.message ?: getString(R.string.general_error),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showDetail(
        showingAmiiboDetailsParams: ShowingAmiiboDetailsParams
    ) {
        hideLoading()
        val viewAmiiboDetails = showingAmiiboDetailsParams.amiiboDetails

        with(binding) {
            ivImage.setImage(viewAmiiboDetails.imageUrl)
            tvGameCharacter.setText(viewAmiiboDetails.character)
            tvSerie.setText(viewAmiiboDetails.gameSeries)
            tvType.setText(viewAmiiboDetails.type)

            ivImage.setOnClickListener {
                viewModel.onWish(AmiiboDetailWish.ExpandAmiiboImage(viewAmiiboDetails.imageUrl))
            }

            btnRelatedGames.visibility =
                if (showingAmiiboDetailsParams.isRelatedGamesSectionEnabled) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            binding.btnRelatedGames.setOnClickListener {
                viewModel.onWish(AmiiboDetailWish.ShowRelatedGames)
            }
        }
    }

    private fun showRelatedGames(amiiboId: String) {
        findNavController().navigate(
            AmiiboDetailFragmentDirections.actionAmiiboDetailFragmentToSearchResultFragment(
                amiiboId,
                true
            )
        )
    }

    private fun showLoading() {
        with(binding) {
            shimmer.root.visibility = View.VISIBLE
            shimmer.shimmerViewContainer.startShimmer()
        }
    }

    private fun hideLoading() {
        with(binding) {
            shimmer.root.visibility = View.GONE
            shimmer.shimmerViewContainer.stopShimmer()
        }
    }
}


@Composable
fun ToolbarTitle() {
    Text("Amiibo Details")
}

@Composable
fun Toolbar() {
    TopAppBar(title = { ToolbarTitle() })
}

@Composable
fun DescriptionField(
    text: String,
    paddingTop: Dp = 0.dp,
    textStyle: TextStyle? = null,
    color: Color = Color.Black
) {
    Row(
        modifier = Modifier.padding(
            start = 16.dp, top = paddingTop
        )
    ) {
        if (textStyle != null) {
            Text(text = text, style = textStyle.merge(TextStyle(color = color)))
        } else {
            Text(text = text, style = TextStyle(color = color))
        }
    }
}

sealed class ImageResource {

    object Loading : ImageResource()
    data class Image(val image: Bitmap) : ImageResource()
}

@Composable
fun AmiiboImage(url: String, viewModel: AmiiboDetailViewModel) {

    var bitmapState by remember { mutableStateOf(ImageResource.Loading) as MutableState<ImageResource> }

    val target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
            bitmapState = ImageResource.Image(bitmap)
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            TODO("Not yet implemented")
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            TODO("Not yet implemented")
        }

    }
    Picasso.get().load(url).into(target)

    Row() {
        when (bitmapState) {
            is ImageResource.Loading -> Text("Loading")
            is ImageResource.Image -> Image(
                bitmap = (bitmapState as ImageResource.Image).image.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .clickable { viewModel.onWish(AmiiboDetailWish.ExpandAmiiboImage(url))  }
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
fun Detail(state: AmiiboDetailViewState.ShowingAmiiboDetails, viewModel: AmiiboDetailViewModel) {
    Column {
        DetailImage(state.showingAmiiboDetailsParams.amiiboDetails.imageUrl, viewModel)
        DetailDescription(state, viewModel)
    }
}

@Composable
private fun DetailImage(url: String, viewModel: AmiiboDetailViewModel) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .height(250.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        AmiiboImage(url, viewModel)
    }
}

@Composable
private fun DetailDescription(
    state: AmiiboDetailViewState.ShowingAmiiboDetails,
    viewModel: AmiiboDetailViewModel
) {
    Row() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 8.dp,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column() {
                DescriptionField(
                    text = state.showingAmiiboDetailsParams.amiiboDetails.name,
                    paddingTop = 16.dp,
                    textStyle = AmiiboWikiTextAppearence.h2
                )
                DescriptionField(
                    text = state.showingAmiiboDetailsParams.amiiboDetails.gameSeries,
                    textStyle = AmiiboWikiTextAppearence.body1
                )
                DescriptionField(
                    text = state.showingAmiiboDetailsParams.amiiboDetails.type,
                    textStyle = AmiiboWikiTextAppearence.body2,
                    color = Color(0xff616161)
                )

                if (state.showingAmiiboDetailsParams.isRelatedGamesSectionEnabled) {
                    RelatedGamesButton(viewModel)
                }

            }
        }
    }
}

@Composable
private fun RelatedGamesButton(viewModel: AmiiboDetailViewModel) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            shape = RoundedCornerShape(8.dp),
            onClick = { viewModel.onWish(AmiiboDetailWish.ShowRelatedGames) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            val set = ConstraintSet {
                val btnRelatedGamesTitle = createRefFor("btnRelatedGamesTitle")
                val imageDrawable = createRefFor("chevron")
                val barrier = createRefFor("barrier")

                constrain(btnRelatedGamesTitle) {
                    width = Dimension.wrapContent
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(barrier.start)
                    bottom.linkTo(parent.bottom)
                }

                constrain(barrier) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }

                constrain(imageDrawable) {
                    width = Dimension.wrapContent
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
            }

            ConstraintLayout(constraintSet = set, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Related Games",
                    style = AmiiboWikiTextAppearence.h3.merge(TextStyle(color = Color.White)),
                    modifier = Modifier
                        .layoutId("btnRelatedGamesTitle")
                        .padding(8.dp)
                )

                val image: Painter = painterResource(id = R.drawable.ic_next)
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier
                        .layoutId("chevron")
                        .padding(end = 8.dp)
                )
            }
        }
    }
}

@Composable
fun Greeting(viewModel: AmiiboDetailViewModel) {
    val state = viewModel.state2.observeAsState()
    Scaffold {
        if (state.value is AmiiboDetailViewState.ShowingAmiiboDetails) {
            Detail(state.value as AmiiboDetailViewState.ShowingAmiiboDetails, viewModel)
        } else {
            Text("Loading")
        }

    }
}


private const val NO_ELEVATION = 0f
