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
import com.zepben.evolve.services.network.tracing.Tracing
import org.slf4j.LoggerFactory

// Logs all the phases of assets, terminal and cores. Useful for debugging.
internal class PhaseLogger private constructor(asset: ConductingEquipment) : (ConductingEquipment, Boolean?) -> Unit {

    private val b: StringBuilder = StringBuilder()
        .append("\n###############################")
        .append("\nTracing from: ${asset.typeNameAndMRID()}")
        .append("\n")
        .append("\n")

    override fun invoke(a: ConductingEquipment, isStopping: Boolean?) {
        a.terminals.forEach { t ->
            b.append("${a.mRID}-T${t.sequenceNumber}: ")

            t.phases.singlePhases().forEach { phase ->
                val nps = t.normalPhases(phase)
                val cps = t.currentPhases(phase)

                b.append("{$phase: n:${nps.phase}:${nps.direction}, c:${cps.phase}:${cps.direction}}, ")
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

                Tracing.connectedEquipmentTrace()
                    .addStepAction(pl)
                    .run(asset)

                pl.log()
            }
        }

    }

}
