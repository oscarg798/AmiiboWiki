package com.oscarg798.amiibowiki.gamedetail.mvi

import com.oscarg798.amiibowiki.core.mvi.SideEffect

internal sealed class UiEffect : SideEffect {
    data class ShowingGameImages(val images: Collection<String>) : UiEffect()
    data class ShowingGameTrailer(val trailer: String) : UiEffect()
}
