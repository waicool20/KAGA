/*
 * GPLv3 License
 *
 *  Copyright (c) KAGA by waicool20
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.waicool20.kaga.kcauto

import com.waicool20.kaga.util.LoggingEventBus
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import java.time.LocalDateTime
import kotlin.concurrent.thread

object YuuBot {
    private val API_URL = "https://yuu.waicool20.com/api/user/"

    init {
        with(LoggingEventBus) {
            // Listen to the end when kca-kai is done report stats
            subscribe(Regex(".*Recoveries done:.*")) {

            }
        }
    }

    fun testApiKey(apiKey: String, onComplete: (Boolean) -> Unit) {
        thread {
            HttpClients.createDefault().use { client ->
                onComplete(client.execute(HttpGet(API_URL + apiKey)).statusLine.statusCode == 200)
            }
        }
    }
}

data class KCAutoKaiStatsDto(
        val startingTime: LocalDateTime,
        val crashes: Int,
        val sortiesDone: Int,
        val sortiesAttempted: Int,
        val expeditionsSent: Int,
        val expeditionsReceived: Int,
        val pvpsDone: Int,
        val questsDone: Int,
        val questsStarted: Int,
        val resupplies: Int,
        val repairs: Int,
        val bucketsUsed: Int,
        val shipsSwitched: Int,
        val recoveries: Int,
        val resources: Resources
) {
    constructor(tracker: KancolleAutoKaiStatsTracker, resources: Resources) : this(
            startingTime = tracker.startingTime ?: LocalDateTime.now(),
            crashes = tracker.crashes,
            sortiesDone = tracker[KancolleAutoKaiStats::sortiesDone],
            sortiesAttempted = tracker[KancolleAutoKaiStats::sortiesAttempted],
            expeditionsSent = tracker[KancolleAutoKaiStats::expeditionsSent],
            expeditionsReceived = tracker[KancolleAutoKaiStats::expeditionsReceived],
            pvpsDone = tracker[KancolleAutoKaiStats::pvpsDone],
            questsDone = tracker[KancolleAutoKaiStats::questsDone],
            questsStarted = tracker[KancolleAutoKaiStats::questsStarted],
            resupplies = tracker[KancolleAutoKaiStats::resupplies],
            repairs = tracker[KancolleAutoKaiStats::repairs],
            bucketsUsed = tracker[KancolleAutoKaiStats::bucketsUsed],
            shipsSwitched = tracker[KancolleAutoKaiStats::shipsSwitched],
            recoveries = tracker[KancolleAutoKaiStats::recoveries],
            resources = resources
    )
}
