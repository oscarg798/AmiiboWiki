package com.oscarg798.amiibowiki.core.extensions

import androidx.navigation.NavBackStackEntry

fun NavBackStackEntry.requireArguments() = arguments ?: error("Arguments must not be null")
