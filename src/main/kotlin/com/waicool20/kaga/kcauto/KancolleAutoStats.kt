package com.waicool20.kaga.kcauto

import com.waicool20.kaga.util.LoggingEventBus
import com.waicool20.kaga.util.inc
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import java.time.LocalDateTime

class KancolleAutoStats {
    var startingTime = SimpleObjectProperty<LocalDateTime>(null)
    var sortiesConducted = SimpleIntegerProperty(0)
    var crashes = SimpleIntegerProperty(0)

    init {
        LoggingEventBus.subscribe(".*~(\\d+) sorties conducted.*".toRegex(), { sortiesConducted.inc() })
    }

    fun startNewSession() {
        startingTime.set(LocalDateTime.now())
        sortiesConducted.set(0)
        crashes.set(0)
    }
}
