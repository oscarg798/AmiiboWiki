package com.oscarg798.amiibowiki.splash.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.oscarg798.amiibowiki.R

@Composable
internal fun AppTitle() {
    Text(
        text = stringResource(id = R.string.app_name),
        style = TextStyle(
            fontFamily = FontFamily(
                Font(R.font.sketch_block)
            ),
            color = MaterialTheme.colors.onBackground,
            fontSize = FontSize
        ),
        modifier = Modifier.layoutId(TitleId)
    )
}

private val FontSize = 28.sp
