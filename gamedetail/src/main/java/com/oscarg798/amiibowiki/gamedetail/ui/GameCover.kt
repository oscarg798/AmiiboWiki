package com.oscarg798.amiibowiki.gamedetail.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.ui.ImageFromUrl
import com.oscarg798.amiibowiki.gamedetail.R
import com.oscarg798.amiibowiki.gamedetail.models.ExpandableImageParam
import com.oscarg798.amiibowiki.gamedetail.models.ExpandableImageType

@Composable
internal fun GameCover(
    game: Game,
    onImageClick: (Collection<ExpandableImageParam>) -> Unit
) {
    when (val cover = game.cover ?: getImagePlaceHolder()) {
        is String ->
            Box(
                Modifier
                    .layoutId(CoverId)
                    .width(CoverImageWidth)
                    .height(CoverImageHeight)
            ) {
                ImageFromUrl(
                    url = cover,
                    contentDescription = stringResource(R.string.cover_image_content_description),
                    imageModifier = Modifier
                        .getCoverModifier()
                        .clickable {
                            onImageClick(
                                listOf(
                                    ExpandableImageParam(
                                        cover,
                                        ExpandableImageType.Cover
                                    )
                                )
                            )
                        }
                )
            }
        is Painter -> Image(
            painter = cover,
            contentDescription = stringResource(R.string.cover_image_content_description),
            modifier = Modifier.getCoverModifier()
        )
    }
}

@Composable
private fun getImagePlaceHolder() = painterResource(id = R.drawable.ic_placeholder)

private fun Modifier.getCoverModifier() = composed {
    Modifier
        .width(CoverImageWidth)
        .height(CoverImageHeight)
}

internal const val CoverId = "CoverId"
internal val CoverImageWidth = 100.dp
internal val CoverImageHeight = 150.dp
