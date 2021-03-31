package com.oscarg798.amiibowiki.dashboard.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.rememberNavController
import com.oscarg798.amiibowiki.R
import com.oscarg798.amiibowiki.core.ui.Dimensions
import com.oscarg798.amiibowiki.core.ui.Router
import com.oscarg798.amiibowiki.core.ui.ScreenConfigurator
import com.oscarg798.amiibowiki.core.ui.ThemeContainer
import com.oscarg798.amiibowiki.dashboard.DashboardViewModel
import com.oscarg798.amiibowiki.dashboard.mvi.DashboardWish
import com.oscarg798.amiibowiki.dashboard.mvi.UiEffect

@Composable
internal fun DashboardScreen(viewModel: DashboardViewModel) {
    val uiEffect by viewModel.uiEffect.collectAsState(initial = null)
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val startDestination = Router.AmiiboList.route
    var title by remember { mutableStateOf(InitialTitle) }
    val screenConfigurator = ScreenConfigurator {
        title = it
    }

    LaunchedEffect(key1 = viewModel) {
        viewModel.onWish(DashboardWish.CheckUpdatesWish)
    }

    ThemeContainer {
        Scaffold(
            topBar = { AmiiboToolbar(title = title) },
            scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
            bottomBar = {
                AppBottomNavigationBar(
                    startDestination = startDestination,
                    navController = navController,
                    items = navigationScreens()
                )
            },
        ) { innerPadding ->
            if (uiEffect is UiEffect.RequestUpdateSideEffect) {
                UpdateDialog(uiEffect!!, viewModel)
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                MainNavigationHost(
                    startDestination = startDestination,
                    snackbarHostState = snackbarHostState,
                    navController = navController,
                    screenConfigurator = screenConfigurator
                )
            }
        }
    }
}

@Composable
private fun AmiiboToolbar(title: String) {
    TopAppBar(title = {
        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(end = Dimensions.Spacing.Small))
    })
}

private fun navigationScreens() = listOf(
    NavigationScreens.AmiiboList,
    NavigationScreens.GameSearch
)

private const val InitialTitle = ""
