package com.oscarg798.amiibowiki.gamedetail.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.ui.ImageFromUrl
import com.oscarg798.amiibowiki.core.ui.Overlay
import com.oscarg798.amiibowiki.gamedetail.R

@Composable
internal fun GameHeaderImage(game: Game) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeaderImageHeight)
            .layoutId(MainImageId)
    ) {
        HeaderImage(game)
    }
}

@Composable
private fun HeaderImage(game: Game) {
    when (val image = game.getMainImage() ?: painterResource(id = R.drawable.ic_placeholder)) {
        is String -> ImageFromUrl(
            url = image,
            imageModifier = Modifier.getHeaderImageModifier(),
            contentScale = ContentScale.FillBounds,
            colorFilter = getOverlayFilter()
        )
        is Painter -> Image(
            painter = image,
            contentDescription = "",
            modifier = Modifier.getHeaderImageModifier(),
            colorFilter = getOverlayFilter()
        )
        else -> error("Image should be an url or painter resource")
    }
}

private fun getOverlayFilter() = ColorFilter.tint(Overlay, BlendMode.Multiply)

private fun Modifier.getHeaderImageModifier() = composed {
    Modifier
        .fillMaxHeight()
        .fillMaxWidth()
}

private fun Game.getMainImage() = when {
    artworks?.isEmpty() == false -> artworks!!.first()
    screenshots?.isEmpty() == false -> screenshots!!.first()
    else -> null
}

internal const val MainImageId = "MainImageId"
internal val HeaderImageHeight = 200.dp
