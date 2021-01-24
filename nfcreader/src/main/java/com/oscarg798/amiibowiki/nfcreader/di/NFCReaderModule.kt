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

package com.oscarg798.amiibowiki.nfcreader.di

import android.content.Context
import android.nfc.NfcAdapter
import com.oscarg798.amiibowiki.core.mvi.Reducer

import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderResult
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReaderViewState
import com.oscarg798.amiibowiki.nfcreader.mvi.NFCReducer
import com.oscarg798.amiibowiki.nfcreader.repository.NFCReaderRepository
import com.oscarg798.amiibowiki.nfcreader.repository.NFCReaderRepositoryImpl
import com.oscarg798.amiibowiki.nfcreader.utils.AmiiboArrayCloner
import com.oscarg798.amiibowiki.nfcreader.utils.AmiiboByteWrapper
import com.oscarg798.amiibowiki.nfcreader.utils.ArrayCloner
import com.oscarg798.amiibowiki.nfcreader.utils.ByteWrapper
import com.oscarg798.amiibowiki.nfcreader.utils.MifareTagTech
import com.oscarg798.amiibowiki.nfcreader.utils.TagTech
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
object NFCReaderModule {

    @NFCReaderScope
    @Provides
    fun provideNFCReader(@ApplicationContext context: Context): NfcAdapter = NfcAdapter.getDefaultAdapter(context)

    @NFCReaderScope
    @Provides
    fun provideMifareTagTech(mifareTagTech: MifareTagTech): TagTech = mifareTagTech

    @NFCReaderScope
    @Provides
    fun provideArrayCloner(amiiboArrayCloner: AmiiboArrayCloner): ArrayCloner = amiiboArrayCloner

    @NFCReaderScope
    @Provides
    fun provideByteWrapper(amiiboByteWrapper: AmiiboByteWrapper): ByteWrapper = amiiboByteWrapper

    @NFCReaderScope
    @Provides
    fun provideNFCReaderRepository(nfcReaderRepositoryImpl: NFCReaderRepositoryImpl): NFCReaderRepository = nfcReaderRepositoryImpl

    @NFCReaderScope
    @Provides
    fun provideNFCReaderReducer(nfcReaderReducer: NFCReducer): Reducer<@JvmSuppressWildcards NFCReaderResult, @JvmSuppressWildcards NFCReaderViewState> = nfcReaderReducer
}
