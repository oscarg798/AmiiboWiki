package com.oscarg798.amiibowiki.searchgamesresults.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.oscarg798.amiibowiki.core.ui.Dimensions
import com.oscarg798.amiibowiki.searchgamesresults.R

@Composable
fun IdlingState() {
    ConstraintLayout(
        constraintSet = getConstraintSet(),
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .layoutId(EmptyStateId)
            .fillMaxWidth()
    ) {
        Image(
            modifier = Modifier
                .layoutId(IdlingIcon)
                .size(EmptyStateSize),
            painter = painterResource(id = R.drawable.ic_game_pad),
            contentDescription = stringResource(
                R.string.idling_state_icon_content_description
            )
        )

        Text(
            text = stringResource(id = R.string.search_guideline),
            modifier = Modifier.layoutId(IdlingTextId).padding(Dimensions.Spacing.Medium),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2.merge(TextStyle(MaterialTheme.colors.onBackground))
        )
    }
}

private fun getConstraintSet() = ConstraintSet {
    val idlingTextId = createRefFor(IdlingTextId)
    val idlingIconId = createRefFor(IdlingIcon)

    constrain(idlingIconId) {
        width = Dimension.fillToConstraints
        linkTo(top = parent.top, bottom = parent.bottom, bias = 0.4f)
        linkTo(start = parent.start, end = parent.end)
    }

    constrain(idlingTextId) {
        width = Dimension.wrapContent
        top.linkTo(idlingIconId.bottom)
        linkTo(start = parent.start, end = parent.end)
    }
}

private const val IdlingTextId = "idlingTextId"
private const val IdlingIcon = "IdlingIconId"
private val EmptyStateSize = 100.dp
