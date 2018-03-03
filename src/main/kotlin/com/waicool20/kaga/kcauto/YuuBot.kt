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

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.LoggingEventBus
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import kotlin.concurrent.thread

object YuuBot {
    private val API_URL = "https://yuu.waicool20.com/api/user/"
    private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
    private val logger = LoggerFactory.getLogger(javaClass)

    enum class ApiKeyStatus {
        VALID, INVALID, UNKNOWN
    }

    init {
        with(LoggingEventBus) {
            // Listen to the end when kca-kai is done report stats
            subscribe(Regex(".*Recoveries done:.*")) { reportStats() }
        }
    }

    fun reportStats() {
        if (Kaga.CONFIG.apiKey.isEmpty()) return
        logger.info("Reporting stats to YuuBot!")
        thread {
            try {
                val response = HttpClients.createDefault().use { client ->
                    val stats = KCAutoKaiStatsDto(KancolleAutoKaiStatsTracker, Resources())
                    HttpPost(API_URL + Kaga.CONFIG.apiKey + "/stats").apply {
                        entity = StringEntity(mapper.writeValueAsString(stats), ContentType.APPLICATION_JSON)
                    }.let { client.execute(it) }.statusLine.statusCode
                }
                when (response) {
                    200 -> logger.debug("Stats reported to YuuBot! Response was: $response")
                    else -> logger.warn("Failed to report stats to YuuBot, response was: $response")
                }
            } catch (e: Exception) {
                logger.warn("Could not report stats, maybe your internet is down?")
            }
        }
    }

    fun reportCrash(dto: CrashInfoDto) {
        if (Kaga.CONFIG.apiKey.isEmpty()) return
        logger.info("Reporting crash to YuuBot")
        thread {
            try {
                val response = HttpClients.createDefault().use { client ->
                    HttpPost(API_URL + Kaga.CONFIG.apiKey + "/crashed").apply {
                        entity = StringEntity(mapper.writeValueAsString(dto), ContentType.APPLICATION_JSON)
                    }.let { client.execute(it) }.statusLine.statusCode
                }
                when (response) {
                    200 -> logger.debug("Crash reported to YuuBot! Response was: $response")
                    else -> logger.warn("Failed to report crash to YuuBot, response was: $response")
                }
            } catch (e: Exception) {
                logger.warn("Could not report crash, maybe your internet is down?")
            }
        }
    }

    fun testApiKey(apiKey: String, onComplete: (ApiKeyStatus) -> Unit) {
        if (apiKey.isEmpty()) {
            logger.info("API key is empty, YuuBot reporting is disabled.")
            onComplete(ApiKeyStatus.INVALID)
            return
        }
        logger.info("Testing API key: $apiKey")
        thread {
            HttpClients.createDefault().use { client ->
                try {
                    val response = client.execute(HttpGet(API_URL + apiKey)).statusLine.statusCode
                    when (response) {
                        200 -> {
                            onComplete(ApiKeyStatus.VALID)
                            logger.info("API key was found valid, response was: $response")
                        }
                        else -> {
                            onComplete(ApiKeyStatus.INVALID)
                            logger.warn("API key was found invalid, response was: $response")
                        }
                    }
                } catch (e: Exception) {
                    logger.warn("Could not check if API key is valid, maybe your internet is down?")
                    onComplete(ApiKeyStatus.UNKNOWN)
                }
            }
        }
    }
}

data class CrashInfoDto(val log: String)

data class KCAutoKaiStatsDto(
        val isRunning: Boolean,
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
            isRunning = Kaga.KCAUTO_KAI.isRunning(),
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