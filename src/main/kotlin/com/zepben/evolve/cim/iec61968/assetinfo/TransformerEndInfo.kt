/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.assetinfo

import com.zepben.evolve.cim.iec61968.assets.AssetInfo
import com.zepben.evolve.cim.iec61970.base.wires.TransformerStarImpedance
import com.zepben.evolve.cim.iec61970.base.wires.WindingConnection
import com.zepben.evolve.services.network.ResistanceReactance
import com.zepben.evolve.services.network.mergeIfIncomplete

/**
 * Transformer end data.
 *
 * @property connectionKind Kind of connection.
 * @property emergencyS Apparent power that the winding can carry under emergency conditions (also called long-term emergency power) in volt amperes (VA).
 * @property endNumber Number for this transformer end, corresponding to the end's order in the PowerTransformer.vectorGroup attribute. Highest voltage
 *                     winding should be 1.
 * @property insulationU Basic insulation level voltage rating in volts (V).
 * @property phaseAngleClock Winding phase angle where 360 degrees are represented with clock hours, so the valid values are {0, ..., 11}. For example,
 *                           to express the second winding in code 'Dyn11', set attributes as follows: 'endNumber'=2, 'connectionKind' = Yn and
 *                           'phaseAngleClock' = 11.
 * @property r DC resistance in ohms.
 * @property ratedS Normal apparent power rating in volt amperes (VA).
 * @property ratedU Rated voltage: phase-phase for three-phase windings, and either phase-phase or phase-neutral for single-phase windings in volts (V).
 * @property shortTermS Apparent power that this winding can carry for a short period of time (in emergency) in volt amperes (VA).
 * @property transformerTankInfo Transformer tank data that this end description is part of.
 * @property transformerStarImpedance Transformer star impedance calculated from this transformer end datasheet.
 * @property energisedEndNoLoadTests All no-load test measurements in which this transformer end was energised.
 * @property energisedEndShortCircuitTests All short-circuit test measurements in which this transformer end was short-circuited.
 * @property groundedEndShortCircuitTests All short-circuit test measurements in which this transformer end was energised.
 * @property openEndOpenCircuitTests All open-circuit test measurements in which this transformer end was not excited.
 * @property energisedEndOpenCircuitTests All open-circuit test measurements in which this transformer end was excited.

 */
class TransformerEndInfo(mRID: String = "") : AssetInfo(mRID) {

    var connectionKind: WindingConnection = WindingConnection.UNKNOWN_WINDING
    var emergencyS: Int = 0
    var endNumber: Int = 0
    var insulationU: Int = 0
    var phaseAngleClock: Int = 0
    var r: Double = 0.0
    var ratedS: Int = 0
    var ratedU: Int = 0
    var shortTermS: Int = 0

    var transformerTankInfo: TransformerTankInfo? = null
    var transformerStarImpedance: TransformerStarImpedance? = null
    var energisedEndNoLoadTests: NoLoadTest? = null
    var energisedEndShortCircuitTests: ShortCircuitTest? = null
    var groundedEndShortCircuitTests: ShortCircuitTest? = null
    var openEndOpenCircuitTests: OpenCircuitTest? = null
    var energisedEndOpenCircuitTests: OpenCircuitTest? = null

    /**
     * Get the [ResistanceReactance] for this [TransformerEndInfo] from either the pre-calculated [transformerStarImpedance] or
     * calculated from the associated test data.
     */
    fun resistanceReactance(): ResistanceReactance? =
        transformerStarImpedance?.resistanceReactance()?.mergeIfIncomplete {
            calculateResistanceReactanceFromTests()
        } ?: calculateResistanceReactanceFromTests()

    // https://app.clickup.com/t/6929263/EWB-772
    @Suppress("RedundantNullableReturnType")
    internal fun calculateResistanceReactanceFromTests(): ResistanceReactance? =
        null

}
