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

sealed class GameCategory(val categoryId: Int) {
    object MainGame : GameCategory(MAIN_GAME)
    object DLCAddOn : GameCategory(DLC_ADD_ON)
    object Expansion : GameCategory(EXPAINSION)
    object Bundle : GameCategory(BUNDLE)
    object StandAloneExpainsion : GameCategory(STANDALONG_EXPANSION)
    object Mod : GameCategory(MOD)
    object Episode : GameCategory(EPISODE)
    object Season : GameCategory(SEASON)
    object Remake : GameCategory(REMAKE)
    object Remaster : GameCategory(REMASTER)
    object ExpandedGame : GameCategory(EXPANDED_GAME)
    object Port : GameCategory(PORT)
    object Fork : GameCategory(FORK)

    companion object {

        fun createFromCategoryId(id: Id) = when (id) {
            MAIN_GAME -> MainGame
            DLC_ADD_ON -> DLCAddOn
            EXPAINSION -> Expansion
            BUNDLE -> Bundle
            STANDALONG_EXPANSION -> StandAloneExpainsion
            MOD -> Mod
            EPISODE -> Episode
            SEASON -> Season
            REMAKE -> Remake
            REMASTER -> Remaster
            EXPANDED_GAME -> ExpandedGame
            PORT -> Port
            FORK -> Fork
            else -> throw IllegalArgumentException("There is no game category associated with id: $id")
        }
    }
}

const val MAIN_GAME = 0
const val DLC_ADD_ON = 1
const val EXPAINSION = 2
const val BUNDLE = 3
const val STANDALONG_EXPANSION = 4
const val MOD = 5
const val EPISODE = 6
const val SEASON = 7
const val REMAKE = 8
const val REMASTER = 9
const val EXPANDED_GAME = 10
const val PORT = 11
const val FORK = 12
