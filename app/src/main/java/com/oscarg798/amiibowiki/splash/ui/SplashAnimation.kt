package com.oscarg798.amiibowiki.splash.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.oscarg798.amiibowiki.R

@Composable
internal fun SplashAnimation() {
    val animationSpec = remember { LottieAnimationSpec.RawRes(R.raw.tetris_anim) }
    LottieAnimation(
        animationSpec,
        modifier = Modifier.layoutId(AnimationId)
    )
}
