package com.oscarg798.amiibowiki.nfcreader.ui

import android.nfc.Tag
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.oscarg798.amiibowiki.core.ui.ScreenConfigurator
import com.oscarg798.amiibowiki.core.ui.ThemeContainer
import kotlinx.coroutines.CoroutineScope

@Composable
internal fun NFCReaderScreen(
    coroutineScope: CoroutineScope,
    tag: Tag,
    onErrorDismissed: () -> Unit
) {

    val screenConfigurator = ScreenConfigurator {}
    val navController = rememberNavController()

    ThemeContainer {
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)) {
            NFCReaderNavHost(
                navController = navController,
                tag = tag,
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
                onErrorDismissed = onErrorDismissed,
                screenConfigurator = screenConfigurator
            )
        }
    }
}
