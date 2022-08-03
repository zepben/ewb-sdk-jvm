/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.evolve.services.network.tracing.connectivity.ConductingEquipmentStep
import org.slf4j.LoggerFactory

// Logs all the feeder directions of assets and terminals. Useful for debugging.
internal class DirectionLogger private constructor(asset: ConductingEquipment) : (ConductingEquipmentStep, Boolean?) -> Unit {

    private val b: StringBuilder = StringBuilder()
        .append("\n###############################")
        .append("\nTracing directions from: ${asset.typeNameAndMRID()}")
        .append("\n")
        .append("\n")

    override fun invoke(a: ConductingEquipmentStep, isStopping: Boolean?) {
        a.conductingEquipment.terminals.forEach { t ->
            b.append("${a.conductingEquipment.mRID}-T${t.sequenceNumber}: ")

            val n = t.normalFeederDirection
            val c = t.currentFeederDirection

            b.append("{n:$n, c:$c}, ")

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

        private val logger = LoggerFactory.getLogger(DirectionLogger::class.java)

        fun trace(asset: ConductingEquipment?) {
            trace(listOf(asset))
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun trace(assets: Collection<ConductingEquipment?>) {
            assets.forEach { asset ->
                val pl = DirectionLogger(asset!!)

                Tracing.connectedEquipmentTrace().apply { addStepAction(pl) }
                    .run(asset)

                pl.log()
            }
        }

    }

}
