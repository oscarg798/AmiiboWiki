package com.oscarg798.amiibowiki.gamedetail.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oscarg798.amiibowiki.core.ui.Dimensions
import com.oscarg798.amiibowiki.gamedetail.R

@Preview
@Composable
internal fun TrailerButton(onTrailerButtonClicked: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentWidth()
            .layoutId(TrailerButtonId)
            .clickable { onTrailerButtonClicked() }
            .border(
                width = BorderWidth,
                color = Color.White,
                shape = RoundedCornerShape(Dimensions.CornerRadius.Medium)
            )
    ) {
        Column(
            Modifier
                .padding(
                    top = Dimensions.Spacing.Medium,
                    bottom = Dimensions.Spacing.Medium,
                    start = Dimensions.Spacing.Medium
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_play),
                contentDescription = stringResource(R.string.trailer_content_description),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }

        Column(
            modifier = Modifier
                .padding(
                    top = Dimensions.Spacing.Medium,
                    start = Dimensions.Spacing.Small,
                    bottom = Dimensions.Spacing.Medium,
                    end = Dimensions.Spacing.Medium
                ),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.trailer_hint),
                style = MaterialTheme.typography.body1.merge(
                    TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            )
        }
    }
}

internal const val TrailerButtonId = "TrailerButtond"
private val BorderWidth = 2.dp
