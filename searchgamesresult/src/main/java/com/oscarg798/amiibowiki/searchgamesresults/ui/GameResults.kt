package com.oscarg798.amiibowiki.searchgamesresults.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import com.oscarg798.amiibowiki.core.ui.Dimensions
import com.oscarg798.amiibowiki.searchgamesresults.models.ViewGameSearchResult

@Composable
internal fun GameResults(
    gamesResults: List<ViewGameSearchResult>,
    onSearchResultClickListener: onSearchResultClickListener
) {
    LazyColumn(
        Modifier
            .layoutId(ResultListId)
            .padding(
                start = Dimensions.Spacing.Medium,
                end = Dimensions.Spacing.Medium
            )
    ) {
        items(
            items = gamesResults.toList()
        ) { SearchResult(item = it, onSearchResultClickListener) }
    }
}
