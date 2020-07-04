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

package com.oscarg798.amiibowiki.core.usecases

import com.oscarg798.amiibowiki.core.di.CoreScope
import com.oscarg798.amiibowiki.core.models.AmiiboType
import com.oscarg798.amiibowiki.core.repositories.AmiiboTypeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import okio.IOException
import javax.inject.Inject

@ExperimentalCoroutinesApi
@CoreScope
class GetAmiiboTypeUseCase @Inject constructor(
    private val getDefaultAmiiboTypeUseCase: GetDefaultAmiiboTypeUseCase,
    private val amiiboTypeRepository: AmiiboTypeRepository
) {
    fun execute(): Flow<List<AmiiboType>> {
        return amiiboTypeRepository.getTypes().filterNot { it.isEmpty() }
            .map {
                arrayListOf<AmiiboType>().apply {
                    addAll(it)
                    add(getDefaultAmiiboTypeUseCase.execute())
                }
            }
    }


}

