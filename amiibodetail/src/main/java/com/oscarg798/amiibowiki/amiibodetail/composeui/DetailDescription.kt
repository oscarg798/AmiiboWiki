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

package com.oscarg798.amiibowiki.amiibodetail.composeui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.oscarg798.amiibowiki.amiibodetail.AmiiboWikiTextAppearence
import com.oscarg798.amiibowiki.amiibodetail.models.ViewAmiiboDetails
import com.oscarg798.amiibowiki.core.spacingMedium
import com.oscarg798.amiibowiki.core.spacingSmall

@Composable
internal fun DetailDescription(
    amiibo: ViewAmiiboDetails,
    relatedGamesSectionEnabled: Boolean,
    onRelatedGamesButtonClick: () -> Unit
) {
    Row {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            backgroundColor = MaterialTheme.colors.surface,
            elevation = spacingSmall,
            shape = RoundedCornerShape(topStart = spacingMedium, topEnd = spacingMedium)
        ) {
            ConstraintLayout(
                modifier = Modifier.padding(spacingMedium),
                constraintSet = getConstraints()
            ) {
                DescriptionField(
                    text = amiibo.name,
                    textStyle = AmiiboWikiTextAppearence.h2,
                    id = AMIIBO_NAME_ID
                )
                DescriptionField(
                    text = amiibo.gameSeries,
                    textStyle = AmiiboWikiTextAppearence.body1.merge(TextStyle(MaterialTheme.colors.background)),
                    id = AMIIGO_GAME_SERIES_ID
                )
                DescriptionField(
                    text = amiibo.type,
                    textStyle = AmiiboWikiTextAppearence.body2,
                    color = MaterialTheme.colors.onSurface,
                    id = AMIIBO_TYPE_ID
                )

                if (relatedGamesSectionEnabled) {
                    RelatedGamesButton(id = RELATED_GAMES_ID) {
                        onRelatedGamesButtonClick()
                    }
                }
            }
        }
    }
}

@Composable
private fun DescriptionField(
    text: String,
    paddingTop: Dp = 0.dp,
    textStyle: TextStyle? = null,
    color: Color = MaterialTheme.colors.onBackground,
    id: String,
) {

    if (textStyle != null) {
        Text(
            text = text,
            style = textStyle.merge(TextStyle(color = color)),
            modifier = Modifier
                .padding(top = paddingTop)
                .layoutId(id)
        )
    } else {
        Text(text = text, style = TextStyle(color = color))
    }
}

@Composable
private fun getConstraints() = ConstraintSet {
    val amiiboNameId = createRefFor(AMIIBO_NAME_ID)
    val amiiboGameSeriesId = createRefFor(AMIIGO_GAME_SERIES_ID)
    val amiiboTypeId = createRefFor(AMIIBO_TYPE_ID)
    val relatedGamesId = createRefFor(RELATED_GAMES_ID)

    constrain(amiiboNameId) {
        width = Dimension.wrapContent
        top.linkTo(parent.top)
        linkTo(start = parent.start, end = parent.end, bias = ALIGN_TO_START)
    }

    constrain(amiiboGameSeriesId) {
        width = Dimension.wrapContent
        top.linkTo(amiiboNameId.bottom)
        linkTo(start = parent.start, end = parent.end, bias = ALIGN_TO_START)
    }

    constrain(amiiboTypeId) {
        width = Dimension.wrapContent
        top.linkTo(amiiboGameSeriesId.bottom)
        linkTo(start = parent.start, end = parent.end, bias = ALIGN_TO_START)
    }

    constrain(relatedGamesId) {
        start.linkTo(parent.start)
        end.linkTo(parent.end)
        linkTo(top = amiiboTypeId.bottom, bottom = parent.bottom, bias = ALIGN_TO_BOTTOM)
    }
}

private const val ALIGN_TO_BOTTOM = 1F
private const val ALIGN_TO_START = 0F
private const val AMIIBO_NAME_ID = "gameName"
private const val AMIIGO_GAME_SERIES_ID = "gameSeries"
private const val AMIIBO_TYPE_ID = "amiiboType"
private const val RELATED_GAMES_ID = "relatedGames"
