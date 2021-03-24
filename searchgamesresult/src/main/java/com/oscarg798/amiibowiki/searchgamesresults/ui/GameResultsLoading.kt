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

package com.oscarg798.amiibowiki.searchgamesresults.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.oscarg798.amiibowiki.core.ui.Dimensions
import com.oscarg798.amiibowiki.core.ui.LoadingImage
import com.oscarg798.amiibowiki.core.ui.Shimmer
import com.oscarg798.amiibowiki.searchgamesresults.GameAlternativeNameId
import com.oscarg798.amiibowiki.searchgamesresults.GameImageId
import com.oscarg798.amiibowiki.searchgamesresults.GameNameId

@Composable
internal fun GameResultsLoading() {
    Box(
        Modifier
            .background(MaterialTheme.colors.background)
            .layoutId(LoadingId)
    ) {
        LazyColumn(
            Modifier.padding(
                start = Dimensions.Spacing.Medium,
                end = Dimensions.Spacing.Medium
            )
        ) {
            items(LoadingExamples) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimensions.Spacing.Small),
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = Dimensions.Elevation.ExtraSmall,
                    shape = RoundedCornerShape(Dimensions.CornerRadius.Small)
                ) {
                    ConstraintLayout(constraintSet = getConstraints()) {
                        AmiiboImageLoading()
                        Shimmer(
                            modifier = Modifier
                                .layoutId(GameNameId)
                                .padding(Dimensions.Spacing.ExtraSmall)
                                .width(LoadingTextWidth)
                                .height(LoadingTextHeight)
                        )

                        Shimmer(
                            modifier = Modifier
                                .layoutId(GameAlternativeNameId)
                                .width(LoadingTextWidth)
                                .height(LoadingTextHeight)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AmiiboImageLoading() {
    Column(Modifier.getAmiiboImageContainerModifier()) {
        LoadingImage(
            Modifier
                .padding(Dimensions.Spacing.Small)
                .fillMaxWidth()
                .fillMaxHeight()
        )
    }
}

private fun getConstraints() = ConstraintSet {
    val gameImageId = createRefFor(GameImageId)
    val gameNameId = createRefFor(GameNameId)
    val gameAlternativeNameId = createRefFor(GameAlternativeNameId)

    constrain(gameImageId) {
        top.linkTo(parent.top)
        start.linkTo(parent.start, margin = Dimensions.Spacing.Small)
    }

    constrain(gameNameId) {
        width = Dimension.fillToConstraints
        bottom.linkTo(gameAlternativeNameId.top)
        linkTo(
            start = gameImageId.end,
            end = parent.end,
            bias = 0f,
            startMargin = Dimensions.Spacing.ExtraSmall,
            endMargin = Dimensions.Spacing.Medium
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

private val LoadingTextWidth = 150.dp
private val LoadingTextHeight = 30.dp
private val LoadingExamples = (1..10).toList()
