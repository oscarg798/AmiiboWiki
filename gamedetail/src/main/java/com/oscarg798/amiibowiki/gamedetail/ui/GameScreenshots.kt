package com.oscarg798.amiibowiki.gamedetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.gamedetail.R
import com.oscarg798.amiibowiki.gamedetail.models.ExpandableImageParam
import com.oscarg798.amiibowiki.gamedetail.models.ExpandableImageType

@Composable
internal fun GameScreenshots(
    game: Game,
    onImageClick: (ExpandableImageParam) -> Unit
) {
    ImageGallery(
        layoutId = ScreenshotsId,
        title = stringResource(id = R.string.screenshots_label),
        images = game.screenshots!!.toList()
    ) {
        onImageClick(ExpandableImageParam(it, ExpandableImageType.Screenshot))
    }
}

internal const val ScreenshotsId = "ScreenshotsId"
