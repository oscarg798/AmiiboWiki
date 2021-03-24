package com.oscarg798.amiibowiki.gamedetail.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import com.oscarg798.amiibowiki.core.models.AgeRatingCategory
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.models.Rating
import com.oscarg798.amiibowiki.gamedetail.R

@Composable
internal fun PegiRating(game: Game) {
    val pegiRating = getPeigRating(game)
    Image(
        painter = painterResource(
            id = when (pegiRating.rating) {
                is Rating.PEGI3 -> R.drawable.ic_pegi_3
                is Rating.PEGI7 -> R.drawable.ic_pegi_7
                is Rating.PEGI12 -> R.drawable.ic_pegi_12
                is Rating.PEGI16 -> R.drawable.ic_pegi_16
                else -> R.drawable.ic_pegi_18
            }
        ),
        contentDescription = "",
        Modifier
            .width(RatingWidth)
            .height(RatingHeight)
            .layoutId(PegiRatingImageId)
    )
}

private fun getPeigRating(game: Game) =
    game.ageRating!!.first { it.category is AgeRatingCategory.PEGI }

internal const val PegiRatingImageId = "PegiRatingImageId"
