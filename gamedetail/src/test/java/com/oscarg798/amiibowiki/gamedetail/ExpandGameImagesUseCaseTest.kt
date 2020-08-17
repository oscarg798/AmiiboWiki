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

package com.oscarg798.amiibowiki.gamedetail

import com.oscarg798.amiibowiki.gamedetail.models.ExpandableImageParam
import com.oscarg798.amiibowiki.gamedetail.models.ExpandableImageType
import com.oscarg798.amiibowiki.gamedetail.usecases.ExpandGameImagesUseCase
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class ExpandGameImagesUseCaseTest {

    private lateinit var usecase: ExpandGameImagesUseCase

    @Before
    fun setup() {
        usecase = ExpandGameImagesUseCase()
    }

    @Test
    fun `given a expand image params when type is cover then it should replace the cover size for the desired size`() {
        val params = ExpandableImageParam(MOCK_COVER_IMAGE_URL, ExpandableImageType.Cover)

        usecase.execute(listOf(params)) shouldBeEqualTo listOf(DESIRED_SIZE)
    }

    @Test
    fun `given a expand image params when type is screenshot then it should replace the cover size for the desired size`() {
        val params = ExpandableImageParam(MOCK_SCREENSHOT_IMAGE_URL, ExpandableImageType.Screenshot)

        usecase.execute(listOf(params)) shouldBeEqualTo listOf(DESIRED_SIZE)
    }

    @Test
    fun `given a expand image params when type is artwork then it should replace the cover size for the desired size`() {
        val params = ExpandableImageParam(MOCK_ARTWORK_IMAGE_URL, ExpandableImageType.Artwork)

        usecase.execute(listOf(params)) shouldBeEqualTo listOf(DESIRED_SIZE)
    }
}

private const val DESIRED_SIZE = "t_1080p"
private const val MOCK_SCREENSHOT_IMAGE_URL = "t_screenshot_med"
private const val MOCK_COVER_IMAGE_URL = "t_cover_big"
private const val MOCK_ARTWORK_IMAGE_URL = "t_thumb"
