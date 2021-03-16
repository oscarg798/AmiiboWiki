package com.oscarg798.amiibowiki.gamedetail.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.oscarg798.amiibowiki.core.models.AgeRatingCategory
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.models.Rating
import com.oscarg798.amiibowiki.gamedetail.R

@Composable
internal fun EsrbRating(game: Game) {
    val esrbRating = getEsrbRating(game)
    Image(
        painter = painterResource(
            id = when (esrbRating.rating) {
                is Rating.ESRBEC -> R.drawable.ic_early_childhood
                is Rating.ESRB10 -> R.drawable.ic_esrb_everyone_10
                is Rating.ESRBTeen -> R.drawable.ic_esrb_teen
                is Rating.ESRBMature -> R.drawable.ic_esrb_mature
                is Rating.ESRBAdultsOnly -> R.drawable.ic_esrb_ao
                is Rating.ESRBEveryone -> R.drawable.ic_esrb_everyone_10
                else -> R.drawable.ic_esrb_rp
            }
        ),
        contentDescription = "",
        modifier = Modifier
            .width(RatingWidth)
            .height(RatingHeight)
            .layoutId(EsrbRatingImageId)
    )
}

private fun getEsrbRating(game: Game) =
    game.ageRating!!.first { it.category is AgeRatingCategory.ESRB }

internal val RatingWidth = 40.dp
internal val RatingHeight = 50.dp
internal const val EsrbRatingImageId = "EsrbRatingImageId"
