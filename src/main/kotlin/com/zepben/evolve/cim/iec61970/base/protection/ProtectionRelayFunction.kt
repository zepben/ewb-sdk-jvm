/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.Sensor
import com.zepben.evolve.cim.iec61970.base.core.PowerSystemResource
import com.zepben.evolve.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.evolve.cim.iec61970.infiec61970.protection.PowerDirectionKind
import com.zepben.evolve.cim.iec61970.infiec61970.protection.ProtectionKind
import com.zepben.evolve.services.common.extensions.*

/**
 * A function that a relay implements to protect equipment.
 *
 * @property model The protection equipment type name (manufacturer information)
 * @property reclosing True if the protection equipment is reclosing or False otherwise.
 * @property relayDelayTime The time delay from detection of abnormal conditions to relay operation in seconds.
 * @property protectionKind The kind of protection being provided by this ProtectionRelayFunction.
 * @property directable Whether this [ProtectionRelayFunction] responds to power flow in a given direction.
 * @property powerDirection The flow of power direction used by this ProtectionRelayFunction.
 */
abstract class ProtectionRelayFunction(mRID: String = "") : PowerSystemResource(mRID) {

    var model: String? = null
    var reclosing: Boolean? = null
    var relayDelayTime: Double? = null
    var protectionKind: ProtectionKind = ProtectionKind.UNKNOWN
    var directable: Boolean? = null
    var powerDirection: PowerDirectionKind = PowerDirectionKind.UNKNOWN_DIRECTION
    
    private var _timeLimits: MutableList<Double>? = null
    private var _protectedSwitches: MutableList<ProtectedSwitch>? = null
    private var _sensors: MutableList<Sensor>? = null
    private var _thresholds: MutableList<RelaySetting>? = null
    private var _schemes: MutableList<ProtectionRelayScheme>? = null

    /**
     * Returns a read-only [List] of time limits (in seconds) for this [ProtectionRelayFunction].
     *
     * @return A read-only [List] of time limits (in seconds) for this [ProtectionRelayFunction].
     */
    val timeLimits: List<Double> get() = _timeLimits.asUnmodifiable()

    /**
     * Returns the number of time limits for this [ProtectionRelayFunction]
     */
    fun numTimeLimits(): Int = _timeLimits?.size ?: 0

    /**
     * Add a time limit
     * @param timeLimit The time limit in seconds to add.
     * @param index The index into the list to add the time limit at. Defaults to the end of the list.
     * @return This [ProtectionRelayFunction] for fluent use.
     */
    fun addTimeLimit(
        timeLimit: Double,
        index: Int = numTimeLimits()
    ): ProtectionRelayFunction {
        require(index in 0..(numTimeLimits())) {
            "Unable to add PositionPoint to ${typeNameAndMRID()}. " +
                "Index $index is invalid. Expected a value between 0 and ${numTimeLimits()}. " +
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
    fun addTimeLimits(
        vararg timeLimits: Double,
    ): ProtectionRelayFunction {
        _timeLimits = _timeLimits ?: mutableListOf()
        timeLimits.forEach {
            _timeLimits!!.add(it)
        }

        return this
    }

    /**
     * Remove a time limit from the list.
     * @param index The index of the time limit to remove.
     * @return The time limit that was removed, or null if no time limit was present at [index].
     */
    fun removeTimeLimit(index: Int): Double? {
        val ret = _timeLimits?.removeAt(index)
        if (_timeLimits.isNullOrEmpty()) _timeLimits = null
        return ret
    }

    /**
     * Clear [timeLimits].
     * @return This [ProtectionRelayFunction] for fluent use.
     */
    fun clearTimeLimits(): ProtectionRelayFunction {
        _timeLimits = null
        return this
    }

    /**
     * All [ProtectedSwitch]es operated by this [ProtectionRelayFunction]. Collection is read-only.
     *
     * @return A read-only [Collection] of [ProtectedSwitch]es operated by this [ProtectionRelayFunction].
     */
    val protectedSwitches: Collection<ProtectedSwitch> get() = _protectedSwitches.asUnmodifiable()

    /**
     * Get the number of [ProtectedSwitch]es operated by this [ProtectionRelayFunction].
     *
     * @return The number of [ProtectedSwitch]es operated by this [ProtectionRelayFunction].
     */
    fun numProtectedSwitches(): Int = _protectedSwitches?.size ?: 0

    /**
     * Get a [ProtectedSwitch] operated by this [ProtectionRelayFunction] by its mRID.
     *
     * @param mRID The mRID of the desired [ProtectedSwitch]
     * @return The [ProtectedSwitch] with the specified [mRID] if it exists, otherwise null
     */
    fun getProtectedSwitch(mRID: String): ProtectedSwitch? = _protectedSwitches?.getByMRID(mRID)

    /**
     * Associate this [ProtectionRelayFunction] with a [ProtectedSwitch] that it operates.
     *
     * @param protectedSwitch The [ProtectedSwitch] to associate with this [ProtectionRelayFunction].
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
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
    fun removeProtectedSwitch(protectedSwitch: ProtectedSwitch?): Boolean {
        val ret = _protectedSwitches.safeRemove(protectedSwitch)
        if (_protectedSwitches.isNullOrEmpty()) _protectedSwitches = null
        return ret
    }

    /**
     * Disassociate all [ProtectedSwitch]es from this [ProtectionRelayFunction].
     *
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
    fun clearProtectedSwitches(): ProtectionRelayFunction {
        _protectedSwitches = null
        return this
    }

    /**
     * All [Sensor]s for this [ProtectionRelayFunction]. Collection is read-only.
     *
     * @return A read-only [Collection] of [Sensor]s for this [ProtectionRelayFunction].
     */
    val sensors: Collection<Sensor> get() = _sensors.asUnmodifiable()

    /**
     * Get the number of [Sensor]s for this [ProtectionRelayFunction].
     *
     * @return The number of [Sensor]s for this [ProtectionRelayFunction].
     */
    fun numSensors(): Int = _sensors?.size ?: 0

    /**
     * Get a [Sensor] for this [ProtectionRelayFunction] by its mRID.
     *
     * @param mRID The mRID of the desired [Sensor]
     * @return The [Sensor] with the specified [mRID] if it exists, otherwise null
     */
    fun getSensor(mRID: String): Sensor? = _sensors?.getByMRID(mRID)

    /**
     * Associate this [ProtectionRelayFunction] with a [Sensor].
     *
     * @param sensor The [Sensor] to associate with this [ProtectionRelayFunction].
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
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
    fun removeSensor(sensor: Sensor?): Boolean {
        val ret = _sensors.safeRemove(sensor)
        if (_sensors.isNullOrEmpty()) _sensors = null
        return ret
    }

    /**
     * Disassociate all [Sensor]s from this [ProtectionRelayFunction].
     *
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
    fun clearSensors(): ProtectionRelayFunction {
        _sensors = null
        return this
    }

    /**
     * All threshold [RelaySetting]s for this [ProtectionRelayFunction]. Collection is read-only.
     *
     * @return A read-only [List] of threshold [RelaySetting]s for this [ProtectionRelayFunction].
     */
    val thresholds: List<RelaySetting> get() = _thresholds.asUnmodifiable()

    /**
     * Get the number of threshold [RelaySetting]s for this [ProtectionRelayFunction].
     *
     * @return The number of threshold [RelaySetting]s for this [ProtectionRelayFunction].
     */
    fun numThresholds(): Int = _thresholds?.size ?: 0

    /**
     * Get a threshold [RelaySetting] for this [ProtectionRelayFunction] by its index. Thresholds are 0-indexed. Returns null for out-of-bound indices.
     *
     * @param sequenceNumber The index of the desired threshold [RelaySetting]
     * @return The threshold [RelaySetting] with the specified [sequenceNumber] if it exists, otherwise null
     */
    fun getThreshold(sequenceNumber: Int): RelaySetting? = _thresholds?.getOrNull(sequenceNumber)

    /**
     * Insert a threshold [RelaySetting] into this [ProtectionRelayFunction]'s list of thresholds.
     *
     * @param threshold The threshold [RelaySetting] to add to this [ProtectionRelayFunction].
     * @param index The index to insert the threshold. Defaults to adding the threshold to the end of the list.
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
    fun addThreshold(threshold: RelaySetting, index: Int = numThresholds()): ProtectionRelayFunction {
        require(index in 0..(numThresholds())) {
            "Unable to add RelaySetting to ${typeNameAndMRID()}. " +
                "Index $index is invalid. Expected a value between 0 and ${numThresholds()}."
        }

        _thresholds = _thresholds ?: mutableListOf()
        _thresholds!!.add(index, threshold)

        return this
    }

    /**
     * Removes a threshold [RelaySetting] from this [ProtectionRelayFunction].
     *
     * @param threshold The threshold [RelaySetting] to disassociate from this [ProtectionRelayFunction].
     * @return true if the threshold [RelaySetting] was disassociated.
     */
    fun removeThreshold(threshold: RelaySetting?): Boolean {
        val ret = _thresholds.safeRemove(threshold)
        if (_thresholds.isNullOrEmpty()) _thresholds = null
        return ret
    }

    /**
     * Removes all threshold [RelaySetting]s from this [ProtectionRelayFunction].
     *
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
    fun clearThresholds(): ProtectionRelayFunction {
        _thresholds = null
        return this
    }

    /**
     * The schemes this function operates under.
     *
     * @return An unmodifiable [Collection] of [ProtectionRelayScheme]s this [ProtectionRelayFunction] operates under.
     */
    val scheme: Collection<ProtectionRelayScheme> get() = _schemes.asUnmodifiable()

    /**
     * Get the number of [ProtectionRelayScheme]s this [ProtectionRelayFunction] operates under.
     *
     * @return The number of [ProtectionRelayScheme]s this [ProtectionRelayFunction] operates under.
     */
    fun numSchemes(): Int = _schemes?.size ?: 0

    /**
     * Get a [ProtectionRelayScheme] this [ProtectionRelayFunction] operates under by its mRID.
     *
     * @param mRID The mRID of the desired [ProtectionRelayScheme]
     * @return The [ProtectionRelayScheme] with the specified [mRID] if it exists, otherwise null
     */
    fun getScheme(mRID: String): ProtectionRelayScheme? = _schemes?.getByMRID(mRID)

    /**
     * Associate this [ProtectionRelayFunction] to a [ProtectionRelayScheme] it operates under.
     *
     * @param scheme The [ProtectionRelayScheme] to associate with this [ProtectionRelayFunction].
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
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
     * @param function The [ProtectionRelayScheme] to disassociate from this [ProtectionRelayFunction].
     * @return true if the [ProtectionRelayScheme] was disassociated.
     */
    fun removeScheme(scheme: ProtectionRelayScheme?): Boolean {
        val ret = _schemes.safeRemove(scheme)
        if (_schemes.isNullOrEmpty()) _schemes = null
        return ret
    }

    /**
     * Disassociate all [ProtectionRelayScheme]s from this [ProtectionRelayFunction].
     *
     * @return A reference to this [ProtectionRelayFunction] for fluent use.
     */
    fun clearSchemes(): ProtectionRelayFunction {
        _schemes = null
        return this
    }

}
