/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.services.network.ResistanceReactance
import com.zepben.evolve.services.network.mergeIfIncomplete

/**
 * A PowerTransformerEnd is associated with each Terminal of a PowerTransformer.
 *
 *
 * The impedance values r, r0, x, and x0 of a PowerTransformerEnd represents a star equivalent as follows
 *
 *
 * 1) for a two Terminal PowerTransformer the high voltage (TransformerEnd.endNumber=1) PowerTransformerEnd has non zero values on
 * r, r0, x, and x0 while the low voltage (TransformerEnd.endNumber=0) PowerTransformerEnd has zero values for r, r0, x, and x0.
 * 2) for a three Terminal PowerTransformer the three PowerTransformerEnds represents a star equivalent with each leg in the star
 * represented by r, r0, x, and x0 values.
 * 3) For a three Terminal transformer each PowerTransformerEnd shall have g, g0, b and b0 values corresponding the no load losses
 * distributed on the three PowerTransformerEnds. The total no load loss shunt impedances may also be placed at one of the
 * PowerTransformerEnds, preferably the end numbered 1, having the shunt values on end 1 is the preferred way.
 * 4) for a PowerTransformer with more than three Terminals the PowerTransformerEnd impedance values cannot be used. Instead use
 * the TransformerMeshImpedance or split the transformer into multiple PowerTransformers.
 *
 * @property powerTransformer The power transformer of this power transformer end.
 * @property b Magnetizing branch susceptance (B mag).  The value can be positive or negative.
 * @property b0 Zero sequence magnetizing branch susceptance.
 * @property connectionKind Kind of connection.
 * @property g Magnetizing branch conductance.
 * @property g0 Zero sequence magnetizing branch conductance (star-model).
 * @property phaseAngleClock Terminal voltage phase angle displacement where 360 degrees are represented with clock hours. The valid values
 *                           are 0 to 11. For example, for the secondary side end of a transformer with vector group code of 'Dyn11', specify the
 *                           connection kind as wye with neutral and specify the phase angle of the clock as 11.  The clock value of the transformer
 *                           end number specified as 1, is assumed to be zero.
 * @property r Resistance (star-model) of the transformer end in ohms. The attribute shall be equal or greater than zero for non-equivalent transformers.
 *             Do not read this directly, use [resistanceReactance().r] instead.
 * @property r0 Zero sequence series resistance (star-model) of the transformer end in ohms. Do not read this directly, use [resistanceReactance().r0] instead.
 * @property ratedS Normal apparent power rating. The attribute shall be a positive value. For a two-winding transformer the values for the high and low voltage
 *                  sides shall be identical.
 * @property ratedU  Rated voltage: phase-phase for three-phase windings, and either phase-phase or phase-neutral for single-phase windings.
 *                   A high voltage side, as given by TransformerEnd.endNumber, shall have a ratedU that is greater or equal than ratedU
 *                   for the lower voltage sides.
 * @property x Positive sequence series reactance (star-model) of the transformer end in ohms. Do not read this directly, use [resistanceReactance().x] instead.
 * @property x0 Zero sequence series reactance of the transformer end in ohms. Do not read this directly, use [resistanceReactance().x0] instead.
 */
class PowerTransformerEnd @JvmOverloads constructor(mRID: String = "") : TransformerEnd(mRID) {

    var powerTransformer: PowerTransformer? = null
        set(value) {
            field =
                if (field == null || field === value) value else throw IllegalStateException("powerTransformer has already been set to $field. Cannot set this field again")
        }

    var b: Double? = null
    var b0: Double? = null
    var connectionKind: WindingConnection = WindingConnection.UNKNOWN_WINDING
    var g: Double? = null
    var g0: Double? = null
    var phaseAngleClock: Int? = null
    var r: Double? = null
    var r0: Double? = null
    var ratedS: Int? = null
    var ratedU: Int? = null
    var x: Double? = null
    var x0: Double? = null

    /**
     * Get the [ResistanceReactance] for this [PowerTransformerEnd] from either:
     * 1. directly assigned values or
     * 2. the pre-calculated [starImpedance] or
     * 3. from the datasheet information of the associated [powerTransformer]
     *
     * If the data is not complete in any of the above it will merge in the missing values from the subsequent sources.
     */
    override fun resistanceReactance(): ResistanceReactance =
        ResistanceReactance(r, r0, x, x0).mergeIfIncomplete {
            starImpedance?.resistanceReactance()
        }.mergeIfIncomplete {
            powerTransformer?.assetInfo?.resistanceReactance(endNumber)
        }

}
