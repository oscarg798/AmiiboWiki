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

package com.oscarg798.amiibowiki.searchgames.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.oscarg798.amiibowiki.core.extensions.setImage
import com.oscarg798.amiibowiki.searchgames.R
import com.oscarg798.amiibowiki.searchgames.databinding.GameRelatedItemBinding
import com.oscarg798.amiibowiki.searchgames.models.ViewGameSearchResult
import kotlinx.android.extensions.LayoutContainer

class SearchResultViewHolder(private val gameRelatedItemBinding: GameRelatedItemBinding) :
    RecyclerView.ViewHolder(gameRelatedItemBinding.root), LayoutContainer {

    override val containerView: View?
        get() = gameRelatedItemBinding.root

    fun bind(
        viewGameSearchResult: ViewGameSearchResult,
        gameRelatedClickListener: SearchResultClickListener
    ) {
        with(gameRelatedItemBinding) {
            tvGameName.text = viewGameSearchResult.gameName
            setAlternativeName(viewGameSearchResult)
            updateGameImage(viewGameSearchResult)

            root.setOnClickListener {
                gameRelatedClickListener.onResultClicked(viewGameSearchResult)
            }
        }
    }

    private fun GameRelatedItemBinding.setAlternativeName(
        viewGameSearchResult: ViewGameSearchResult
    ) {
        if (viewGameSearchResult.alternativeName.isNullOrEmpty()) {
            tvGameAlternativeName.visibility = View.INVISIBLE
        } else {
            tvGameAlternativeName.visibility = View.VISIBLE
            tvGameAlternativeName.text = viewGameSearchResult.alternativeName
        }
    }

    private fun GameRelatedItemBinding.updateGameImage(viewGameSearchResult: ViewGameSearchResult) {
        if (viewGameSearchResult.cover == null) {
            ivGameCoverShimmer.startShimmer()
            ivGameCoverShimmer.visibility = View.VISIBLE
            ivGameSearchCover.setImageDrawable(
                ContextCompat.getDrawable(
                    ivGameSearchCover.context,
                    R.drawable.ic_placeholder_gray
                )
            )
            ivGameSearchCover.setColorFilter(ivGameSearchCover.context.getColor(R.color.silver_chalice))
        } else {
            ivGameCoverShimmer.visibility = View.GONE
            ivGameCoverShimmer.stopShimmer()
            ivGameSearchCover.colorFilter = null
            ivGameSearchCover.setImage(viewGameSearchResult.cover)
        }
    }
}
