package com.oscarg798.amiibowiki.nfcreader.mvi

import com.oscarg798.amiibowiki.core.models.AmiiboIdentifier
import com.oscarg798.amiibowiki.core.mvi.SideEffect

internal data class ShowAmiiboDetailsUiEffect(val amiiboIdentifier: AmiiboIdentifier): SideEffect{

    override fun equals(other: Any?): Boolean  = other === this

    override fun hashCode(): Int {
        return amiiboIdentifier.hashCode()
    }
}
