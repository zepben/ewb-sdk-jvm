/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61968.assetinfo

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61968.assets.AssetInfo
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import java.util.function.BiConsumer

/**
 * [ZBEX]
 * Relay Datasheet Information.
 *
 * @property curveSetting [ZBEX] The type of curve used for the Relay.
 * @property recloseFast [ZBEX] true if recloseDelays are associated with a fast Curve, false otherwise.
 * @property recloseDelays [ZBEX] The reclose delays for this curve and relay type. The index of the list is the reclose step, and the value is the overall delay time.
 */
@ZBEX
class RelayInfo(mRID: String) : AssetInfo(mRID) {

    @ZBEX
    var curveSetting: String? = null

    @ZBEX
    var recloseFast: Boolean? = null

    private var _recloseDelays: MutableList<Double>? = null

    @ZBEX
    val recloseDelays: List<Double> get() = _recloseDelays.asUnmodifiable()

    /**
     * Returns the number of reclose delays for this [RelayInfo]
     */
    fun numDelays(): Int = _recloseDelays?.size ?: 0

    /**
     * Get the reclose delay at the specified index, if it exists. Otherwise, this returns null.
     *
     * @param sequenceNumber the index of the reclose delay.
     * @return the reclose delay at [sequenceNumber] if it exists, otherwise null.
     */
    fun getDelay(sequenceNumber: Int): Double? = _recloseDelays?.getOrNull(sequenceNumber)

    /**
     * Java interop forEachIndexed. Perform the specified action against each reclose delay ([Double]).
     *
     * @param action The action to perform on each reclose delay ([Double])
     */
    fun forEachDelay(action: BiConsumer<Int, Double>) {
        _recloseDelays?.forEachIndexed(action::accept)
    }

    /**
     * Add a reclose delay
     * @param delay The delay in seconds to add.
     * @param sequenceNumber The index into the list to add the delay at. Defaults to the end of the list.
     * @return This [RelayInfo] for fluent use.
     */
    @JvmOverloads
    fun addDelay(
        delay: Double,
        sequenceNumber: Int = numDelays()
    ): RelayInfo {
        require(sequenceNumber in 0..(numDelays())) {
            "Unable to add Double to ${typeNameAndMRID()}. " +
                "Sequence number $sequenceNumber is invalid. Expected a value between 0 and ${numDelays()}. " +
                "Make sure you are adding the items in order and there are no gaps in the numbering."
        }

        _recloseDelays = _recloseDelays ?: mutableListOf()
        _recloseDelays!!.add(sequenceNumber, delay)

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
     * Remove a delay by its value.
     * @param delay The value of the delay to remove.
     * @return true if a delay was removed, false otherwise.
     */
    fun removeDelay(delay: Double): Boolean {
        val ret = _recloseDelays?.remove(delay) == true
        if (_recloseDelays.isNullOrEmpty()) _recloseDelays = null
        return ret
    }

    /**
     * Remove a delay from the list.
     * @param index The index of the delay to remove.
     * @return The delay that was removed, or null if no delay was present at [index].
     */
    fun removeDelayAt(index: Int): Double? {
        if (index >= numDelays()) return null
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

/**
 * Perform the specified action against each reclose delay ([Double]).
 *
 * @param action The action to perform on each reclose delay ([Double])
 */
fun RelayInfo.forEachDelay(action: (sequenceNumber: Int, delay: Double) -> Unit): Unit = forEachDelay(BiConsumer(action))
