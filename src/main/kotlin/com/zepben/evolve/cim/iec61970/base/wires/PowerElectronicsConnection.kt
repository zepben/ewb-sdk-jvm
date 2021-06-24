/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsUnit
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.getByMRID
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.common.extensions.validateReference

/**
 * A connection to the AC network for energy production or consumption that uses power electronics rather than
 * rotating machines.
 *
 * @property maxIFault Maximum fault current this device will contribute, in per-unit of rated current, before the converter protection
 *                     will trip or bypass.
 * @property maxQ Maximum reactive power limit. This is the maximum (nameplate) limit for the unit.
 * @property minQ Minimum reactive power limit for the unit. This is the minimum (nameplate) limit for the unit.
 * @property p Active power injection. Load sign convention is used, i.e. positive sign means flow out from a node.
 *             Starting value for a steady state solution.
 * @property q Reactive power injection. Load sign convention is used, i.e. positive sign means flow out from a node.
 *             Starting value for a steady state solution.
 * @property ratedS Nameplate apparent power rating for the unit. The attribute shall have a positive value.
 * @property ratedU Rated voltage (nameplate data, Ur in IEC 60909-0). It is primarily used for short circuit data exchange according
 *                  to IEC 60909. The attribute shall be a positive value.
 * @property units An AC network connection may have several power electronics units connecting through it.
 * @property phases The individual units models for the power electronics connection.
 */
class PowerElectronicsConnection @JvmOverloads constructor(mRID: String = "") : RegulatingCondEq(mRID) {

    private var _powerElectronicsUnits: MutableList<PowerElectronicsUnit>? = null
    private var _powerElectronicsConnectionPhases: MutableList<PowerElectronicsConnectionPhase>? = null
    var maxIFault: Int? = null
    var maxQ: Double? = null
    var minQ: Double? = null
    var p: Double? = null
    var q: Double? = null
    var ratedS: Int? = null
    var ratedU: Int? = null

    /**
     * The units for this power electronics connection. The returned collection is read only.
     */
    val units: Collection<PowerElectronicsUnit> get() = _powerElectronicsUnits.asUnmodifiable()

    /**
     * The phases for this power electronics connection. The returned collection is read only.
     */
    val phases: Collection<PowerElectronicsConnectionPhase> get() = _powerElectronicsConnectionPhases.asUnmodifiable()


    /**
     * Get the number of entries in the [PowerElectronicsUnit] collection.
     */
    fun numUnits() = _powerElectronicsUnits?.size ?: 0

    /**
     * The individual unit information of the power electronics connection.
     *
     * @param mRID the mRID of the required [PowerElectronicsUnit]
     * @return The [PowerElectronicsUnit] with the specified [mRID] if it exists, otherwise null
     */
    fun getUnit(mRID: String) = _powerElectronicsUnits?.getByMRID(mRID)

    fun addUnit(unit: PowerElectronicsUnit): PowerElectronicsConnection {
        if (validateReference(unit, ::getUnit, "An PowerElectronicsUnit"))
            return this

        _powerElectronicsUnits = _powerElectronicsUnits ?: mutableListOf()
        _powerElectronicsUnits!!.add(unit)

        return this
    }

    fun removeUnit(unit: PowerElectronicsUnit?): Boolean {
        val ret = _powerElectronicsUnits?.remove(unit) == true
        if (_powerElectronicsUnits.isNullOrEmpty()) _powerElectronicsUnits = null
        return ret
    }

    fun clearUnits(): PowerElectronicsConnection {
        _powerElectronicsUnits = null
        return this
    }

    /**
     * Get the number of entries in the [PowerElectronicsConnectionPhase] collection.
     */
    fun numPhases() = _powerElectronicsConnectionPhases?.size ?: 0

    /**
     * The individual phase information of the power electronics connection.
     *
     * @param mRID the mRID of the required [PowerElectronicsConnectionPhase]
     * @return The [PowerElectronicsConnectionPhase] with the specified [mRID] if it exists, otherwise null
     */
    fun getPhase(mRID: String) = _powerElectronicsConnectionPhases?.getByMRID(mRID)

    fun addPhase(phase: PowerElectronicsConnectionPhase): PowerElectronicsConnection {
        if (validateReference(phase, ::getPhase, "An PowerElectronicsConnectionPhase"))
            return this

        if (phase.powerElectronicsConnection == null)
            phase.powerElectronicsConnection = this

        require(phase.powerElectronicsConnection === this) {
            "${phase.typeNameAndMRID()} `powerElectronicsConnection` property references ${phase.powerElectronicsConnection?.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }

        _powerElectronicsConnectionPhases = _powerElectronicsConnectionPhases ?: mutableListOf()
        _powerElectronicsConnectionPhases!!.add(phase)

        return this
    }

    fun removePhase(phase: PowerElectronicsConnectionPhase?): Boolean {
        val ret = _powerElectronicsConnectionPhases?.remove(phase) == true
        if (_powerElectronicsConnectionPhases.isNullOrEmpty()) _powerElectronicsConnectionPhases = null
        return ret
    }

    fun clearPhases(): PowerElectronicsConnection {
        _powerElectronicsConnectionPhases = null
        return this
    }
}
