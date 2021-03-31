package com.oscarg798.amiibowiki.gamedetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.oscarg798.amiibowiki.core.models.AgeRatingCategory
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.ui.AmiiboWikiBackNavigationIcon
import com.oscarg798.amiibowiki.core.ui.Dimensions
import com.oscarg798.amiibowiki.core.ui.ScreenConfigurator
import com.oscarg798.amiibowiki.gamedetail.models.ExpandableImageParam

@Composable
fun GameDetail(
    game: Game,
    screenConfigurator: ScreenConfigurator,
    onBackButtonPressed: () -> Unit,
    onTrailerClicked: () -> Unit,
    onImageClick: (ExpandableImageParam) -> Unit
) {
    val hasPegiRating =
        game.ageRating?.firstOrNull { it.category is AgeRatingCategory.PEGI } != null
    val hasEsrbRating =
        game.ageRating?.firstOrNull { it.category is AgeRatingCategory.ESRB } != null

    SideEffect {
        screenConfigurator.titleUpdater(game.name)
    }

    ConstraintLayout(
        constraintSet = getGameDetailConstraints(hasPegiRating, hasEsrbRating),
        Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colors.background)
    ) {
        GameHeaderImage(game)
        AmiiboWikiBackNavigationIcon(Modifier.layoutId(BackButtonId)) { onBackButtonPressed() }
        GameCover(game, onImageClick)
        GameTitle(game)

        if (game.videosId?.isEmpty() == false) {
            TrailerButton(onTrailerClicked)
        }

        if (hasPegiRating) {
            PegiRating(game)
        }

        if (hasEsrbRating) {
            EsrbRating(game)
        }

        if (game.summary != null) {
            GameDescription(description = game.summary!!)
        }

        if (game.screenshots?.isEmpty() == false) {
            GameScreenshots(game, onImageClick)
        }

        if (game.artworks?.isEmpty() == false) {
            GameArtworks(game, onImageClick)
        }
    }
}

internal fun getGameDetailConstraints(hasPeigRating: Boolean, hasEsrbRating: Boolean) =
    ConstraintSet {
        val mainImageId = createRefFor(MainImageId)
        val backButtonId = createRefFor(BackButtonId)
        val coverId = createRefFor(CoverId)
        val pegiRatingImageId = createRefFor(PegiRatingImageId)
        val esrbRatingImageId = createRefFor(EsrbRatingImageId)
        val descriptionId = createRefFor(DescriptionId)
        val screenshotsId = createRefFor(ScreenshotsId)
        val artworksId = createRefFor(ArtworksId)
        val titleId = createRefFor(TitleId)
        val trailerButtonId = createRefFor(TrailerButtonId)

        constrain(mainImageId) {
            top.linkTo(parent.top)
            linkTo(start = parent.start, end = parent.end)
        }

        constrain(trailerButtonId) {
            linkTo(top = mainImageId.top, bottom = mainImageId.bottom)
            linkTo(start = parent.start, end = parent.end)
        }

        constrain(backButtonId) {
            top.linkTo(parent.top, margin = Dimensions.Spacing.Medium)
            start.linkTo(parent.start, margin = Dimensions.Spacing.Medium)
        }

        constrain(coverId) {
            top.linkTo(mainImageId.bottom)
            bottom.linkTo(mainImageId.bottom)
            start.linkTo(parent.start, Dimensions.Spacing.Medium)
        }

        constrain(titleId) {
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
            top.linkTo(coverId.bottom, margin = Dimensions.Spacing.Small)
            linkTo(
                start = coverId.start,
                end = parent.end,
                bias = AlignToStart,
                endMargin = Dimensions.Spacing.Medium
            )
        }

        constrain(pegiRatingImageId) {
            top.linkTo(titleId.bottom, margin = Dimensions.Spacing.Small)
            start.linkTo(titleId.start)
        }

        constrain(esrbRatingImageId) {
            top.linkTo(pegiRatingImageId.top)
            start.linkTo(
                if (hasPeigRating) {
                    pegiRatingImageId.end
                } else {
                    parent.start
                },
                margin = Dimensions.Spacing.Small
            )
        }

        constrain(descriptionId) {
            width = Dimension.fillToConstraints
            top.linkTo(
                when {
                    hasPeigRating -> pegiRatingImageId.bottom
                    hasEsrbRating -> esrbRatingImageId.bottom
                    else -> titleId.bottom
                },
                margin = Dimensions.Spacing.Medium
            )
            linkTo(
                start = titleId.start,
                end = titleId.end,
                bias = AlignToStart
            )
        }

        constrain(screenshotsId) {
            width = Dimension.fillToConstraints
            top.linkTo(descriptionId.bottom)
            linkTo(
                start = titleId.start,
                end = titleId.end
            )
        }

        val barrier = createBottomBarrier(titleId, pegiRatingImageId, screenshotsId)

        constrain(artworksId) {
            width = Dimension.fillToConstraints
            top.linkTo(barrier, margin = Dimensions.Spacing.Small)
            linkTo(
                start = titleId.start,
                end = titleId.end,
            )
        }
    }

internal const val BackButtonId = "BackButtonId"

private const val AlignToStart = 0f
