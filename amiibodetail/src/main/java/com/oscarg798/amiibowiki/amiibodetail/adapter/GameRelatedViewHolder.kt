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

package com.oscarg798.amiibowiki.amiibodetail.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.oscarg798.amiibowiki.amiibodetail.databinding.GameRelatedItemBinding
import com.oscarg798.amiibowiki.amiibodetail.models.ViewGameSearchResult
import com.oscarg798.amiibowiki.core.setImage
import kotlinx.android.extensions.LayoutContainer

class GameRelatedViewHolder(private val gameRelatedItemBinding: GameRelatedItemBinding) :
    RecyclerView.ViewHolder(gameRelatedItemBinding.root), LayoutContainer {

    override val containerView: View?
        get() = gameRelatedItemBinding.root

    fun bind(
        viewGameSearchResult: ViewGameSearchResult,
        gameRelatedClickListener: GameRelatedClickListener
    ) {
        gameRelatedItemBinding.root.setOnClickListener {
            gameRelatedClickListener.onGameRelatedClick(viewGameSearchResult)
        }

        gameRelatedItemBinding.tvGameName.text = viewGameSearchResult.gameName

        if (viewGameSearchResult.alternativeName.isNullOrEmpty()) {
            gameRelatedItemBinding.tvGameAlternativeName.visibility = View.INVISIBLE
        } else {
            gameRelatedItemBinding.tvGameAlternativeName.visibility = View.VISIBLE
            gameRelatedItemBinding.tvGameAlternativeName.text = viewGameSearchResult.alternativeName
        }

        viewGameSearchResult.cover?.let {
            gameRelatedItemBinding.ivGameSearchCover.setImage(it)
        }
    }
}
