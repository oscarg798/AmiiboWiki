package com.oscarg798.amiibowiki.core.utils

import androidx.navigation.NavController

fun NavController.requireCurrentBackStackEntryArguments() = currentBackStackEntry?.arguments
    ?: throw IllegalArgumentException("No arguments found in current back stack entry")

fun NavController.requirePreviousBackStackEntryArguments() = previousBackStackEntry?.arguments
    ?: throw IllegalArgumentException("Previous back stack does not have arguments")
