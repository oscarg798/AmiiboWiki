package com.oscarg798.amiibowiki.searchgamesresults.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.oscarg798.amiibowiki.searchgamesresults.R

@Composable
fun EmptyState() {
    ConstraintLayout(
        constraintSet = getConstraintSet(),
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .layoutId(EmptyStateId)
            .fillMaxWidth()
    ) {
        Image(
            modifier = Modifier
                .layoutId(EmptyStateIconId)
                .size(EmptyStateSize),
            painter = painterResource(id = R.drawable.ic_game_pad), contentDescription = ""
        )

        Text(
            stringResource(id = R.string.search_guideline), modifier = Modifier.layoutId(EmptyStateTextId),
            style = MaterialTheme.typography.body2.merge(TextStyle(MaterialTheme.colors.onBackground))
        )
    }
}

private fun getConstraintSet() = ConstraintSet {
    val emptyStateTextId = createRefFor(EmptyStateTextId)
    val emptyStateIconId = createRefFor(EmptyStateIconId)

    constrain(emptyStateIconId) {
        width = Dimension.fillToConstraints
        linkTo(top = parent.top, bottom = parent.bottom, bias = 0.4f)
        linkTo(start = parent.start, end = parent.end)
    }

    constrain(emptyStateTextId) {
        width = Dimension.wrapContent
        top.linkTo(emptyStateIconId.bottom)
        linkTo(start = parent.start, end = parent.end)
    }
}

private const val EmptyStateTextId = "emptyStateTextId"
private const val EmptyStateIconId = "emptyStateIconId"
private val EmptyStateSize = 100.dp
