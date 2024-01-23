/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.cim.iec61970.base.core.PowerSystemResource
import com.zepben.evolve.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.evolve.cim.iec61970.infiec61970.protection.PowerDirectionKind
import com.zepben.evolve.cim.iec61970.infiec61970.protection.ProtectionKind
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.getByMRID
import com.zepben.evolve.services.common.extensions.safeRemove
import com.zepben.evolve.services.common.extensions.validateReference

/**
 * A function that a relay implements to protect equipment.
 *
 * @property relayTimeLimitTime The time timeLimit from detection of abnormal conditions to relay operation in seconds.
 * @property protectionKind The kind of protection being provided by this ProtectionRelayFunction.
 * @property directable Whether this ProtectionRelayFunction responds to power flow in a given direction.
 * @property powerDirection The flow of power direction used by this ProtectionRelayFunction.
 *
 */
abstract class ProtectionRelayFunction(mRID: String = "") : PowerSystemResource(mRID) {

    var model: String? = null
    var reclosing: Boolean? = null
    val timeLimits: List<Double> get() = _timeLimits.asUnmodifiable()
    var relayDelayTime: Double? = null
    var protectionKind: ProtectionKind = ProtectionKind.UNKNOWN
    var directable: Boolean? = null
    var powerDirection: PowerDirectionKind = PowerDirectionKind.UNKNOWN_DIRECTION
    
    private var _timeLimits: MutableList<Double>? = null
    private var _protectedSwitches: MutableList<ProtectedSwitch>? = null

    /**
     * Returns the number of reclose timeLimits for this [ProtectionRelayFunction]
     */
    fun numTimeLimits(): Int = _timeLimits?.size ?: 0

    /**
     * Add a reclose time limit
     * @param timeLimit The time limit in seconds to add.
     * @param index The index into the list to add the timeLimit at. Defaults to the end of the list.
     * @return This [ProtectionRelayFunction] for fluent use.
     */
    fun addTimeLimit(
        timeLimit: Double,
        index: Int = numTimeLimits()
    ): ProtectionRelayFunction {
        _timeLimits = _timeLimits ?: mutableListOf()
        _timeLimits!!.add(index, timeLimit)

        return this
    }

    /**
     * Add reclose time limits
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
     * @param index The index of the timeLimit to remove.
     * @return The time limit that was removed, or null if no timeLimit was present at [index].
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

}
