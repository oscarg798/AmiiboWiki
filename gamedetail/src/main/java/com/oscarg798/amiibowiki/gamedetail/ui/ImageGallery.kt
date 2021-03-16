package com.oscarg798.amiibowiki.gamedetail.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.oscarg798.amiibowiki.core.ui.AmiiboWikiTextAppearence
import com.oscarg798.amiibowiki.core.ui.Dimensions
import com.oscarg798.amiibowiki.core.ui.ImageFromUrl

@Composable
internal fun ImageGallery(
    layoutId: String,
    title: String,
    images: List<String>,
    onImageClick: () -> Unit
) {

    Column(
        Modifier
            .fillMaxWidth()
            .layoutId(layoutId)
    ) {
        Row {
            Text(
                text = title,
                style = AmiiboWikiTextAppearence.h2.merge(TextStyle(MaterialTheme.colors.onSurface)),
                modifier = Modifier.padding(bottom = Dimensions.Spacing.Small)
            )
        }

        LazyRow {
            items(images) { image ->
                ImageFromUrl(
                    url = image,
                    imageModifier = Modifier
                        .imageSize()
                        .clickable {
                            onImageClick()
                        },
                    loadingModifier = Modifier.imageSize()
                )
            }
        }
    }
}

private fun Modifier.imageSize() = width(200.dp)
    .padding(Dimensions.Spacing.Small)
    .height(100.dp)
