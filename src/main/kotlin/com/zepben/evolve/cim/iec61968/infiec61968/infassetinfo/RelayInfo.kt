/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo

import com.zepben.evolve.cim.iec61968.assets.AssetInfo
import com.zepben.evolve.services.common.extensions.asUnmodifiable

/**
 * Relay Datasheet Information.
 *
 * @property curveSetting The type of curve used for the Relay.
 */
class RelayInfo(mRID: String = "") : AssetInfo(mRID) {

    var curveSetting: String? = null
    var _recloseDelays: MutableList<Double>? = null

    /**
     * The reclose delays for this curve and relay type. The index of the list is the reclose step, and the value is the overall delay time.
     */
    val recloseDelays: List<Double> get() = _recloseDelays.asUnmodifiable()

    /**
     * Returns the number of reclose delays for this [RelayInfo]
     */
    fun numDelays(): Int = _recloseDelays?.size ?: 0

    /**
     * Add a reclose delay
     * @param delay The delay in seconds to add.
     * @param index The index into the list to add the delay at. Defaults to the end of the list.
     * @return This [RelayInfo] for fluent use.
     */
    fun addDelay(
        delay: Double,
        index: Int = numDelays()
    ): RelayInfo {
        _recloseDelays = _recloseDelays ?: mutableListOf()
        _recloseDelays!!.add(index, delay)

        return this
    }

    /**
     * Add reclose delays
     * @param delays The delays in seconds to add.
     * @return This [RelayInfo] for fluent use.
     */
    fun addDelays(
        vararg delays: Double,
    ): RelayInfo {
        _recloseDelays = _recloseDelays ?: mutableListOf()
        delays.forEach {
            _recloseDelays!!.add(it)
        }

        return this
    }

    /**
     * Remove a delay from the list.
     * @param index The index of the delay to remove.
     * @return The delay that was removed, or null if no delay was present at [index].
     */
    fun removeDelay(index: Int): Double? {
        val ret = _recloseDelays?.removeAt(index)
        if (_recloseDelays.isNullOrEmpty()) _recloseDelays = null
        return ret
    }

    /**
     * Clear [recloseDelays].
     * @return This [RelayInfo] for fluent use.
     */
    fun clearDelays(): RelayInfo {
        _recloseDelays = null
        return this
    }


}
