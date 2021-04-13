package com.oscarg798.amiibowiki.dashboard.mvi

import com.oscarg798.amiibowiki.core.mvi.SideEffect
import com.oscarg798.amiibowiki.updatechecker.UpdateType

sealed class UiEffect : SideEffect {
    data class RequestUpdateSideEffect(val type: UpdateType) : UiEffect()
    object HideUpdateDialog: UiEffect()
}

