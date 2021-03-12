/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network

import com.zepben.evolve.cim.iec61968.assetinfo.PowerTransformerInfo
import com.zepben.evolve.cim.iec61968.assetinfo.TransformerEndInfo
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformerEnd
import com.zepben.evolve.cim.iec61970.base.wires.TransformerStarImpedance

/**
 * @property r Positive sequence series resistance.
 * @property r0 Zero sequence series resistance.
 * @property x Positive sequence series reactance.
 * @property x0 Zero sequence series reactance.
 */
data class ResistanceReactance(
    val r: Double,
    val r0: Double,
    val x: Double,
    val x0: Double
) {

    fun isComplete(): Boolean = !r.isNaN() && !r0.isNaN() && !x.isNaN() && !x0.isNaN()

}

private fun ResistanceReactance?.mergeIfIncomplete(toMerge: () -> ResistanceReactance?): ResistanceReactance? {
    return when {
        this == null -> toMerge()
        isComplete() -> this
        else -> toMerge()?.let { rr ->
            ResistanceReactance(
                r.takeIf { !it.isNaN() } ?: rr.r,
                r0.takeIf { !it.isNaN() } ?: rr.r0,
                x.takeIf { !it.isNaN() } ?: rr.x,
                x0.takeIf { !it.isNaN() } ?: rr.x0
            )
        } ?: this
    }
}

fun PowerTransformerEnd?.resistanceReactance(): ResistanceReactance =
    this?.let {
        ResistanceReactance(r, r0, x, x0).mergeIfIncomplete {
            starImpedance?.resistanceReactance()
        }.mergeIfIncomplete {
            powerTransformer?.assetInfo?.resistanceReactance(endNumber)
        }
    } ?: ResistanceReactance(Double.NaN, Double.NaN, Double.NaN, Double.NaN)

fun TransformerStarImpedance?.resistanceReactance(): ResistanceReactance? =
    this?.let { ResistanceReactance(it.r, it.r0, it.x, it.x0) }

fun PowerTransformerInfo?.resistanceReactance(endNumber: Int): ResistanceReactance? =
    this?.transformerTankInfos?.flatMap { it.transformerEndInfos }?.firstOrNull { it.endNumber == endNumber }?.resistanceReactance()

fun TransformerEndInfo?.resistanceReactance(): ResistanceReactance? =
    this?.let {
        transformerStarImpedance?.resistanceReactance().mergeIfIncomplete {
            // https://app.clickup.com/t/6929263/EWB-615 Calculate from test data. This should be moved to another extension.
            ResistanceReactance(Double.NaN, Double.NaN, Double.NaN, Double.NaN)
        }
    }
