package com.oscarg798.amiibowiki.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.airbnb.lottie.compose.rememberLottieAnimationState
import com.oscarg798.amiibowiki.core.R

@Composable
fun LoadingAnimation(modifier: Modifier, playing: Boolean = true) {
    val animationSpec = remember { LottieAnimationSpec.RawRes(R.raw.tetris_anim) }
    LottieAnimation(
        spec = animationSpec,
        animationState = rememberLottieAnimationState(
            repeatCount = Int.MAX_VALUE,
            autoPlay = playing
        ),
        modifier = modifier
    )
}
