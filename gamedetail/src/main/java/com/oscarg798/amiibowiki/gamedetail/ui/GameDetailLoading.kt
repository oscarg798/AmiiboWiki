package com.oscarg798.amiibowiki.gamedetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.oscarg798.amiibowiki.core.ui.LoadingImage
import com.oscarg798.amiibowiki.core.ui.Shimmer

@Composable
internal fun GameDetailLoading() {

    ConstraintLayout(
        constraintSet = getGameDetailConstraints(
            hasPeigRating = true,
            hasEsrbRating = true
        ),
        modifier = Modifier.background(MaterialTheme.colors.background)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HeaderImageHeight)
                .layoutId(MainImageId)
        ) {
            LoadingImage(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
        }

        Box(
            modifier = Modifier
                .layoutId(CoverId)
                .width(CoverImageWidth)
                .height(CoverImageHeight)
        ) {
            LoadingImage(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
        }

        Shimmer(
            modifier = Modifier
                .fillMaxWidth()
                .height(25.dp)
                .layoutId(TitleId)
        )

        Box(
            modifier = Modifier
                .width(RatingWidth)
                .height(RatingHeight)
                .layoutId(PegiRatingImageId)
        ) {
            LoadingImage(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
        }

        Box(
            modifier = Modifier
                .width(RatingWidth)
                .height(RatingHeight)
                .layoutId(EsrbRatingImageId)
        ) {
            LoadingImage(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
        }

        Shimmer(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .layoutId(DescriptionId)
        )
    }
}
