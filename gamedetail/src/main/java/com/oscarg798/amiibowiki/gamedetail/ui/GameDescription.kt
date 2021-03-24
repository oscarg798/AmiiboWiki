package com.oscarg798.amiibowiki.gamedetail.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.oscarg798.amiibowiki.core.ui.AmiiboWikiTextAppearence
import com.oscarg798.amiibowiki.gamedetail.R

@Composable
internal fun GameDescription(description: String) {
    var descriptionState by remember { mutableStateOf(DescriptionState.Collapsed as DescriptionState) }

    Column(
        Modifier
            .fillMaxWidth()
            .layoutId(DescriptionId)
    ) {
        Row {
            Text(
                style = AmiiboWikiTextAppearence.body2.merge(TextStyle(MaterialTheme.colors.onBackground)),
                text = description,
                maxLines = when (descriptionState) {
                    is DescriptionState.Expanded -> MaxLinesAllowedOnExpanded
                    else -> MaxLinesAllowedOnCollapsed
                }
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(
                    id = when (descriptionState) {
                        is DescriptionState.Expanded -> R.drawable.ic_collapse
                        else -> R.drawable.ic_expand
                    }
                ),
                contentDescription = when (descriptionState) {
                    DescriptionState.Collapsed -> stringResource(R.string.collapse_content_description)
                    DescriptionState.Expanded -> stringResource(R.string.expaind_icon_content_description)
                },
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                modifier = Modifier
                    .size(IconSize)
                    .clickable {
                        descriptionState = when (descriptionState) {
                            is DescriptionState.Expanded -> DescriptionState.Collapsed
                            else -> DescriptionState.Expanded
                        }
                    }
            )
        }
    }
}

private sealed class DescriptionState {
    object Expanded : DescriptionState()
    object Collapsed : DescriptionState()
}

private val IconSize = 30.dp
private const val MaxLinesAllowedOnCollapsed = 4
private const val MaxLinesAllowedOnExpanded = Int.MAX_VALUE

internal const val DescriptionId = "DescriptionId"
