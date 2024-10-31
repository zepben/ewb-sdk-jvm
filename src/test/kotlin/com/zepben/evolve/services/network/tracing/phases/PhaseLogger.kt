/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.run
import com.zepben.evolve.services.network.tracing.traversal.StepAction
import com.zepben.evolve.services.network.tracing.traversal.StepContext
import org.slf4j.LoggerFactory

// Logs all the phases of assets, terminals and nominal phases. Useful for debugging.
internal class PhaseLogger private constructor(asset: ConductingEquipment) : StepAction<NetworkTraceStep<Unit>> {

    private val b: StringBuilder = StringBuilder()
        .append("\n###############################")
        .append("\nTracing phases from: ${asset.typeNameAndMRID()}")
        .append("\n")
        .append("\n")

    override fun apply(item: NetworkTraceStep<Unit>, context: StepContext) {
        item.path.toEquipment.terminals.forEach { t ->
            b.append("${item.path.toEquipment.mRID}-T${t.sequenceNumber}: ")

            t.phases.singlePhases.forEach { phase ->
                val nps = t.normalPhases[phase]
                val cps = t.currentPhases[phase]

                b.append("{$phase: n:$nps, c:$cps}, ")
            }

            clearLastComma(b)
            b.append("\n")
        }
    }

    private fun clearLastComma(b: StringBuilder) {
        val index = b.lastIndexOf(",")
        if (index != -1)
            b.delete(index, b.length)
    }

    private fun log() {
        logger.info(b.toString())
    }

    companion object {

        private val logger = LoggerFactory.getLogger(PhaseLogger::class.java)

        fun trace(asset: ConductingEquipment?) {
            trace(listOf(asset!!))
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun trace(assets: Collection<ConductingEquipment?>) {
            assets.forEach { asset ->
                val pl = PhaseLogger(asset!!)

                Tracing.equipmentNetworkTrace()
                    .addStepAction(pl)
                    .run(asset)

                pl.log()
            }
        }

    }

}
