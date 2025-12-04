/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * A generic equivalent for an energy supplier on a transmission or distribution voltage level.
 *
 * @property activePower High voltage source active injection. Load sign convention is used, i.e. positive sign means flow out from a node.
 * Starting value for steady state solutions.
 * @property reactivePower High voltage source reactive injection. Load sign convention is used, i.e. positive sign means flow out from a node.
 * Starting value for steady state solutions.
 * @property voltageAngle Phase angle of a-phase open circuit used when voltage characteristics need to be imposed at the node associated with
 *                        the terminal of the energy source, such as when voltages and angles from the transmission level are used as input to
 *                        the distribution network. The attribute shall be a positive value or zero.
 * @property voltageMagnitude Phase-to-phase open circuit voltage magnitude used when voltage characteristics need to be imposed at the node
 *                            associated with the terminal of the energy source, such as when voltages and angles from the transmission level
 *                            are used as input to the distribution network. The attribute shall be a positive value or zero.
 * @property pMax This is the maximum active power that can be produced by the source. Load sign convention is used, i.e. positive sign means flow out from a
 *                TopologicalNode (bus) into the conducting equipment.
 * @property pMin This is the minimum active power that can be produced by the source. Load sign convention is used, i.e. positive sign means flow out from a
 *                TopologicalNode (bus) into the conducting equipment.
 * @property r Positive sequence Thevenin resistance.
 * @property r0 Zero sequence Thevenin resistance.
 * @property rn Negative sequence Thevenin resistance.
 * @property x Positive sequence Thevenin reactance.
 * @property x0 Zero sequence Thevenin reactance.
 * @property xn Negative sequence Thevenin reactance.
 * @property isExternalGrid True if this energy source represents the higher-level power grid connection to an external grid that normally is modelled as the
 *                          slack bus for power flow calculations.
 * @property rMin Minimum positive sequence Thevenin resistance.
 * @property rnMin Minimum negative sequence Thevenin resistance
 * @property r0Min Minimum zero sequence Thevenin resistance.
 * @property xMin Minimum positive sequence Thevenin reactance.
 * @property xnMin Minimum negative sequence Thevenin reactance.
 * @property x0Min Minimum zero sequence Thevenin reactance.
 * @property rMax Maximum positive sequence Thevenin resistance.
 * @property rnMax Maximum negative sequence Thevenin resistance.
 * @property r0Max Maximum zero sequence Thevenin resistance.
 * @property xMax Maximum positive sequence Thevenin reactance.
 * @property xnMax Maximum negative sequence Thevenin resistance.
 * @property x0Max Maximum zero sequence Thevenin reactance.

 */
class EnergySource(mRID: String) : EnergyConnection(mRID) {

    private var _energySourcePhases: MutableList<EnergySourcePhase>? = null
    var activePower: Double? = null
    var reactivePower: Double? = null
    var voltageAngle: Double? = null
    var voltageMagnitude: Double? = null
    var pMax: Double? = null
    var pMin: Double? = null
    var r: Double? = null
    var r0: Double? = null
    var rn: Double? = null
    var x: Double? = null
    var x0: Double? = null
    var xn: Double? = null
    var isExternalGrid: Boolean? = null
    var rMin: Double? = null
    var rnMin: Double? = null
    var r0Min: Double? = null
    var xMin: Double? = null
    var xnMin: Double? = null
    var x0Min: Double? = null
    var rMax: Double? = null
    var rnMax: Double? = null
    var r0Max: Double? = null
    var xMax: Double? = null
    var xnMax: Double? = null
    var x0Max: Double? = null

    /**
     * The phases for this energy source. The returned collection is read only.
     */
    val phases: Collection<EnergySourcePhase> get() = _energySourcePhases.asUnmodifiable()

    /**
     * Get the number of entries in the [EnergySourcePhase] collection.
     */
    fun numPhases(): Int = _energySourcePhases?.size ?: 0

    /**
     * The individual phase information of the energy source.
     *
     * @param mRID the mRID of the required [EnergySourcePhase]
     * @return The [EnergySourcePhase] with the specified [mRID] if it exists, otherwise null
     */
    fun getPhase(mRID: String): EnergySourcePhase? = _energySourcePhases?.getByMRID(mRID)

    /**
     * Add an [EnergySourcePhase] to this [EnergySource].
     *
     * @param [phase] The [EnergySourcePhase] to add.
     * @return This [EnergySource] for fluent use.
     */
    fun addPhase(phase: EnergySourcePhase): EnergySource {
        if (validateReference(phase, ::getPhase, "An EnergySourcePhase"))
            return this

        if (phase.energySource == null)
            phase.energySource = this

        require(phase.energySource === this) {
            "${phase.typeNameAndMRID()} `energySource` property references ${phase.energySource!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }

        _energySourcePhases = _energySourcePhases ?: mutableListOf()
        _energySourcePhases!!.add(phase)

        return this
    }

    /**
     * Remove an [EnergySourcePhase] from this [EnergySource].
     *
     * @param [phase] The [EnergySourcePhase] to remove.
     * @return true if [[phase]] is removed from the collection.
     */
    fun removePhase(phase: EnergySourcePhase): Boolean {
        val ret = _energySourcePhases?.remove(phase) == true
        if (_energySourcePhases.isNullOrEmpty()) _energySourcePhases = null
        return ret
    }

    /**
     * Clear all [EnergySourcePhase]'s from this [EnergySource].
     *
     * @return This [EnergySource] for fluent use.
     */
    fun clearPhases(): EnergySource {
        _energySourcePhases = null
        return this
    }

}
