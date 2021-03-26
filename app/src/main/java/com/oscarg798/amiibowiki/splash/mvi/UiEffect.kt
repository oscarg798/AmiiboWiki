package com.oscarg798.amiibowiki.splash.mvi

import com.oscarg798.amiibowiki.core.mvi.SideEffect

internal sealed class UiEffect : SideEffect{

    object Navigate : UiEffect()
}
