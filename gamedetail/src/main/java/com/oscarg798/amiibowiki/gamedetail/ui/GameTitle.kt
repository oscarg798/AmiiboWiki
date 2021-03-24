package com.oscarg798.amiibowiki.gamedetail.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.ui.AmiiboWikiTextAppearence

@Composable
internal fun GameTitle(game: Game) {
    Text(
        text = game.name,
        style = AmiiboWikiTextAppearence.h2.merge(TextStyle(MaterialTheme.colors.onSurface)),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .layoutId(TitleId)
    )
}

internal const val TitleId = "TitleId"
