/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assetinfo

import com.zepben.ewb.cim.iec61968.assets.AssetInfo
import com.zepben.ewb.cim.iec61970.base.wires.TransformerStarImpedance
import com.zepben.ewb.cim.iec61970.base.wires.WindingConnection
import com.zepben.ewb.services.network.ResistanceReactance
import com.zepben.ewb.services.network.mergeIfIncomplete
import kotlin.math.round
import kotlin.math.sqrt

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
class TransformerEndInfo(mRID: String) : AssetInfo(mRID) {

    var connectionKind: WindingConnection = WindingConnection.UNKNOWN
    var emergencyS: Int? = null
    var endNumber: Int = 0
    var insulationU: Int? = null
    var phaseAngleClock: Int? = null
    var r: Double? = null
    var ratedS: Int? = null
    var ratedU: Int? = null
    var shortTermS: Int? = null

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

    private fun round2dp(value: Double): Double =
        round(value * 100) / 100

    internal fun calculateResistanceReactanceFromTests(): ResistanceReactance? {
        // NOTE: The conversion to doubles below is to stop int overflow in the following maths.
        val rU = ratedU?.toDouble() ?: return null
        val rS = ratedS?.toDouble() ?: return null

        fun calculateRXFromTest(shortCircuitTest: ShortCircuitTest?): Pair<Double?, Double?> = shortCircuitTest?.run {
            val amps = current ?: (rS / rU)

            val r = voltageOhmicPart?.let {
                // active voltage = copper loss (i.e. wattmeter reading) / current (i.e. ammeter reading)
                // R = copper loss / current^2 = active voltage / current
                val activeVoltage = (it / 100) * rU
                activeVoltage / amps
            } ?: loss?.let {
                // R = copper loss / current^2
                it / (amps * amps)
            } ?: return Pair(null, null)

            val roundedX = voltage?.let { (it / 100) * rU }?.let { volts ->
                val zMag = volts / amps
                val x = sqrt((zMag * zMag) - (r * r))
                round2dp(x)
            }

            Pair(round2dp(r), roundedX)
        } ?: Pair(null, null)

        val (r, x) = calculateRXFromTest(energisedEndShortCircuitTests)
        val (r0, x0) = calculateRXFromTest(groundedEndShortCircuitTests)

        return ResistanceReactance(r, x, r0, x0).takeUnless { it.isEmpty() }
    }

}
