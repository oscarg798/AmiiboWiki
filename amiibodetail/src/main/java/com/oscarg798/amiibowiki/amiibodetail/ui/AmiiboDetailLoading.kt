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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oscarg798.amiibowiki.core.ui.Dimensions
import com.oscarg798.amiibowiki.core.ui.LoadingImage
import com.oscarg798.amiibowiki.core.ui.Shimmer

@Composable
internal fun AmiiboDetailLoading() {
    Column {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxWidth()
                .padding(top = Dimensions.Spacing.Medium),
            horizontalArrangement = Arrangement.Center
        ) {
            LoadingImage(
                modifier = Modifier
                    .size(AmiiboImageSize, AmiiboImageSize)
                    .padding(end = Dimensions.Spacing.Small)
            )
        }

        Row {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                backgroundColor = MaterialTheme.colors.surface,
                shape = RoundedCornerShape(
                    topStart = Dimensions.Spacing.Medium,
                    topEnd = Dimensions.Spacing.Medium
                )
            ) {
                Shimmer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DETAIL_SIZE)
                )
            }
        }
    }
}

private val DETAIL_SIZE = 50.dp
