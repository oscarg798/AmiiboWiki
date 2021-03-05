/*
 * Copyright 2021 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.oscarg798.amiibowiki.searchgamesresults.composeui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.oscarg798.amiibowiki.core.*
import com.oscarg798.amiibowiki.core.commonui.LoadingImage
import com.oscarg798.amiibowiki.core.ui.Shimmer
import com.oscarg798.amiibowiki.searchgamesresults.gameAlternativeNameId
import com.oscarg798.amiibowiki.searchgamesresults.gameImageId
import com.oscarg798.amiibowiki.searchgamesresults.gameNameId

@Composable
internal fun Loading() {
    Box(Modifier.background(MaterialTheme.colors.background)) {
        LazyColumn(
            Modifier.padding(
                start = spacingMedium,
                end = spacingMedium
            )
        ) {
            items(LOADING_EXAMPLES) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacingSmall),
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = elevationExtraSmall,
                    shape = RoundedCornerShape(cornerRadiusSmall)
                ) {
                    ConstraintLayout(constraintSet = getConstratins()) {
                        LoadingAmiiboImage()
                        Shimmer(
                            modifier = Modifier
                                .layoutId(gameNameId)
                                .padding(spacingExtraSmall)
                                .width(LOADING_TEXT_WIDTH)
                                .height(LOADING_TEXT_HEIGHT)
                        )

                        Shimmer(
                            modifier = Modifier
                                .layoutId(gameAlternativeNameId)
                                .width(LOADING_TEXT_WIDTH)
                                .height(LOADING_TEXT_HEIGHT)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingAmiiboImage() {
    Column(Modifier.getAmiiboImageContainerModifier()) {
        LoadingImage(
            Modifier
                .padding(spacingSmall)
                .fillMaxWidth()
                .fillMaxHeight()
        )
    }
}

private fun getConstratins() = ConstraintSet {
    val gameImageId = createRefFor(gameImageId)
    val gameNameId = createRefFor(gameNameId)
    val gameAlternativeNameId = createRefFor(gameAlternativeNameId)

    constrain(gameImageId) {
        top.linkTo(parent.top)
        start.linkTo(parent.start, margin = spacingSmall)
    }

    constrain(gameNameId) {
        width = Dimension.fillToConstraints
        bottom.linkTo(gameAlternativeNameId.top)
        linkTo(
            start = gameImageId.end,
            end = parent.end,
            bias = 0f,
            startMargin = spacingExtraSmall,
            endMargin = spacingMedium
        )
    }

    constrain(gameAlternativeNameId) {
        width = Dimension.fillToConstraints
        linkTo(top = parent.top, bottom = parent.bottom, bias = 0.6f)
        linkTo(
            start = gameNameId.start,
            end = gameNameId.end,
            bias = 0f
        )
    }
}

private val LOADING_TEXT_WIDTH = 150.dp
private val LOADING_TEXT_HEIGHT = 30.dp
private val LOADING_EXAMPLES = (1..10).toList()
private const val BARRIER_ID = "barrier"
private const val RELEATED_GAMES_RIGHT_DRAWABLE_ID = "btnReleatedGamesDrawable"
private const val RELEATED_GAMES_TITLE_ID = "btnRelatedGamesTitle"
