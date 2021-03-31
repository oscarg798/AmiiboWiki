package com.oscarg798.amiibowiki.nfcreader.di

import android.content.Context
import android.nfc.NfcAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped

@InstallIn(ActivityComponent::class)
@Module
object NFCAdapterModule {

    @ActivityScoped
    @Provides
    fun provideNFCReader(@ApplicationContext context: Context): NfcAdapter =
        NfcAdapter.getDefaultAdapter(context)
}
