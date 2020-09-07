/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.cim.iec61970.base.wires

import com.zepben.cimbend.common.extensions.asUnmodifiable
import com.zepben.cimbend.common.extensions.getByMRID
import com.zepben.cimbend.common.extensions.validateReference

/**
 * Generic user of energy - a  point of consumption on the power system model.
 *
 * @property customerCount Number of individual customers represented by this demand.
 * @property grounded Used for Yn and Zn connections. True if the neutral is solidly grounded.
 * @property p Active power of the load. Load sign convention is used, i.e. positive sign means flow out from a node
 *             For voltage dependent loads the value is at rated voltage. Starting value for a steady state solution.
 * @property pFixed Active power of the load that is a fixed quantity. Load sign convention is used, i.e. positive sign means flow out from a node.
 * @property phaseConnection The type of phase connection, such as wye or delta.
 * @property q Reactive power of the load. Load sign convention is used, i.e. positive sign means flow out from a node.
 *             For voltage dependent loads the value is at rated voltage. Starting value for a steady state solution.
 * @property qFixed power of the load that is a fixed quantity. Load sign convention is used, i.e. positive sign means flow out from a node.
 */
class EnergyConsumer @JvmOverloads constructor(mRID: String = "") : EnergyConnection(mRID) {

    private var _energyConsumerPhases: MutableList<EnergyConsumerPhase>? = null
    var customerCount: Int = 0
    var grounded: Boolean = false
    var p: Double = 0.0
    var pFixed: Double = 0.0
    var phaseConnection: PhaseShuntConnectionKind = PhaseShuntConnectionKind.D
    var q: Double = 0.0
    var qFixed: Double = 0.0

    /**
     * The individual phase models for this energy consumer. The returned collection is read only.
     */
    val phases: Collection<EnergyConsumerPhase> get() = _energyConsumerPhases.asUnmodifiable()

    /**
     * Get the number of entries in the [EnergyConsumerPhase] collection.
     */
    fun numPhases() = _energyConsumerPhases?.size ?: 0

    /**
     * The individual phase models for this energy consumer.
     *
     * @param mRID the mRID of the required [EnergyConsumerPhase]
     * @return The [EnergyConsumerPhase] with the specified [mRID] if it exists, otherwise null
     */
    fun getPhase(mRID: String) = _energyConsumerPhases?.getByMRID(mRID)

    fun addPhase(phase: EnergyConsumerPhase): EnergyConsumer {
        if (validateReference(phase, ::getPhase, "An EnergyConsumerPhase"))
            return this

        _energyConsumerPhases = _energyConsumerPhases ?: mutableListOf()
        _energyConsumerPhases!!.add(phase)

        return this
    }

    fun removePhase(phase: EnergyConsumerPhase?): Boolean {
        val ret = _energyConsumerPhases?.remove(phase) == true
        if (_energyConsumerPhases.isNullOrEmpty()) _energyConsumerPhases = null
        return ret
    }

    fun clearPhases(): EnergyConsumer {
        _energyConsumerPhases = null
        return this
    }
}
