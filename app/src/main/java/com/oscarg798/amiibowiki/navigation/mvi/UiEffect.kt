package com.oscarg798.amiibowiki.navigation.mvi

import com.oscarg798.amiibowiki.core.mvi.SideEffect
import com.oscarg798.amiibowiki.updatechecker.UpdateType

data class RequestUpdateSideEffect(val type: UpdateType) : SideEffect{

    override fun equals(other: Any?): Boolean = other === this
    override fun hashCode(): Int {
        return type.hashCode()
    }
}
