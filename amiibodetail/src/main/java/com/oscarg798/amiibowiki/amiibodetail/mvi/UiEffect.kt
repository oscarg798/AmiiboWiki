package com.oscarg798.amiibowiki.amiibodetail.mvi

import com.oscarg798.amiibowiki.core.mvi.SideEffect

internal sealed class UiEffect : SideEffect {
    data class ShowAmiiboImage(val url: String) : UiEffect()
    data class ShowRelatedGames(val amiiboId: String) : UiEffect()
}
