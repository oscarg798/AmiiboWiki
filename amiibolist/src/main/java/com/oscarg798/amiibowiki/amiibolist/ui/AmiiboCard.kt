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

package com.oscarg798.amiibowiki.amiibolist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.oscarg798.amiibowiki.amiibolist.ViewAmiibo
import com.oscarg798.amiibowiki.core.ui.Dimensions
import com.oscarg798.amiibowiki.core.ui.ImageFromUrl

@Composable
internal fun AmiiboCard(amiibo: ViewAmiibo, amiiboClickListener: (ViewAmiibo) -> Unit) {
    Card(
        elevation = Dimensions.Elevation.Small,
        shape = RoundedCornerShape(Dimensions.CornerRadius.Small),
        modifier = Modifier
            .clickable { amiiboClickListener(amiibo) }
            .padding(Dimensions.Spacing.Small)
    ) {
        Column(Modifier.padding(Dimensions.Spacing.Medium)) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    amiibo.serie,
                    style = MaterialTheme.typography.body2.merge(TextStyle(MaterialTheme.colors.onSurface)),
                    maxLines = 1
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(top = Dimensions.Spacing.Medium)
                    .fillMaxWidth()
            ) {
                ImageFromUrl(
                    amiibo.image,
                    Modifier.getAmiiboImageModifiers(),
                    Modifier.getAmiiboImageModifiers()
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(top = Dimensions.Spacing.Medium, bottom = Dimensions.Spacing.Medium)
                    .fillMaxWidth()
            ) {
                Text(
                    amiibo.name,
                    style = MaterialTheme.typography.body1.merge(TextStyle(MaterialTheme.colors.onBackground)),
                    maxLines = 1
                )
            }
        }
    }
}

internal fun Modifier.getAmiiboImageModifiers() = Modifier.size(100.dp)
