package com.oscarg798.amiibowiki.gamedetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.gamedetail.R
import com.oscarg798.amiibowiki.gamedetail.models.ExpandableImageParam
import com.oscarg798.amiibowiki.gamedetail.models.ExpandableImageType

@Composable
internal fun GameArtworks(
    game: Game,
    onImageClick: (ExpandableImageParam) -> Unit
) {
    ImageGallery(
        layoutId = ArtworksId,
        title = stringResource(id = R.string.artworks_label),
        images = game.artworks!!.toList()
    ) {
        onImageClick(ExpandableImageParam(it, ExpandableImageType.Artwork))
    }
}

internal const val ArtworksId = "ArtworksId"
