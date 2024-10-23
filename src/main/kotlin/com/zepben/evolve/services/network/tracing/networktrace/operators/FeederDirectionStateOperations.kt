/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.operators

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection

interface FeederDirectionStateOperations {
    fun getDirection(terminal: Terminal): FeederDirection
    fun setDirection(terminal: Terminal, direction: FeederDirection): Boolean
    fun addDirection(terminal: Terminal, direction: FeederDirection): Boolean
    fun removeDirection(terminal: Terminal, direction: FeederDirection): Boolean

    companion object {
        val NORMAL: FeederDirectionStateOperations = object : FeederDirectionStateOperations {
            override fun getDirection(terminal: Terminal): FeederDirection = terminal.normalFeederDirection

            override fun setDirection(terminal: Terminal, direction: FeederDirection): Boolean {
                if (terminal.normalFeederDirection == direction)
                    return false

                terminal.normalFeederDirection = direction
                return true
            }

            override fun addDirection(terminal: Terminal, direction: FeederDirection): Boolean {
                val previous = terminal.normalFeederDirection
                val new = previous + direction
                if (new == previous)
                    return false

                terminal.normalFeederDirection = new
                return true
            }

            override fun removeDirection(terminal: Terminal, direction: FeederDirection): Boolean {
                val previous = terminal.normalFeederDirection
                val new = previous - direction
                if (new == previous)
                    return false

                terminal.normalFeederDirection = new
                return true
            }
        }

        val CURRENT: FeederDirectionStateOperations = object : FeederDirectionStateOperations {
            override fun getDirection(terminal: Terminal): FeederDirection = terminal.currentFeederDirection

            override fun setDirection(terminal: Terminal, direction: FeederDirection): Boolean {
                if (terminal.currentFeederDirection == direction)
                    return false

                terminal.currentFeederDirection = direction
                return true
            }

            override fun addDirection(terminal: Terminal, direction: FeederDirection): Boolean {
                val previous = terminal.currentFeederDirection
                val new = previous + direction
                if (new == previous)
                    return false

                terminal.currentFeederDirection = new
                return true
            }

            override fun removeDirection(terminal: Terminal, direction: FeederDirection): Boolean {
                val previous = terminal.currentFeederDirection
                val new = previous - direction
                if (new == previous)
                    return false

                terminal.currentFeederDirection = new
                return true
            }
        }
    }
}
