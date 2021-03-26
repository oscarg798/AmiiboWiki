package com.oscarg798.amiibowiki.nfcreader.logger

import com.oscarg798.amiibowiki.logger.annotations.AppCrashed
import com.oscarg798.amiibowiki.logger.annotations.LogEventProperties
import com.oscarg798.amiibowiki.logger.annotations.LoggerDecorator
import java.lang.Exception

@LoggerDecorator
interface NFCReaderLogger {

    @AppCrashed
    fun logException(@LogEventProperties exception: Exception)
}
