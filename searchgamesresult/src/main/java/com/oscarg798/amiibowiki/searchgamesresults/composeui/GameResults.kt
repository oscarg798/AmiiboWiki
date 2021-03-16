package com.oscarg798.amiibowiki.searchgamesresults.composeui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import com.oscarg798.amiibowiki.core.spacingMedium
import com.oscarg798.amiibowiki.searchgamesresults.models.ViewGameSearchResult

@Composable
internal fun GameResults(
    gamesResult: List<ViewGameSearchResult>,
    onSearchResultClickListener: onSearchResultClickListener
) {
    LazyColumn(
        Modifier
            .layoutId(resultsListId)
            .padding(
                start = spacingMedium,
                end = spacingMedium
            )
    ) {
        items(
            items = gamesResult.toList()
        ) { SearchResult(item = it, onSearchResultClickListener) }
    }
}
