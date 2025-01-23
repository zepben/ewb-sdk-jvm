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
class FindSwerEquipment {

    private fun createTrace(stateOperators: NetworkStateOperators) = Tracing.networkTrace(stateOperators).addCondition { stopAtOpen() }

    /**
     * Find the [ConductingEquipment] on any [Feeder] in a [NetworkService] which is SWER. This will include any equipment on the LV network that is energised
     * via SWER.
     *
     * @param networkService The [NetworkService] to process.
     * @param networkStateOperators The [NetworkStateOperators] to be used when finding SWER equipment.
     *
     * @return A [Set] of [ConductingEquipment] on any [Feeder] in [networkService] that is SWER, or energised via SWER.
     */
    @JvmOverloads
    fun find(networkService: NetworkService, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL): Set<ConductingEquipment> =
        networkService.sequenceOf<Feeder>()
            .flatMap { find(it, networkStateOperators) }
            .toSet()

    /**
     * Find the [ConductingEquipment] on a [Feeder] which is SWER. This will include any equipment on the LV network that is energised via SWER.
     *
     * @param feeder The [Feeder] to process.
     * @param networkStateOperators The [NetworkStateOperators] to be used when finding SWER equipment.
     *
     * @return A [Set] of [ConductingEquipment] on [feeder] that is SWER, or energised via SWER.
     */
    fun find(feeder: Feeder, networkStateOperators: NetworkStateOperators = NetworkStateOperators.NORMAL): Set<ConductingEquipment> {
        val swerEquipment = mutableSetOf<ConductingEquipment>()

        // We will add all the SWER transformers to the swerEquipment list before starting any traces to prevent tracing though them by accident. In
        // order to do this, we collect the sequence to a list to change the iteration order.
        networkStateOperators.getEquipment(feeder)
            .asSequence()
            .filterIsInstance<PowerTransformer>()
            .filter { it.hasSwerTerminal }
            .filter { it.hasNonSwerTerminal }
            .toList()
            .onEach { swerEquipment.add(it) }
            .forEach { traceFrom(networkStateOperators, it, swerEquipment) }

        return swerEquipment.toSet()
    }

    private fun traceFrom(stateOperators: NetworkStateOperators, transformer: PowerTransformer, swerEquipment: MutableSet<ConductingEquipment>) {
        // Trace from any SWER terminals.
        traceSwerFrom(stateOperators, transformer, swerEquipment)

        // Trace from any LV terminals.
        traceLvFrom(stateOperators, transformer, swerEquipment)
    }

    private fun traceSwerFrom(stateOperators: NetworkStateOperators, transformer: PowerTransformer, swerEquipment: MutableSet<ConductingEquipment>) {
        val trace = createTrace(stateOperators).apply {
            addQueueCondition { nextStep, _, _, _ ->
                when {
                    nextStep.path.toTerminal.isSwerTerminal || nextStep.path.toEquipment is Switch -> nextStep.path.toEquipment !in swerEquipment
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

    private fun traceLvFrom(stateOperators: NetworkStateOperators, transformer: PowerTransformer, swerEquipment: MutableSet<ConductingEquipment>) {
        val trace = createTrace(stateOperators)
            .addQueueCondition { nextStep, _, _, _ ->
                when {
                    nextStep.path.toEquipment.baseVoltageValue in 1..1000 -> nextStep.path.toEquipment !in swerEquipment
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
