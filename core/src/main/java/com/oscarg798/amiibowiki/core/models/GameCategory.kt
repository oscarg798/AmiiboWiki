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

package com.oscarg798.amiibowiki.core.models

sealed class GameCategory {
    object MainGame : GameCategory()
    object DLCAddOn : GameCategory()
    object Expansion : GameCategory()
    object Bundle : GameCategory()
    object StandAloneExpainsion : GameCategory()
    object Mod : GameCategory()
    object Episode : GameCategory()

    companion object {

        fun createFromCategoryId(id: Id) = when (id) {
            MAIN_GAME -> MainGame
            DLC_ADD_ON -> DLCAddOn
            EXPAINSION -> Expansion
            BUNDLE -> Bundle
            STANDALONG_EXPANSION -> StandAloneExpainsion
            MOD -> Mod
            EPISODE -> Episode
            else -> throw IllegalArgumentException("Thre is no game category associated with id: $id")
        }
    }
}

private const val MAIN_GAME = 0
private const val DLC_ADD_ON = 1
private const val EXPAINSION = 2
private const val BUNDLE = 3
private const val STANDALONG_EXPANSION = 4
private const val MOD = 5
private const val EPISODE = 6
