/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.boilerplate.relations.TransformerEndRatedSList
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.extensions.iec61970.base.wires.TransformerCoolingType
import com.zepben.ewb.cim.extensions.iec61970.base.wires.TransformerEndRatedS
import com.zepben.ewb.services.network.ResistanceReactance
import com.zepben.ewb.services.network.mergeIfIncomplete

/**
 * A PowerTransformerEnd is associated with each Terminal of a PowerTransformer.
 *
 *
 * The impedance values r, r0, x, and x0 of a PowerTransformerEnd represents a star equivalent as follows
 *
 *
 * 1) for a two Terminal PowerTransformer the high voltage (TransformerEnd.endNumber=1) PowerTransformerEnd has nonzero values on
 * r, r0, x, and x0 while the low voltage (TransformerEnd.endNumber=0) PowerTransformerEnd has zero values for r, r0, x, and x0.
 * 2) for a three Terminal PowerTransformer the three PowerTransformerEnds represents a star equivalent with each leg in the star
 * represented by r, r0, x, and x0 values.
 * 3) For a three Terminal transformer each PowerTransformerEnd shall have g, g0, b and b0 values corresponding the no load losses
 * distributed on the three PowerTransformerEnds. The total no load loss shunt impedances may also be placed at one of the
 * PowerTransformerEnds, preferably the end numbered 1, having the shunt values on end 1 is the preferred way.
 * 4) for a PowerTransformer with more than three Terminals the PowerTransformerEnd impedance values cannot be used. Instead, use
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
 * @property ratedS The largest normal apparent power rating for this end. The attribute shall be a positive value. For a two-winding transformer the values for
 * the high and low voltage sides shall be identical.
 * @property ratedU  Rated voltage: phase-phase for three-phase windings, and either phase-phase or phase-neutral for single-phase windings.
 *                   A high voltage side, as given by TransformerEnd.endNumber, shall have a ratedU that is greater or equal than ratedU
 *                   for the lower voltage sides.
 * @property x Positive sequence series reactance (star-model) of the transformer end in ohms. Do not read this directly, use [resistanceReactance().x] instead.
 * @property x0 Zero sequence series reactance of the transformer end in ohms. Do not read this directly, use [resistanceReactance().x0] instead.
 */
class PowerTransformerEnd(mRID: String) : TransformerEnd(mRID) {

    var powerTransformer: PowerTransformer? = null
        set(value) {
            field = value?.run{
                if (field == null || field === value) value else throw IllegalStateException("powerTransformer has already been set to $field. Cannot set this field again")
            }
        }

    var b: Double? = null
    var b0: Double? = null
    var connectionKind: WindingConnection = WindingConnection.UNKNOWN
    var g: Double? = null
    var g0: Double? = null
    var phaseAngleClock: Int? = null
    var r: Double? = null
    var r0: Double? = null

    var ratedS: Int?
        get() = _sRatings?.firstOrNull()?.ratedS
    @Deprecated(
            "Use addRating() instead, as this will clear all ratings and is intended for backwards compatibility only",
            ReplaceWith("addRating(value, TransformerCoolingType.UNKNOWN_COOLING_TYPE)"),
        )
        set(value) {
            if (value != null) {
                clearRatings()
                addRating(value, TransformerCoolingType.UNKNOWN)
            } else {
                clearRatings()
            }

        }

    var ratedU: Int? = null
    var x: Double? = null
    var x0: Double? = null
    private var _sRatings: MutableList<TransformerEndRatedS>? = null

    /**
     * [ZBEX] The normal apparent power ratings for this transformer by their [TransformerCoolingType],
     * stored in descending order by ratedS. The largest rating will be at the start.
     *
     * The [TransformerEndRatedS.ratedS] attribute shall be a positive value. For a two-winding transformer the values for the high and low voltage sides shall
     * be identical.
     *
     * The returned collection is read only.
     */
    @ZBEX
    val sRatings: TransformerEndRatedSList
        get() = TransformerEndRatedSList(
            { _sRatings },
            { _sRatings = it },
            ::validateRating,
            { -it.ratedS }

        )


    private fun validateRating(rating: TransformerEndRatedS) {
        if (_sRatings?.any { r -> r.coolingType == rating.coolingType } == true)
            throw IllegalArgumentException("A rating for coolingType ${rating.coolingType.name} already exists, please remove it first.")
    }

    /**
     * Get the [ResistanceReactance] for this [PowerTransformerEnd] from either:
     * 1. directly assigned values or
     * 2. the pre-calculated [starImpedance] or
     * 3. from the datasheet information of the associated [powerTransformer]
     *
     * If the data is not complete in any of the above it will merge in the missing values from the subsequent sources.
     */
    override fun resistanceReactance(): ResistanceReactance =
        ResistanceReactance(r, x, r0, x0).mergeIfIncomplete {
            starImpedance?.resistanceReactance()
        }.mergeIfIncomplete {
            powerTransformer?.assetInfo?.resistanceReactance(endNumber)
        }

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region sRatings boilerplate

    @Deprecated(
        message = "Use sRatings.getByCoolingType(coolingType) instead.",
        replaceWith = ReplaceWith("sRatings.getByCoolingType(coolingType)")
    )
    fun getRating(coolingType: TransformerCoolingType): TransformerEndRatedS? = sRatings.getByCoolingType(coolingType)

    @Deprecated(
        message = "Use sRatings.size instead.",
        replaceWith = ReplaceWith("sRatings.size")
    )
    fun numRatings(): Int = sRatings.size

    @Deprecated(
        message = "Use sRatings.add(ratedS, coolingType) instead.",
        replaceWith = ReplaceWith("also { it.sRatings.add(ratedS, coolingType) }")
    )
    fun addRating(
        ratedS: Int,
        coolingType: TransformerCoolingType = TransformerCoolingType.UNKNOWN,
    ): PowerTransformerEnd = apply {
        sRatings.add(ratedS, coolingType)
    }

    @Deprecated(
        message = "Use sRatings.add(rating) instead.",
        replaceWith = ReplaceWith("also { it.sRatings.add(rating) }")
    )
    fun addRating(rating: TransformerEndRatedS): PowerTransformerEnd = apply {
        sRatings.add(rating)
    }

    @Deprecated(
        message = "Use sRatings.remove(rating) instead.",
        replaceWith = ReplaceWith("sRatings.remove(rating)")
    )
    fun removeRating(rating: TransformerEndRatedS): Boolean = sRatings.remove(rating)

    @Deprecated(
        message = "Use sRatings.removeByCoolingType(coolingType) instead.",
        replaceWith = ReplaceWith("sRatings.removeByCoolingType(coolingType)")
    )
    fun removeRating(coolingType: TransformerCoolingType): TransformerEndRatedS? = sRatings.removeByCoolingType(coolingType)

    @Deprecated(
        message = "Use sRatings.clear() instead.",
        replaceWith = ReplaceWith("also { it.sRatings.clear() }")
    )
    fun clearRatings(): PowerTransformerEnd = apply {
        sRatings.clear()
    }

    // endregion

    // endregion

}
