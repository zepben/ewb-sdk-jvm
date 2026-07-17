/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61968.assetinfo

import com.zepben.ewb.boilerplate.LazyIndexedList
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61968.assets.AssetInfo
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
    val recloseDelays: LazyIndexedList<Double> get() = LazyIndexedList(
        { _recloseDelays },
        { _recloseDelays = it },
        this,
        "Double"
    )


    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region recloseDelays boilerplate

    @Deprecated(
        message = "Use recloseDelays.size instead.",
        replaceWith = ReplaceWith("recloseDelays.size")
    )
    fun numDelays(): Int = recloseDelays.size

    @Deprecated(
        message = "Use recloseDelays.getOrNull(sequenceNumber) instead.",
        replaceWith = ReplaceWith("recloseDelays.getOrNull(sequenceNumber)")
    )
    fun getDelay(sequenceNumber: Int): Double? = recloseDelays.getOrNull(sequenceNumber)

    @Deprecated(
        message = "Use recloseDelays.forEachIndexed(action::accept) instead.",
        replaceWith = ReplaceWith("recloseDelays.forEachIndexed(action::accept)")
    )
    fun forEachDelay(action: BiConsumer<Int, Double>) = recloseDelays.forEachIndexed(action::accept)

    @Deprecated(
        message = "Use recloseDelays.add(sequenceNumber, delay) instead.",
        replaceWith = ReplaceWith("also { it.recloseDelays.add(sequenceNumber, delay) }")
    )
    @JvmOverloads
    fun addDelay(
        delay: Double,
        sequenceNumber: Int = numDelays(),
    ): RelayInfo {
        recloseDelays.add(sequenceNumber, delay)
        return this
    }

    @Deprecated(
        message = "Use recloseDelays.addAll(delays.asList()) instead.",
        replaceWith = ReplaceWith("also { it.recloseDelays.addAll(delays.asList()) }")
    )
    fun addDelays(
        vararg delays: Double,
    ): RelayInfo {
        recloseDelays.addAll(delays.asList())
        return this
    }

    @Deprecated(
        message = "Use recloseDelays.remove(delay) instead.",
        replaceWith = ReplaceWith("recloseDelays.remove(delay)")
    )
    fun removeDelay(delay: Double): Boolean = recloseDelays.remove(delay)

    @Deprecated(
        message = "Use recloseDelays.removeAt(index) instead.",
        replaceWith = ReplaceWith("recloseDelays.removeAt(index)")
    )
    fun removeDelayAt(index: Int): Double? = recloseDelays.removeAt(index)

    @Deprecated(
        message = "Use recloseDelays.clear() instead.",
        replaceWith = ReplaceWith("also { it.recloseDelays.clear() }")
    )
    fun clearDelays(): RelayInfo {
        recloseDelays.clear()
        return this
    }

    // endregion

    // endregion

}

/**
 * Perform the specified action against each reclose delay ([Double]).
 *
 * @param action The action to perform on each reclose delay ([Double])
 */
fun RelayInfo.forEachDelay(action: (sequenceNumber: Int, delay: Double) -> Unit): Unit = forEachDelay(BiConsumer(action))
