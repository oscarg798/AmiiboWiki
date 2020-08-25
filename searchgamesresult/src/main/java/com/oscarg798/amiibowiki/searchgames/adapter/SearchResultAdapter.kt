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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oscarg798.amiibowiki.searchgames.databinding.GameRelatedItemBinding
import com.oscarg798.amiibowiki.searchgames.models.ViewGameSearchResult

class SearchResultAdapter(
    private val gameResultRelatedClickListener: SearchResultClickListener
) : RecyclerView.Adapter<SearchResultViewHolder>() {

    private val searchResults = ArrayList<ViewGameSearchResult>()

    private fun getItem(position: Int) = searchResults[position]

    override fun getItemCount(): Int = searchResults.size

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) =
        holder.bind(getItem(position), gameResultRelatedClickListener)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder =
        SearchResultViewHolder(
            GameRelatedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    /**
     * We did not use [DiffUtil] because most of the time
     * the whole list will be change just to update the images, then as the whole list
     * will change [DiffUtil] end up affecting the scroll and moving it to the top so we preffer
     * [notifyItemRangeChanged].
     */
    fun updateProducts(searchResults: List<ViewGameSearchResult>) {
        this.searchResults.clear()
        this.searchResults.addAll(searchResults)
        this.notifyItemRangeChanged(SEARCH_RESULTS_FIRST_POSITION, searchResults.size)
    }
}

private const val SEARCH_RESULTS_FIRST_POSITION = 0
