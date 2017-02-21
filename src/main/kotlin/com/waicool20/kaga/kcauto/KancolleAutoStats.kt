package com.waicool20.kaga.kcauto

import com.waicool20.kaga.util.LoggingEventBus
import java.time.LocalDateTime

class KancolleAutoStats {
    var startingTime: LocalDateTime? = null
    var sortiesConducted = 0
    var crashes = 0

    init {
        LoggingEventBus.subscribe(".*(\\d+?) sorties conducted.*".toRegex(), { match ->
            sortiesConducted = match.groupValues[1].toInt()
        })
    }

    fun startNewSession() {
        startingTime = LocalDateTime.now()
        sortiesConducted = 0
        crashes = 0
    }
}
