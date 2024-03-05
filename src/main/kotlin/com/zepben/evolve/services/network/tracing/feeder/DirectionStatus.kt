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
 * Interface to query or set the [FeederDirection] for a [Terminal].
 */
interface DirectionStatus {

    /**
     * @return The direction added to this status.
     */
    val value: FeederDirection

    /**
     * Clears the existing direction and sets it to the specified direction.
     *
     * @param direction The direction of the [Terminal].
     * @return True if the direction has been updated, otherwise false.
     */
    fun set(direction: FeederDirection): Boolean

    /**
     * Adds the given direction to the [Terminal].
     *
     * @param direction The direction to add to the [Terminal].
     * @return True if the direction has been updated, otherwise false.
     */
    fun add(direction: FeederDirection): Boolean

    /**
     * Removes the given direction from the [Terminal].
     *
     * @param direction The direction to remove from the [Terminal].
     * @return True if the direction has been updated, otherwise false.
     */
    fun remove(direction: FeederDirection): Boolean

}
