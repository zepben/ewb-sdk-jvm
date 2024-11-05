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

/**
 * Interface for accessing and managing the [FeederDirection] associated with [Terminal]s.
 */
interface FeederDirectionStateOperations {

    /**
     * Retrieves the feeder direction for the specified terminal.
     *
     * @param terminal The terminal for which to retrieve the feeder direction.
     * @return The current feeder direction associated with the specified terminal.
     */
    fun getDirection(terminal: Terminal): FeederDirection

    /**
     * Sets the feeder direction for the specified terminal.
     *
     * @param terminal The terminal for which to set the feeder direction.
     * @param direction The new feeder direction to assign to the terminal.
     * @return `true` if the direction was changed; `false` if the direction was already set to the specified value.
     */
    fun setDirection(terminal: Terminal, direction: FeederDirection): Boolean

    /**
     * Adds the specified feeder direction to the terminal, preserving existing directions.
     *
     * @param terminal The terminal for which to add the feeder direction.
     * @param direction The feeder direction to add.
     * @return `true` if the direction was added successfully; `false` if the direction was already present.
     */
    fun addDirection(terminal: Terminal, direction: FeederDirection): Boolean

    /**
     * Removes the specified feeder direction from the terminal.
     *
     * @param terminal The terminal for which to remove the feeder direction.
     * @param direction The feeder direction to remove.
     * @return `true` if the direction was removed; `false` if the direction was not present.
     */
    fun removeDirection(terminal: Terminal, direction: FeederDirection): Boolean

    companion object {
        /**
         * Instance operating on the normal feeder direction of terminals.
         */
        val NORMAL: FeederDirectionStateOperations = NormalFeederDirectionStateOperations()

        /**
         * Instance operating on the current feeder of terminals.
         */
        val CURRENT: FeederDirectionStateOperations = CurrentFeederDirectionStateOperations()
    }
}

private class NormalFeederDirectionStateOperations : FeederDirectionStateOperations {
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

private class CurrentFeederDirectionStateOperations : FeederDirectionStateOperations {
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
