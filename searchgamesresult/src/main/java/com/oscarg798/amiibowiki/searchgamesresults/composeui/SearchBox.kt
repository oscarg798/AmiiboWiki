package com.oscarg798.amiibowiki.searchgamesresults.composeui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.oscarg798.amiibowiki.searchgamesresults.R

@Composable
internal fun SearchBox(
    currentQuery: String,
    onSearch: (String) -> Unit
) {
    val query = remember { mutableStateOf(currentQuery) }

    TextField(
        value = query.value,
        colors = TextFieldDefaults.textFieldColors(
            placeholderColor = MaterialTheme.colors.secondary,
            cursorColor = MaterialTheme.colors.secondary,
            backgroundColor = MaterialTheme.colors.surface,
            textColor = MaterialTheme.colors.onSurface
        ),
        onValueChange = { value ->
            query.value = value
            onSearch(value)
        },
        modifier = Modifier
            .fillMaxWidth()
            .layoutId(searchBoxId),
        label = {
            Text(text = stringResource(id = R.string.search_placeholder))
        },
        leadingIcon = {
            Image(
                painterResource(id = R.drawable.ic_search), contentDescription = "",
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )
        }
    )
}
