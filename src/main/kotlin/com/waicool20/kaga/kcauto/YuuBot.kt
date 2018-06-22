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
import com.waicool20.waicoolutils.logging.LoggingEventBus
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.slf4j.LoggerFactory
import java.time.Instant
import kotlin.concurrent.thread

object YuuBot {
    private val endpoint
        get() = if (useLocalServer) {
            "http://localhost:8080/api/user/"
        } else {
            "https://yuu.waicool20.com/api/user/"
        }
    private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
    private val logger = LoggerFactory.getLogger(javaClass)
    var useLocalServer = false

    private var resources = Resources()

    enum class ApiKeyStatus {
        VALID, INVALID, UNKNOWN
    }

    init {
        with(LoggingEventBus) {
            // Listen to the end when kca is done report stats
            subscribe(Regex(".*Recoveries done:.*")) { reportStats() }
        }
    }

    fun reportStats() {
        if (Kaga.CONFIG.apiKey.isEmpty()) return
        httpClient { client ->
            readResources()
            logger.info("Reporting stats to YuuBot!")
            try {
                val stats = KCAutoStatsDto(KCAutoStatsTracker, resources)
                val response = HttpPost(endpoint + Kaga.CONFIG.apiKey + "/stats").apply {
                    entity = StringEntity(mapper.writeValueAsString(stats), ContentType.APPLICATION_JSON)
                }.let { client.execute(it) }.statusLine.statusCode
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
        httpClient { client ->
            try {
                val response = HttpPost(endpoint + Kaga.CONFIG.apiKey + "/crashed").apply {
                    entity = StringEntity(mapper.writeValueAsString(dto), ContentType.APPLICATION_JSON)
                }.let { client.execute(it) }.statusLine.statusCode
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
        httpClient { client ->
            try {
                val response = client.execute(HttpGet(endpoint + apiKey)).statusLine.statusCode
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

    private fun httpClient(action: (CloseableHttpClient) -> Unit) = thread { HttpClients.createDefault().use(action) }

    private fun readResources() {
        val rsc = Resources.readResources()
        resources = Resources(
                rsc.fuel.takeIf { it >= 0 } ?: resources.fuel,
                rsc.ammo.takeIf { it >= 0 } ?: resources.ammo,
                rsc.steel.takeIf { it >= 0 } ?: resources.steel,
                rsc.bauxite.takeIf { it >= 0 } ?: resources.bauxite,
                rsc.buckets.takeIf { it >= 0 } ?: resources.buckets,
                rsc.devmats.takeIf { it >= 0 } ?: resources.devmats
        )
    }
}

data class CrashInfoDto(val log: String)

data class KCAutoStatsDto(
        val profileName: String,
        val isRunning: Boolean,
        val startingTime: Instant,
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
    constructor(tracker: KCAutoStatsTracker, resources: Resources) : this(
            profileName = Kaga.PROFILE.name,
            isRunning = Kaga.KCAUTO.isRunning(),
            startingTime = tracker.startingTime ?: Instant.now(),
            crashes = tracker.crashes,
            sortiesDone = tracker[KCAutoStats::sortiesDone],
            sortiesAttempted = tracker[KCAutoStats::sortiesAttempted],
            expeditionsSent = tracker[KCAutoStats::expeditionsSent],
            expeditionsReceived = tracker[KCAutoStats::expeditionsReceived],
            pvpsDone = tracker[KCAutoStats::pvpsDone],
            questsDone = tracker[KCAutoStats::questsDone],
            questsStarted = tracker[KCAutoStats::questsStarted],
            resupplies = tracker[KCAutoStats::resupplies],
            repairs = tracker[KCAutoStats::repairs],
            bucketsUsed = tracker[KCAutoStats::bucketsUsed],
            shipsSwitched = tracker[KCAutoStats::shipsSwitched],
            recoveries = tracker[KCAutoStats::recoveries],
            resources = resources
    )
}
