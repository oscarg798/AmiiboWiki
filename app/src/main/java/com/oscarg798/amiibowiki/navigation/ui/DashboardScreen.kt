package com.oscarg798.amiibowiki.navigation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.oscarg798.amiibowiki.core.ui.ThemeContainer

@Composable
internal fun DashboardScreen() {
    val navController = rememberNavController()

    val snackbarHostState = remember { SnackbarHostState() }

    ThemeContainer {
        Scaffold(
            scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
            bottomBar = {
                AppBottomNavigationBar(
                    navController, listOf(
                        NavigationScreens.AmiiboList,
                        NavigationScreens.GameSearch
                    )
                )
            },
        ) { innerPadding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                MainNavigationHost(
                    snackbarHostState = snackbarHostState,
                    navController = navController
                )
            }
        }
    }
}
