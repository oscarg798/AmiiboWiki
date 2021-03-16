package com.oscarg798.amiibowiki.core.ui

import androidx.compose.foundation.Image
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.oscarg798.amiibowiki.core.R

@Composable
fun AmiiboWikiNavigationIcon(modifier: Modifier = Modifier, onBackPressed: () -> Unit) {
    IconButton(onClick = { onBackPressed() }, modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = "",
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
        )
    }
}
