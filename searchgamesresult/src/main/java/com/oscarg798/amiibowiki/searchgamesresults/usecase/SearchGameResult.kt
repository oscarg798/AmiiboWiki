package com.oscarg798.amiibowiki.searchgamesresults.usecase

import com.oscarg798.amiibowiki.core.models.GameSearchResult
import kotlinx.coroutines.flow.Flow

sealed class SearchGameResult {

    object NotAllowed : SearchGameResult()
    data class Allowed(val flow: Flow<Collection<GameSearchResult>>) : SearchGameResult()
}
