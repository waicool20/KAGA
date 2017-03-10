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

package com.waicool20.kaga.util

import javafx.beans.property.DoubleProperty
import javafx.beans.property.FloatProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.LongProperty

fun IntegerProperty.inc() = set(value + 1)

fun IntegerProperty.dec() = set(value - 1)

fun FloatProperty.inc() = set(value + 1)

fun FloatProperty.dec() = set(value - 1)

fun DoubleProperty.inc() = set(value + 1)

fun DoubleProperty.dec() = set(value - 1)

fun LongProperty.inc() = set(value + 1)

fun LongProperty.dec() = set(value - 1)
