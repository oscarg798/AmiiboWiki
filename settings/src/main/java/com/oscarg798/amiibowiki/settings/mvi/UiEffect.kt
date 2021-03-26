package com.oscarg798.amiibowiki.settings.mvi

import com.oscarg798.amiibowiki.core.mvi.SideEffect

sealed class UiEffect : SideEffect {
    object ShowingDevelopmentActivity : UiEffect() {
        override fun equals(other: Any?): Boolean = false
    }

    object RecreateActivity : UiEffect() {
        override fun equals(other: Any?): Boolean = false
    }

    object ShowingDarkModeDialog : UiEffect() {
        override fun equals(other: Any?): Boolean = false
    }
}
