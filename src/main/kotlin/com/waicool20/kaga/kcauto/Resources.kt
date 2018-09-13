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

import com.waicool20.waicoolutils.SikuliXLoader
import com.waicool20.waicoolutils.binarizeImage
import com.waicool20.waicoolutils.scale
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
        private val logger = LoggerFactory.getLogger(Resources::class.java)
        fun readResources(): Resources {
            if (SikuliXLoader.SIKULI_WORKING) {
                var fuel = -1
                var ammo = -1
                var steel = -1
                var bauxite = -1
                var buckets = -1
                var devmats = -1
                logger.info("Reading Resources")
                Screen().exists("fuel.png")?.apply {
                    fuel = Region(x + 24, y - 2, 75, 20).readNumber()
                    ammo = Region(x + 24, y + 26, 75, 20).readNumber()
                    steel = Region(x + 131, y - 2, 75, 20).readNumber()
                    bauxite = Region(x + 131, y + 26, 75, 20).readNumber()
                } ?: logger.warn("Resources unreadable, maybe something is blocking it.")

                Screen().exists("bucket.png")?.apply {
                    buckets = Region(x + 17, y - 4, 60, 25).readNumber()
                } ?: logger.warn("Buckets unreadable, maybe something is blocking it.")

                Screen().exists("devmat.png")?.apply {
                    devmats = Region(x + 21, y - 5, 70, 25).readNumber()
                } ?: logger.warn("Devmats unreadable, maybe something is blocking it.")

                logger.info("Fuel: $fuel | Ammo: $ammo | Steel: $steel | Bauxite: $bauxite | Buckets: $buckets | DevMats: $devmats")
                return Resources(fuel, ammo, steel, bauxite, buckets, devmats)
            }
            return Resources(-1, -1, -1, -1, -1, -1)
        }

        private val numberReplacements = mapOf(
                "cCDGoOQ@" to "0", "iIl\\[\\]|!" to "1",
                "zZ" to "2", "E" to "3",
                "A" to "4", "sS" to "5",
                "B:" to "8", " -" to ""
        )

        private fun Region.readNumber(scaleFactor: Double = 3.0, threshold: Double = 0.4): Int {
            val stringList = mutableListOf<String>()
            repeat(3) { i ->
                val image = screen.capture(this).image
                        .scale(scaleFactor + i).binarizeImage(threshold)
                var text = TextRecognizer.getInstance().recognizeWord(image)
                numberReplacements.forEach { r, num ->
                    text = text.replace(Regex("[$r]"), num)
                }
                text.toIntOrNull()?.let {
                    return it
                } ?: stringList.add(text)
            }
            logger.error("Could not read number from text: $stringList")
            return -1
        }
    }
}
