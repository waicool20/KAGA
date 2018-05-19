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

import java.security.Permission

class IllegalExitException : SecurityException()

/**
 * Prevents calls to System.exit() inside the given action lambda block
 *
 * @param action Lambda to be executed that prevents exit calls
 * @return Lambda result
 * @throws IllegalExitException in case action block tries exit
 */
fun <T> preventSystemExit(action: () -> T): T {
    val manager = System.getSecurityManager()
    val exitManager = object : SecurityManager() {
        override fun checkPermission(permission: Permission) {
            if (permission.name.contains("exitVM")) {
                throw IllegalExitException()
            }
        }
    }
    System.setSecurityManager(exitManager)
    return try {
        action()
    } catch (e: Throwable) {
        if (e.cause is IllegalExitException) {
            System.setSecurityManager(manager)
            throw IllegalExitException()
        }
        throw e
    }
}
