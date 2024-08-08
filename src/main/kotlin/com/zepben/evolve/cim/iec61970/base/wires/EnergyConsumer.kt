/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.getByMRID
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.common.extensions.validateReference

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
    var customerCount: Int? = null
    var grounded: Boolean = false
    var p: Double? = null
    var pFixed: Double? = null
    var phaseConnection: PhaseShuntConnectionKind = PhaseShuntConnectionKind.D
    var q: Double? = null
    var qFixed: Double? = null

    /**
     * The individual phase models for this energy consumer. The returned collection is read only.
     */
    val phases: Collection<EnergyConsumerPhase> get() = _energyConsumerPhases.asUnmodifiable()

    /**
     * Get the number of entries in the [EnergyConsumerPhase] collection.
     */
    fun numPhases(): Int = _energyConsumerPhases?.size ?: 0

    /**
     * The individual phase models for this energy consumer.
     *
     * @param mRID the mRID of the required [EnergyConsumerPhase]
     * @return The [EnergyConsumerPhase] with the specified [mRID] if it exists, otherwise null
     */
    fun getPhase(mRID: String): EnergyConsumerPhase? = _energyConsumerPhases?.getByMRID(mRID)

    fun addPhase(phase: EnergyConsumerPhase): EnergyConsumer {
        if (validateReference(phase, ::getPhase, "An EnergyConsumerPhase"))
            return this

        if (phase.energyConsumer == null)
            phase.energyConsumer = this

        require(phase.energyConsumer === this) {
            "${phase.typeNameAndMRID()} `energyConsumer` property references ${phase.energyConsumer!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }

        _energyConsumerPhases = _energyConsumerPhases ?: mutableListOf()
        _energyConsumerPhases!!.add(phase)

        return this
    }

    fun removePhase(phase: EnergyConsumerPhase): Boolean {
        val ret = _energyConsumerPhases?.remove(phase) == true
        if (_energyConsumerPhases.isNullOrEmpty()) _energyConsumerPhases = null
        return ret
    }

    fun clearPhases(): EnergyConsumer {
        _energyConsumerPhases = null
        return this
    }
}
