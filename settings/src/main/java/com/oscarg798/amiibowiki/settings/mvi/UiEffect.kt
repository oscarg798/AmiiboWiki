package com.oscarg798.amiibowiki.settings.mvi

import com.oscarg798.amiibowiki.core.mvi.SideEffect

internal sealed class UiEffect : SideEffect {
    object ShowingDevelopmentActivity : UiEffect()
    object RecreateActivity : UiEffect()
    object ShowingDarkModeDialog : UiEffect()
}
