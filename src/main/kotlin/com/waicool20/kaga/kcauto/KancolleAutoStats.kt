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

        // Track pvp conducted
        LoggingEventBus.subscribe(".*~(\\d+) PvPs conducted.*".toRegex(), {
            currentStats().pvpsConducted = it.groupValues[1].toInt()
        })

        // Track buckets used
        LoggingEventBus.subscribe(".*[uU]sing bucket.*".toRegex(), {
            currentStats().bucketsUsed++
        })

        // Track submarines switched
        LoggingEventBus.subscribe(".*Swapping submarines!.*".toRegex(), {
            currentStats().submarinesSwitched++
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

    fun pvpsConductedTotal() = history.map { it.pvpsConducted }.sum()

    fun bucketsUsedTotal() = history.map { it.bucketsUsed }.sum()

    fun submarinesSwitchedTotal() = history.map { it.submarinesSwitched }.sum()

    private fun currentStats() = history.last()
}

data class KancolleAutoStats(
        var sortiesConducted: Int = 0,
        var expeditionsConducted: Int = 0,
        var pvpsConducted: Int = 0,
        var bucketsUsed: Int = 0,
        var submarinesSwitched: Int = 0
)
