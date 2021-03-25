package com.oscarg798.amiibowiki.core.logger

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.oscarg798.amiibowiki.logger.events.CrashEvent
import com.oscarg798.lomeno.event.LogEvent
import com.oscarg798.lomeno.logger.Logger
import javax.inject.Inject

class CrashLogger @Inject constructor(private val firebaseCrashlytics: FirebaseCrashlytics) : Logger {

    override fun flush() {
        // NO_OP
    }

    override fun identify(id: String) {
        firebaseCrashlytics.setUserId(id)
    }

    override fun log(logEvent: LogEvent) {
        require(logEvent is CrashEvent)
        firebaseCrashlytics.recordException(logEvent.exception)
    }
}
