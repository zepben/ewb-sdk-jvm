/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.protection

import com.zepben.ewb.boilerplate.collections.LazyIndexList
import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.interfaces.MridCollection
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.extensions.iec61968.assetinfo.RelayInfo
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.Sensor
import com.zepben.ewb.cim.iec61970.base.core.PowerSystemResource
import com.zepben.ewb.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference
import java.util.function.BiConsumer

/**
 * [ZBEX] This extension is in-line with the CIM working group for replacing the `protection` package, can be replaced when the working
 * group outcome is merged into the CIM model.
 *
 * A function that a relay implements to protect equipment.
 *
 * @property model [ZBEX] The protection equipment type name (manufacturer information)
 * @property reclosing [ZBEX] True if the protection equipment is reclosing or False otherwise.
 * @property relayDelayTime [ZBEX] The time delay from detection of abnormal conditions to relay operation in seconds.
 * @property protectionKind [ZBEX] The kind of protection being provided by this [ProtectionRelayFunction].
 * @property directable [ZBEX] Whether this [ProtectionRelayFunction] responds to power flow in a given direction.
 * @property powerDirection [ZBEX] The flow of power direction used by this [ProtectionRelayFunction].
 * @property timeLimits [ZBEX] The time limits (in seconds) for this relay function. Order of entries corresponds to the order of entries in thresholds.
 * @property thresholds [ZBEX] The thresholds for this relay function. The order of thresholds corresponds to the order of time limits.
 * @property protectedSwitches [ZBEX] The [ProtectedSwitch]es operated by this [ProtectionRelayFunction].
 * @property sensors [ZBEX] The [Sensor]s for this relay function.
 * @property schemes [ZBEX] The schemes this function operates under.
 */
@ZBEX
abstract class ProtectionRelayFunction(mRID: String) : PowerSystemResource(mRID) {

    override var assetInfo: RelayInfo? = null

    @ZBEX
    var model: String? = null

    @ZBEX
    var reclosing: Boolean? = null

    @ZBEX
    var relayDelayTime: Double? = null

    @ZBEX
    var protectionKind: ProtectionKind = ProtectionKind.UNKNOWN

    @ZBEX
    var directable: Boolean? = null

    @ZBEX
    var powerDirection: PowerDirectionKind = PowerDirectionKind.UNKNOWN

    private var _timeLimits: MutableList<Double>? = null
    private var _protectedSwitches: MutableList<ProtectedSwitch>? = null
    private var _sensors: MutableList<Sensor>? = null
    private var _thresholds: MutableList<RelaySetting>? = null
    private var _schemes: MutableList<ProtectionRelayScheme>? = null

    @ZBEX
    val timeLimits: LazyIndexList<Double> get() = LazyIndexList(
        { _timeLimits },
        { _timeLimits = it },
        this,
        "A Double"
    )

    @ZBEX
    val thresholds: LazyIndexList<RelaySetting> get() = LazyIndexList(
        { _thresholds },
        { _thresholds = it },
        this,
        "A RelaySetting"
    )

    @ZBEX
    val protectedSwitches: MridCollection<ProtectedSwitch> get() = LazyMridList(
        getter = { _protectedSwitches },
        setter = { _protectedSwitches = it },
        owner = this,
        elementDescription = "A ProtectedSwitch"
    )

    @ZBEX
    val sensors: MridCollection<Sensor> get() = LazyMridList(
        getter = { _sensors },
        setter = { _sensors = it },
        owner = this,
        elementDescription = "A Sensor"
    )

    @ZBEX
    val schemes: MridCollection<ProtectionRelayScheme> get() = LazyMridList(
        getter = { _schemes },
        setter = { _schemes = it },
        owner = this,
        elementDescription = "A ProtectionRelayScheme"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region timeLimits boilerplate

    /**
     * Returns the number of time limits for this [ProtectionRelayFunction]
     */
    @Deprecated(
        message = "Use timeLimits.size instead.",
        replaceWith = ReplaceWith("timeLimits.size")
    )
    fun numTimeLimits(): Int = _timeLimits?.size ?: 0

    /**
     * Get the time limit of this [ProtectionRelayFunction] with index [sequenceNumber] if it exists, otherwise null.
     *
     * @param sequenceNumber The index of the desired time limit.
     * @return The time limit with the specified [sequenceNumber] if it exists, otherwise null.
     */
    @Deprecated(
        message = "Use timeLimits.getOrNull(sequenceNumber) instead.",
        replaceWith = ReplaceWith("timeLimits.getOrNull(sequenceNumber)")
    )
    fun getTimeLimit(sequenceNumber: Int): Double? = _timeLimits?.getOrNull(sequenceNumber)

    /**
     * Java interop forEachIndexed. Perform the specified action against each time limit.
     *
     * @param action The action to perform on each time limit
     */
    @Deprecated(
        message = "Use timeLimits.forEachIndexed(action::accept) instead.",
        replaceWith = ReplaceWith("timeLimits.forEachIndexed(action::accept)")
    )
    fun forEachTimeLimit(action: BiConsumer<Int, Double>) {
        _timeLimits?.forEachIndexed(action::accept)
    }

    /**
     * Add a time limit
     * @param timeLimit The time limit in seconds to add.
     * @param index The index into the list to add the time limit at. Defaults to the end of the list.
     * @return This [ProtectionRelayFunction] for fluent use.
     */
    @Deprecated(
        message = "Use timeLimits.add(index, timeLimit) instead.",
        replaceWith = ReplaceWith("also { it.timeLimits.add(index, timeLimit) }")
    )
    @JvmOverloads
    fun addTimeLimit(
        timeLimit: Double,
        index: Int = numTimeLimits(),
    ): ProtectionRelayFunction {
        require(index in 0..(numTimeLimits())) {
            "Unable to add Double to ${typeNameAndMRID()}. " +
                "Sequence number $index is invalid. Expected a value between 0 and ${numTimeLimits()}. " +
                "Make sure you are adding the items in order and there are no gaps in the numbering."
        }

        _timeLimits = _timeLimits ?: mutableListOf()
        _timeLimits!!.add(index, timeLimit)

        return this
    }

    /**
     * Add time limits
     * @param timeLimits The time limits in seconds to add.
     * @return This [ProtectionRelayFunction] for fluent use.
     */
    @Deprecated(
        message = "Use timeLimits.addAll(timeLimits.size, timeLimits.asList()) instead.",
        replaceWith = ReplaceWith("also { timeLimits.forEach { timeLimit -> it.timeLimits.add(timeLimit) } }")
    )
    fun addTimeLimits(vararg timeLimits: Double): ProtectionRelayFunction {
        _timeLimits = _timeLimits ?: mutableListOf()
        timeLimits.forEach {
            _timeLimits!!.add(it)
        }

        return this
    }

    /**
     * Remove a time limit from the list.
     * @param timeLimit The time limit to remove.
     * @return true if the time limit was found and removed.
     */
    @Deprecated(
        message = "Use timeLimits.remove(timeLimit) instead.",
        replaceWith = ReplaceWith("timeLimits.remove(timeLimit)")
    )
    fun removeTimeLimit(timeLimit: Double): Boolean {
        val ret = _timeLimits?.remove(timeLimit) ?: false
        if (_sensors.isNullOrEmpty()) _sensors = null
        return ret
    }

    /**
     * Remove a time limit from the list.
     * @param index The index of the time limit to remove.
     * @return The time limit that was removed, or null if no time limit was present at [index].
     */
    @Deprecated(
        message = "Use timeLimits.removeAtOrNull(index) instead.",
        replaceWith = ReplaceWith("timeLimits.removeAtOrNull(index)")
    )
    fun removeTimeLimitAt(index: Int): Double? {
        if (index >= numTimeLimits()) return null
        val ret = _timeLimits?.removeAt(index)
        if (_timeLimits.isNullOrEmpty()) _timeLimits = null
        return ret
    }

    /**
     * Clear [timeLimits].
     * @return This [ProtectionRelayFunction] for fluent use.
     */
    @Deprecated(
        message = "Use timeLimits.clear() instead.",
        replaceWith = ReplaceWith("also { it.timeLimits.clear() }")
    )
    fun clearTimeLimits(): ProtectionRelayFunction {
        _timeLimits = null
        return this
    }

    // endregion

    // region thresholds boilerplate

    /**
     * Get the number of threshold [RelaySetting]s for this [ProtectionRelayFunction].
     *
     * @return The number of threshold [RelaySetting]s for this [ProtectionRelayFunction].
     */
    @Deprecated(
        message = "Use thresholds.size instead.",
        replaceWith = ReplaceWith("thresholds.size")
    )
    fun numThresholds(): Int = _thresholds?.size ?: 0

    /**
     * Get a threshold [RelaySetting] for this [ProtectionRelayFunction] by its index. Thresholds are 0-indexed. Returns null for out-of-bound indices.
     *
     * @param sequenceNumber The sequence number of the desired threshold [RelaySetting]
     * @return The threshold [RelaySetting] with the specified [sequenceNumber] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use thresholds.getOrNull(sequenceNumber) instead.",
        replaceWith = ReplaceWith("thresholds.getOrNull(sequenceNumber)")
    )
    fun getThreshold(sequenceNumber: Int): RelaySetting? = _thresholds?.getOrNull(sequenceNumber)

    /**
     * Java interop forEachIndexed. Perform the specified action against each threshold [RelaySetting].
     *
     * @param action The action to perform on each threshold [RelaySetting]
     */
    @Deprecated(
        message = "Use thresholds.forEachIndexed(action::accept) instead.",
        replaceWith = ReplaceWith("thresholds.forEachIndexed(action::accept)")
    )
    fun forEachThreshold(action: BiConsumer<Int, RelaySetting>) {
        _thresholds?.forEachIndexed(action::accept)
    }

    /**
     * Add a threshold [RelaySetting] to this [ProtectionRelayFunction]'s list of thresholds.
     *
     * @param threshold The threshold [RelaySetting] to add to this [ProtectionRelayFunction].
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
    @Deprecated(
        message = "Use thresholds.add(sequenceNumber, threshold) instead.",
        replaceWith = ReplaceWith("also { it.thresholds.add(sequenceNumber, threshold) }")
    )
    @JvmOverloads
    fun addThreshold(threshold: RelaySetting, sequenceNumber: Int = numThresholds()): ProtectionRelayFunction {
        require(sequenceNumber in 0..(numThresholds())) {
            "Unable to add RelaySetting to ${typeNameAndMRID()}. " +
                "Sequence number $sequenceNumber is invalid. Expected a value between 0 and ${numThresholds()}. " +
                "Make sure you are adding the items in order and there are no gaps in the numbering."
        }

        _thresholds = _thresholds ?: mutableListOf()
        _thresholds!!.add(sequenceNumber, threshold)

        return this
    }

    /**
     * Removes a threshold [RelaySetting] from this [ProtectionRelayFunction].
     *
     * @param threshold The threshold [RelaySetting] to disassociate from this [ProtectionRelayFunction].
     * @return true if the threshold [RelaySetting] was disassociated.
     */
    @Deprecated(
        message = "Use thresholds.remove(threshold) instead.",
        replaceWith = ReplaceWith("thresholds.remove(threshold)")
    )
    fun removeThreshold(threshold: RelaySetting): Boolean {
        val ret = _thresholds?.remove(threshold) == true
        if (_thresholds.isNullOrEmpty()) _thresholds = null
        return ret
    }

    /**
     * Remove a threshold [RelaySetting] from this [ProtectionRelayFunction] by its sequence number.
     *
     * NOTE: This will update the sequence numbers of all items located after the removed sequence number.
     *
     * @param sequenceNumber The sequence number of the threshold [RelaySetting] to disassociate from this [ProtectionRelayFunction].
     * @return the threshold [RelaySetting] that was disassociated, or null if there was no threshold [RelaySetting] for the given [sequenceNumber].
     */
    @Deprecated(
        message = "Use thresholds.removeAtOrNull(sequenceNumber) instead.",
        replaceWith = ReplaceWith("thresholds.removeAtOrNull(sequenceNumber)")
    )
    fun removeThreshold(sequenceNumber: Int): RelaySetting? {
        _thresholds?.apply {
            if (sequenceNumber >= size)
                return null

            val ret = removeAt(sequenceNumber)
            if (isNullOrEmpty()) _thresholds = null
            return ret
        }

        return null
    }

    /**
     * Removes all threshold [RelaySetting]s from this [ProtectionRelayFunction].
     *
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
    @Deprecated(
        message = "Use thresholds.clear() instead.",
        replaceWith = ReplaceWith("also { it.thresholds.clear() }")
    )
    fun clearThresholds(): ProtectionRelayFunction {
        _thresholds = null
        return this
    }

    // endregion

    // region protectedSwitches boilerplate

    /**
     * Get the number of [ProtectedSwitch]es operated by this [ProtectionRelayFunction].
     *
     * @return The number of [ProtectedSwitch]es operated by this [ProtectionRelayFunction].
     */
    @Deprecated(
        message = "Use protectedSwitches.size instead.",
        replaceWith = ReplaceWith("protectedSwitches.size")
    )
    fun numProtectedSwitches(): Int = _protectedSwitches?.size ?: 0

    /**
     * Get a [ProtectedSwitch] operated by this [ProtectionRelayFunction] by its mRID.
     *
     * @param mRID The mRID of the desired [ProtectedSwitch]
     * @return The [ProtectedSwitch] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use protectedSwitches.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("protectedSwitches.getByMRID(mRID)")
    )
    fun getProtectedSwitch(mRID: String): ProtectedSwitch? = _protectedSwitches?.getByMRID(mRID)

    /**
     * Associate this [ProtectionRelayFunction] with a [ProtectedSwitch] that it operates.
     *
     * @param protectedSwitch The [ProtectedSwitch] to associate with this [ProtectionRelayFunction].
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
    @Deprecated(
        message = "Use protectedSwitches.add(protectedSwitch) instead.",
        replaceWith = ReplaceWith("also { it.protectedSwitches.add(protectedSwitch) }")
    )
    fun addProtectedSwitch(protectedSwitch: ProtectedSwitch): ProtectionRelayFunction {
        if (validateReference(protectedSwitch, ::getProtectedSwitch, "A ProtectedSwitch"))
            return this

        _protectedSwitches = _protectedSwitches ?: mutableListOf()
        _protectedSwitches!!.add(protectedSwitch)

        return this
    }

    /**
     * Disassociate this [ProtectionRelayFunction] from a [ProtectedSwitch].
     *
     * @param protectedSwitch The [ProtectedSwitch] to disassociate from this [ProtectionRelayFunction].
     * @return true if the [ProtectedSwitch] was disassociated.
     */
    @Deprecated(
        message = "Use protectedSwitches.remove(protectedSwitch) instead.",
        replaceWith = ReplaceWith("protectedSwitches.remove(protectedSwitch)")
    )
    fun removeProtectedSwitch(protectedSwitch: ProtectedSwitch): Boolean {
        val ret = _protectedSwitches.safeRemove(protectedSwitch)
        if (_protectedSwitches.isNullOrEmpty()) _protectedSwitches = null
        return ret
    }

    /**
     * Disassociate all [ProtectedSwitch]es from this [ProtectionRelayFunction].
     *
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
    @Deprecated(
        message = "Use protectedSwitches.clear() instead.",
        replaceWith = ReplaceWith("protectedSwitches.clear()")
    )
    fun clearProtectedSwitches(): ProtectionRelayFunction {
        _protectedSwitches = null
        return this
    }

    // endregion

    // region sensors boilerplate

    /**
     * Get the number of [Sensor]s for this [ProtectionRelayFunction].
     *
     * @return The number of [Sensor]s for this [ProtectionRelayFunction].
     */
    @Deprecated(
        message = "Use sensors.size instead.",
        replaceWith = ReplaceWith("sensors.size")
    )
    fun numSensors(): Int = _sensors?.size ?: 0

    /**
     * Get a [Sensor] for this [ProtectionRelayFunction] by its mRID.
     *
     * @param mRID The mRID of the desired [Sensor]
     * @return The [Sensor] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use sensors.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("sensors.getByMRID(mRID)")
    )
    fun getSensor(mRID: String): Sensor? = _sensors?.getByMRID(mRID)

    /**
     * Associate this [ProtectionRelayFunction] with a [Sensor].
     *
     * @param sensor The [Sensor] to associate with this [ProtectionRelayFunction].
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
    @Deprecated(
        message = "Use sensors.add(sensor) instead.",
        replaceWith = ReplaceWith("also { it.sensors.add(sensor) }")
    )
    fun addSensor(sensor: Sensor): ProtectionRelayFunction {
        if (validateReference(sensor, ::getSensor, "A Sensor"))
            return this

        _sensors = _sensors ?: mutableListOf()
        _sensors!!.add(sensor)

        return this
    }

    /**
     * Disassociate this [ProtectionRelayFunction] from a [Sensor].
     *
     * @param sensor The [Sensor] to disassociate from this [ProtectionRelayFunction].
     * @return true if the [Sensor] was disassociated.
     */
    @Deprecated(
        message = "Use sensors.remove(sensor) instead.",
        replaceWith = ReplaceWith("sensors.remove(sensor)")
    )
    fun removeSensor(sensor: Sensor): Boolean {
        val ret = _sensors.safeRemove(sensor)
        if (_sensors.isNullOrEmpty()) _sensors = null
        return ret
    }

    /**
     * Disassociate all [Sensor]s from this [ProtectionRelayFunction].
     *
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
    @Deprecated(
        message = "Use sensors.clear() instead.",
        replaceWith = ReplaceWith("sensors.clear()")
    )
    fun clearSensors(): ProtectionRelayFunction {
        _sensors = null
        return this
    }

    // endregion

    // region schemes boilerplate

    /**
     * Get the number of [ProtectionRelayScheme]s this [ProtectionRelayFunction] operates under.
     *
     * @return The number of [ProtectionRelayScheme]s this [ProtectionRelayFunction] operates under.
     */
    @Deprecated(
        message = "Use schemes.size instead.",
        replaceWith = ReplaceWith("schemes.size")
    )
    fun numSchemes(): Int = _schemes?.size ?: 0

    /**
     * Get a [ProtectionRelayScheme] this [ProtectionRelayFunction] operates under by its mRID.
     *
     * @param mRID The mRID of the desired [ProtectionRelayScheme]
     * @return The [ProtectionRelayScheme] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use schemes.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("schemes.getByMRID(mRID)")
    )
    fun getScheme(mRID: String): ProtectionRelayScheme? = _schemes?.getByMRID(mRID)

    /**
     * Associate this [ProtectionRelayFunction] to a [ProtectionRelayScheme] it operates under.
     *
     * @param scheme The [ProtectionRelayScheme] to associate with this [ProtectionRelayFunction].
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
    @Deprecated(
        message = "Use schemes.add(scheme) instead.",
        replaceWith = ReplaceWith("also { it.schemes.add(scheme) }")
    )
    fun addScheme(scheme: ProtectionRelayScheme): ProtectionRelayFunction {
        if (validateReference(scheme, ::getScheme, "A ProtectionRelayScheme"))
            return this

        _schemes = _schemes ?: mutableListOf()
        _schemes!!.add(scheme)

        return this
    }

    /**
     * Disassociate this [ProtectionRelayFunction] from a [ProtectionRelayScheme].
     *
     * @param scheme The [ProtectionRelayScheme] to disassociate from this [ProtectionRelayFunction].
     * @return true if the [ProtectionRelayScheme] was disassociated.
     */
    @Deprecated(
        message = "Use schemes.remove(scheme) instead.",
        replaceWith = ReplaceWith("schemes.remove(scheme)")
    )
    fun removeScheme(scheme: ProtectionRelayScheme): Boolean {
        val ret = _schemes.safeRemove(scheme)
        if (_schemes.isNullOrEmpty()) _schemes = null
        return ret
    }

    /**
     * Disassociate all [ProtectionRelayScheme]s from this [ProtectionRelayFunction].
     *
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
    @Deprecated(
        message = "Use schemes.clear() instead.",
        replaceWith = ReplaceWith("schemes.clear()")
    )
    fun clearSchemes(): ProtectionRelayFunction {
        _schemes = null
        return this
    }

    // endregion

    // endregion
}

/**
 * Perform the specified action against each time limit.
 *
 * @param action The action to perform on each time limit
 */
fun ProtectionRelayFunction.forEachTimeLimits(action: (sequenceNumber: Int, timeLimit: Double) -> Unit): Unit = forEachTimeLimit(BiConsumer(action))

/**
 * Perform the specified action against each threshold.
 *
 * @param action The action to perform on each threshold
 */
fun ProtectionRelayFunction.forEachThreshold(action: (sequenceNumber: Int, threshold: RelaySetting) -> Unit): Unit = forEachThreshold(BiConsumer(action))
