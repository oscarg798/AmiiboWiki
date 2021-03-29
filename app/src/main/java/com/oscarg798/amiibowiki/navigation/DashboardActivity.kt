/*
 * Copyright 2020 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.oscarg798.amiibowiki.navigation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.oscarg798.amiibowiki.R
import com.oscarg798.amiibowiki.amiibodetail.ui.AmiiboDetailScreen
import com.oscarg798.amiibowiki.amiibolist.ui.AmiiboListScreen
import com.oscarg798.amiibowiki.core.extensions.setViewTreeObserver
import com.oscarg798.amiibowiki.core.extensions.verifyNightMode
import com.oscarg798.amiibowiki.core.ui.Screen
import com.oscarg798.amiibowiki.core.ui.ThemeContainer
import com.oscarg798.amiibowiki.navigation.mvi.CheckUpdatesWish
import com.oscarg798.amiibowiki.navigation.ui.DashboardScreen
import com.oscarg798.amiibowiki.updatechecker.requestUpdate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalFoundationApi::class)
@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyNightMode()
        setViewTreeObserver()

        setContent {
            DashboardScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onWish(CheckUpdatesWish)
    }


    private fun observeViewModelEffect() {
        lifecycleScope.launchWhenResumed {
            viewModel.uiEffect.collect { effect ->
                requestUpdate(effect.type)
            }
        }
    }
}









