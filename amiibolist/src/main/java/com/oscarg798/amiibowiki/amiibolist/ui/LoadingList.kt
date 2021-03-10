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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oscarg798.amiibowiki.amiibolist.mvi.AmiiboListViewState
import com.oscarg798.amiibowiki.core.commonui.LoadingImage
import com.oscarg798.amiibowiki.core.cornerRadiusSmall
import com.oscarg798.amiibowiki.core.elevationSmall
import com.oscarg798.amiibowiki.core.spacingMedium
import com.oscarg798.amiibowiki.core.spacingSmall
import com.oscarg798.amiibowiki.core.ui.Shimmer

@ExperimentalFoundationApi
@Composable
internal fun AmiiboLoadingList(state: AmiiboListViewState) {
    if (!state.loading) {
        return
    }

    LazyVerticalGrid(cells = GridCells.Fixed(GRID_COUNT)) {
        items(DEMO_LIST) {
            Card(
                elevation = elevationSmall,
                shape = RoundedCornerShape(cornerRadiusSmall),
                modifier = Modifier
                    .padding(spacingSmall)
            ) {
                Column() {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = spacingMedium)
                    ) {
                        Shimmer(
                            modifier = Modifier
                                .width(TEXT_SHIMMER_WIDTH)
                                .height(DETAIL_SIZE)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(top = spacingMedium)
                            .fillMaxWidth()
                    ) {
                        LoadingImage(modifier = Modifier.getAmiiboImageModifiers())
                    }

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(top = spacingMedium, bottom = spacingMedium)
                            .fillMaxWidth()
                    ) {
                        Shimmer(
                            modifier = Modifier
                                .width(TEXT_SHIMMER_WIDTH)
                                .height(DETAIL_SIZE)
                        )
                    }
                }
            }
        }
    }
}

private val TEXT_SHIMMER_WIDTH = 100.dp
private val DEMO_LIST = (1..20).toList()
private val DETAIL_SIZE = 25.dp
