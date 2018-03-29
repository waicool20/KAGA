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

import com.waicool20.kaga.Kaga
import com.waicool20.kaga.util.binarizeImage
import com.waicool20.kaga.util.scale
import org.sikuli.script.Region
import org.sikuli.script.Screen
import org.sikuli.script.TextRecognizer
import org.slf4j.LoggerFactory

data class Resources(
        var fuel: Int = 0,
        var ammo: Int = 0,
        var steel: Int = 0,
        var bauxite: Int = 0,
        var buckets: Int = 0,
        var devmats: Int = 0
) {
    companion object {
        private val logger = LoggerFactory.getLogger(javaClass)
        fun readResources(): Resources {
            if (Kaga.SIKULI_WORKING) {
                Screen().exists("fuel.png").apply {
                    val fuelCountRegion = Region(x + 25, y, 44, 17)
                    val ammoCountRegion = Region(x + 25, y + 19, 44, 17)
                    val steelCountRegion = Region(x + 95, y, 44, 17)
                    val bauxCountRegion = Region(x + 95, y + 19, 44, 17)
                    val bucketRegion = Region(x + 34, y - 21, 36, 14)
                    val devmatRegion = Region(x + 101, y - 21, 36, 14)
                    logger.info("Reading Resources")
                    val fuel = fuelCountRegion.readNumber()
                    val ammo = ammoCountRegion.readNumber()
                    val steel = steelCountRegion.readNumber()
                    val bauxite = bauxCountRegion.readNumber()
                    val buckets = bucketRegion.readNumber()
                    val devmats = devmatRegion.readNumber()
                    logger.info("Fuel: $fuel | Ammo: $ammo | Steel: $steel | Bauxite: $bauxite | Buckets: $buckets | DevMats: $devmats")
                    return Resources(fuel, ammo, steel, bauxite, buckets, devmats)
                }
            }
            return Resources(-1)
        }

        private val numberReplacements = mapOf(
                "cCDGoOQ@" to "0", "iIl\\[\\]|!" to "1",
                "zZ" to "2", "E" to "3",
                "A" to "4", "sS" to "5",
                "B:" to "8", " -" to ""
        )

        private fun Region.readNumber(scaleFactor: Double = 3.0, threshold: Double = 0.4): Int {
            val image = screen.capture(this).image
                    .scale(scaleFactor).binarizeImage(threshold)
            var text = TextRecognizer.getInstance().recognizeWord(image)
            numberReplacements.forEach { r, num ->
                text = text.replace(Regex("[$r]"), num)
            }
            return text.toIntOrNull() ?: run {
                logger.error("Could not read number from text: $text")
                0
            }
        }
    }
}
