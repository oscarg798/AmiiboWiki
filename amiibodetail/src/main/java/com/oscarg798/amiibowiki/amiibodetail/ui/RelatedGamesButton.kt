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

package com.oscarg798.amiibowiki.amiibodetail.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.oscarg798.amiibowiki.amiibodetail.R
import com.oscarg798.amiibowiki.core.ui.AmiiboWikiTextAppearence
import com.oscarg798.amiibowiki.core.ui.Dimensions

@Composable
internal fun RelatedGamesButton(id: String, onRelatedGamesButtonClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .layoutId(id)
    ) {
        Button(
            shape = RoundedCornerShape(Dimensions.CornerRadius.Small),
            onClick = { onRelatedGamesButtonClick() },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
            modifier = Modifier
                .padding(top = Dimensions.Spacing.Medium)
                .fillMaxWidth()
        ) {

            val set = getConstraints()

            ConstraintLayout(constraintSet = set, modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(id = R.string.related_games_title),
                    style = AmiiboWikiTextAppearence.h3.merge(TextStyle(MaterialTheme.colors.onSecondary)),
                    modifier = Modifier
                        .layoutId(RELEATED_GAMES_TITLE_ID)
                        .padding(Dimensions.Spacing.Small)
                )

                val image: Painter = painterResource(id = R.drawable.ic_next)
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier
                        .layoutId(RELEATED_GAMES_RIGHT_DRAWABLE_ID)
                        .padding(end = Dimensions.Spacing.Small)
                )
            }
        }
    }
}

private fun getConstraints() = ConstraintSet {
    val btnRelatedGamesTitle = createRefFor(RELEATED_GAMES_TITLE_ID)
    val imageDrawable = createRefFor(RELEATED_GAMES_RIGHT_DRAWABLE_ID)
    val barrier = createRefFor(BARRIER_ID)

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

private const val BARRIER_ID = "barrier"
private const val RELEATED_GAMES_RIGHT_DRAWABLE_ID = "btnReleatedGamesDrawable"
private const val RELEATED_GAMES_TITLE_ID = "btnRelatedGamesTitle"
