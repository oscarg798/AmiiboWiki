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

package com.oscarg798.amiibowiki.searchgamesresults.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.oscarg798.amiibowiki.searchgamesresults.databinding.GameRelatedItemBinding
import com.oscarg798.amiibowiki.searchgamesresults.models.ViewGameSearchResult

internal class SearchResultAdapter(
    private val gameResultRelatedClickListener: SearchResultClickListener
) : ListAdapter<ViewGameSearchResult, SearchResultViewHolder>(diff) {

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) =
        holder.bind(getItem(position), gameResultRelatedClickListener)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder =
        SearchResultViewHolder(
            GameRelatedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    companion object {

        val diff = object : DiffUtil.ItemCallback<ViewGameSearchResult>() {
            override fun areItemsTheSame(
                oldItem: ViewGameSearchResult,
                newItem: ViewGameSearchResult
            ): Boolean = oldItem.gameId == newItem.gameId

            override fun areContentsTheSame(
                oldItem: ViewGameSearchResult,
                newItem: ViewGameSearchResult
            ): Boolean = oldItem == newItem
        }
    }
}

private const val SEARCH_RESULTS_FIRST_POSITION = 0
