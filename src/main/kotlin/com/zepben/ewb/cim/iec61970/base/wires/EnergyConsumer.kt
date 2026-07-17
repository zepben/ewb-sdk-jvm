/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.boilerplate.LazyMridList
import com.zepben.ewb.boilerplate.MridCollection

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
class EnergyConsumer(mRID: String) : EnergyConnection(mRID) {

    private var _energyConsumerPhases: MutableList<EnergyConsumerPhase>? = null
    var customerCount: Int? = null
    var grounded: Boolean? = null
    var p: Double? = null
    var pFixed: Double? = null
    var phaseConnection: PhaseShuntConnectionKind = PhaseShuntConnectionKind.D
    var q: Double? = null
    var qFixed: Double? = null

    /**
     * The individual phase models for this energy consumer. The returned collection is read only.
     */
    val phases: MridCollection<EnergyConsumerPhase> get() = LazyMridList(
        getter = { _energyConsumerPhases },
        setter = { _energyConsumerPhases = it },
        owner = this,
        elementDescription = "An EnergyConsumerPhase",
        validate = { validatePhase(it) }
    )

    private fun validatePhase(phase: EnergyConsumerPhase) {
        if (phase.energyConsumer == null)
            phase.energyConsumer = this

        require(phase.energyConsumer === this) {
            "${phase.typeNameAndMRID()} `energyConsumer` property references ${phase.energyConsumer!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }
    }

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region phases boilerplate

    @Deprecated(
        message = "Use phases.size instead.",
        replaceWith = ReplaceWith("phases.size")
    )
    fun numPhases(): Int = phases.size

    @Deprecated(
        message = "Use phases.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("phases.getByMRID(mRID)")
    )
    fun getPhase(mRID: String): EnergyConsumerPhase? = phases.getByMrid(mRID)

    @Deprecated(
        message = "Use phases.add(phase) instead.",
        replaceWith = ReplaceWith("also { it.phases.add(phase) }")
    )
    fun addPhase(phase: EnergyConsumerPhase): EnergyConsumer {
        phases.add(phase)
        return this
    }

    @Deprecated(
        message = "Use phases.remove(phase) instead.",
        replaceWith = ReplaceWith("phases.remove(phase)")
    )
    fun removePhase(phase: EnergyConsumerPhase): Boolean = phases.remove(phase)

    @Deprecated(
        message = "Use phases.clear() instead.",
        replaceWith = ReplaceWith("phases.clear()")
    )
    fun clearPhases(): EnergyConsumer {
        phases.clear()
        return this
    }

    // endregion

    // endregion
}
