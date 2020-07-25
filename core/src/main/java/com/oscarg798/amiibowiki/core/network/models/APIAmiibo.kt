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

package com.oscarg798.amiibowiki.core.network.models

import com.google.gson.annotations.SerializedName
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate

data class APIAmiiboReleaseDate(
    @SerializedName("au") val australia: String?,
    @SerializedName("eu") val europe: String?,
    @SerializedName("jp") val japan: String?,
    @SerializedName("na") val northAmerica: String?
)

data class APIAmiibo(
    @SerializedName("amiiboSeries")
    val amiiboSeries: String,
    @SerializedName("character")
    val character: String,
    @SerializedName("gameSeries")
    val gameSeries: String,
    @SerializedName("head")
    val head: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("release")
    val releaseDate: APIAmiiboReleaseDate?,
    @SerializedName("tail")
    val tail: String,
    @SerializedName("name")
    val name: String
) {
    fun toAmiibo() =
        Amiibo(
            amiiboSeries = amiiboSeries,
            character = character,
            gameSeries = gameSeries,
            head = head,
            image = image,
            type = type,
            releaseDate = releaseDate?.toAmiiboReleaseDate(),
            tail = tail,
            name = name
        )
}

fun APIAmiiboReleaseDate.toAmiiboReleaseDate() = AmiiboReleaseDate(australia = australia, europe = europe, northAmerica = northAmerica, japan = japan)
