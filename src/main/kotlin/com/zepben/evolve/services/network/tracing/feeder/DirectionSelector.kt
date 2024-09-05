/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.Terminal

/**
 * Functional interface that can be used by traces to specify which [DirectionStatus] to use.
 * See [SetDirection] or [RemoveDirection] for example usage.
 */
fun interface DirectionSelector {

    fun select(terminal: Terminal): DirectionStatus

    fun selectOrNull(terminal: Terminal?): DirectionStatus? = terminal?.let { select(it) }

    // Constant common implements of ActivePhaseSelector
    companion object {

        @JvmField
        val NORMAL_DIRECTION: DirectionSelector = DirectionSelector { terminal: Terminal ->
            object : DirectionStatus {

                override val value: FeederDirection
                    get() = terminal.normalFeederDirection

                override fun set(direction: FeederDirection): Boolean {
                    if (terminal.normalFeederDirection == direction)
                        return false

                    terminal.normalFeederDirection = direction
                    return true
                }

                override fun add(direction: FeederDirection): Boolean {
                    val previous = terminal.normalFeederDirection
                    val new = previous + direction
                    if (new == previous)
                        return false

                    terminal.normalFeederDirection = new
                    return true
                }

                override fun remove(direction: FeederDirection): Boolean {
                    val previous = terminal.normalFeederDirection
                    val new = previous - direction
                    if (new == previous)
                        return false

                    terminal.normalFeederDirection = new
                    return true
                }
            }
        }

        @JvmField
        val CURRENT_DIRECTION: DirectionSelector = DirectionSelector { terminal: Terminal ->
            object : DirectionStatus {
                override val value: FeederDirection
                    get() = terminal.currentFeederDirection

                override fun set(direction: FeederDirection): Boolean {
                    if (terminal.currentFeederDirection == direction)
                        return false

                    terminal.currentFeederDirection = direction
                    return true
                }

                override fun add(direction: FeederDirection): Boolean {
                    val previous = terminal.currentFeederDirection
                    val new = previous + direction
                    if (new == previous)
                        return false

                    terminal.currentFeederDirection = new
                    return true
                }

                override fun remove(direction: FeederDirection): Boolean {
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

}
