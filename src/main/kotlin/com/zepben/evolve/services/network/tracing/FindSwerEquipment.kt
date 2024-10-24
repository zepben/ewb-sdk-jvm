/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.cim.iec61970.base.wires.Switch
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.networktrace.run

/**
 * A class which can be used for finding the SWER equipment in a [NetworkService] or [Feeder].
 */
class FindSwerEquipment(
    val stateOperators: NetworkStateOperators,
) {

    private val createTrace = { Tracing.connectedEquipmentTrace(stateOperators).addNetworkCondition { stopAtOpen() } }

    /**
     * Find the [ConductingEquipment] on any [Feeder] in a [NetworkService] which is SWER. This will include any equipment on the LV network that is energised
     * via SWER.
     *
     * @param networkService The [NetworkService] to process.
     *
     * @return A [Set] of [ConductingEquipment] on any [Feeder] in [networkService] that is SWER, or energised via SWER.
     */
    fun find(networkService: NetworkService): Set<ConductingEquipment> =
        networkService.sequenceOf<Feeder>()
            .flatMap { find(it) }
            .toSet()

    /**
     * Find the [ConductingEquipment] on a [Feeder] which is SWER. This will include any equipment on the LV network that is energised via SWER.
     *
     * @param feeder The [Feeder] to process.
     *
     * @return A [Set] of [ConductingEquipment] on [feeder] that is SWER, or energised via SWER.
     */
    fun find(feeder: Feeder): Set<ConductingEquipment> {
        val swerEquipment = mutableSetOf<ConductingEquipment>()

        // We will add all the SWER transformers to the swerEquipment list before starting any traces to prevent tracing though them by accident. In
        // order to do this, we collect the sequence to a list to change the iteration order.
        stateOperators.getEquipment(feeder)
            .asSequence()
            .filterIsInstance<PowerTransformer>()
            .filter { it.hasSwerTerminal }
            .filter { it.hasNonSwerTerminal }
            .toList()
            .onEach { swerEquipment.add(it) }
            .forEach { traceFrom(it, swerEquipment) }

        return swerEquipment.toSet()
    }

    private fun traceFrom(transformer: PowerTransformer, swerEquipment: MutableSet<ConductingEquipment>) {
        // Trace from any SWER terminals.
        traceSwerFrom(transformer, swerEquipment)

        // Trace from any LV terminals.
        traceLvFrom(transformer, swerEquipment)
    }

    private fun traceSwerFrom(transformer: PowerTransformer, swerEquipment: MutableSet<ConductingEquipment>) {
        val trace = createTrace().apply {
            // Because queue conditions are called for all terminals, even if we only step on equipment we always want to continue on an internal trace
            addQueueCondition { step, _ ->
                when {
                    // TODO [Review]: Because this is a NetworkTrace with onlyActionEquipment = true, step actions only happen once for each equipment.
                    //                However queue conditions run for every queued terminal step and now we need a special check for tracedInternally which feels
                    //                a bit clunky. Need to have a think about this...
                    step.path.tracedInternally -> true
                    step.path.toTerminal.isSwerTerminal || step.path.toEquipment is Switch -> step.path.toEquipment !in swerEquipment
                    else -> false
                }
            }

            addStepAction { step, _ -> swerEquipment.add(step.path.toEquipment) }
        }

        transformer.terminals
            .asSequence()
            .filter { it.isSwerTerminal }
            .forEach {
                trace.reset()
                trace.run(it)
            }
    }

    private fun traceLvFrom(transformer: PowerTransformer, swerEquipment: MutableSet<ConductingEquipment>) {
        val trace = createTrace()
            .addQueueCondition { step, _ ->
                when {
                    step.path.tracedInternally -> true
                    step.path.toEquipment.baseVoltageValue in 1..1000 -> step.path.toEquipment !in swerEquipment
                    else -> false
                }
            }
            .addStepAction { step, _ -> swerEquipment.add(step.path.toEquipment) }


        transformer.terminals
            .asSequence()
            .filter { it.isNonSwerTerminal }
            .forEach {
                trace.reset()
                trace.run(it)
            }
    }

    private val Terminal.isSwerTerminal: Boolean get() = phases.numPhases() == 1
    private val Terminal.isNonSwerTerminal: Boolean get() = phases.numPhases() > 1
    private val ConductingEquipment.hasSwerTerminal: Boolean get() = terminals.any { it.isSwerTerminal }
    private val ConductingEquipment.hasNonSwerTerminal: Boolean get() = terminals.any { it.isNonSwerTerminal }

}
