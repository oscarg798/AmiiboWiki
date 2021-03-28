package com.oscarg798.amiibowiki.navigation.mvi

import com.oscarg798.amiibowiki.core.mvi.SideEffect
import com.oscarg798.amiibowiki.updatechecker.UpdateType

data class RequestUpdateSideEffect(val type: UpdateType) : SideEffect
