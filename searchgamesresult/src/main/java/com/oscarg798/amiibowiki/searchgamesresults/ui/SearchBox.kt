package com.oscarg798.amiibowiki.searchgamesresults.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.oscarg798.amiibowiki.searchgamesresults.R

@Composable
internal fun SearchBox(
    query: TextFieldValue,
    onSearch: (TextFieldValue) -> Unit
) {
    TextField(
        value = query,
        maxLines = SearchBoxMaxLines,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        colors = TextFieldDefaults.textFieldColors(
            focusedLabelColor = MaterialTheme.colors.secondary,
            placeholderColor = MaterialTheme.colors.secondary,
            cursorColor = MaterialTheme.colors.secondary,
            backgroundColor = MaterialTheme.colors.surface,
            textColor = MaterialTheme.colors.onSurface
        ),
        onValueChange = onSearch,
        modifier = Modifier
            .fillMaxWidth()
            .layoutId(SearchBoxId),
        label = {
            Text(text = stringResource(id = R.string.search_placeholder))
        },
        leadingIcon = {
            Image(
                painterResource(id = R.drawable.ic_search),
                contentDescription = stringResource(R.string.search_icon_content_description),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
            )
        }
    )
}

private const val SearchBoxMaxLines = 1
