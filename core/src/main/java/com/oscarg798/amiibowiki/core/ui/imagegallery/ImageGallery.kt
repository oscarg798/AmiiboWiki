package com.oscarg798.amiibowiki.core.ui.imagegallery

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.oscarg798.amiibowiki.core.ui.ImageFromUrl

/**
 * We do not want to attach the Dialog to the other modules so we choose
 * to make it part of the nav controller as a deeplink,
 * thats why on dismiss we pop up the backstack
 */
@Composable
internal fun ImageGallery(image: String, navController: NavController) {
    var shown by remember { mutableStateOf(true) }

    if (shown) {
        Dialog(
            onDismissRequest = {
                shown = false
                navController.popBackStack()
            }
        ) {
            ImageFromUrl(
                url = image,
                contentScale = ContentScale.Fit,
                imageModifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}
