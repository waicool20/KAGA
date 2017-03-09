package com.waicool20.kaga.kcauto

import com.waicool20.kaga.util.LoggingEventBus
import java.time.LocalDateTime

class KancolleAutoStatsTracker {
    var startingTime: LocalDateTime? = null
    var crashes = 0
    val history = mutableListOf<KancolleAutoStats>()

    init {
        // Track sorties conducted
        LoggingEventBus.subscribe(".*~(\\d+) sorties conducted.*".toRegex(), {
            currentStats().sortiesConducted = it.groupValues[1].toInt()
        })

        // Track expeditions conducted
        LoggingEventBus.subscribe(".*~(\\d+) expeditions conducted.*".toRegex(), {
            currentStats().expeditionsConducted = it.groupValues[1].toInt()
        })

        // Track crashes occurred
        LoggingEventBus.subscribe(".*Kancolle Auto didn't terminate gracefully.*".toRegex(), {
            crashes++
        })
    }

    fun startNewSession() {
        history.clear()
        startingTime = LocalDateTime.now()
        crashes = 0
        trackNewChild()
    }

    fun trackNewChild() = history.add(KancolleAutoStats())

    fun sortiesConductedTotal() = history.map { it.sortiesConducted }.sum()

    fun expeditionsConductedTotal() = history.map { it.expeditionsConducted }.sum()

    private fun currentStats() = history.last()
}

data class KancolleAutoStats(
        var sortiesConducted: Int = 0,
        var expeditionsConducted: Int = 0)
