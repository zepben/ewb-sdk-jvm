/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61968.assetinfo.PowerTransformerInfo
import com.zepben.evolve.cim.iec61968.assets.AssetInfo
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.common.extensions.*

/**
 * An electrical device consisting of  two or more coupled windings, with or without a magnetic core, for introducing mutual coupling
 * between electric circuits. Transformers can be used to control voltage and phase shift (active power flow).
 *
 *
 * A power transformer may be composed of separate transformer tanks that need not be identical.
 *
 *
 * A power transformer can be modeled with or without tanks and is intended for use in both balanced and unbalanced representations. A
 * power transformer typically has two terminals, but may have one (grounding), three or more terminals.
 *
 *
 * The inherited association ConductingEquipment.BaseVoltage should not be used.  The association from TransformerEnd to BaseVoltage
 * should be used instead.
 *
 * @property vectorGroup Vector group of the transformer for protective relaying, e.g., Dyn1. For unbalanced transformers, this may not be simply
 *                       determined from the constituent winding connections and phase angle displacements.
 *
 *                       The vectorGroup string consists of the following components in the order listed: high voltage winding connection, mid
 *                       voltage winding connection(for three winding transformers), phase displacement clock number from 0 to 11,  low voltage
 *                       winding connection phase displacement clock number from 0 to 11.   The winding connections are D(delta), Y(wye),
 *                       YN(wye with neutral), Z(zigzag), ZN(zigzag with neutral), A(auto transformer). Upper case means the high voltage,
 *                       lower case mid or low.The high voltage winding always has clock position 0 and is not included in the vector group
 *                       string.  Some examples: YNy0(two winding wye to wye with no phase displacement), YNd11(two winding wye to delta with
 *                       330 degrees phase displacement), YNyn0d5(three winding transformer wye with neutral high voltage, wye with neutral mid
 *                       voltage and no phase displacement, delta low voltage with 150 degrees displacement).
 *
 *                       Phase displacement is defined as the angular difference between the phasors representing the voltages between the
 *                       neutral point(real or imaginary) and the corresponding terminals of two windings, a positive sequence voltage system
 *                       being applied to the high-voltage terminals, following each other in alphabetical sequence if they are lettered, or in
 *                       numerical sequence if they are numbered: the phasors are assumed to rotate in a counter-clockwise sense.
 *
 * @property primaryVoltage Holds the primary voltage value for a transformer.
 * @property transformerUtilisation The fraction of the transformer’s normal capacity (nameplate rating) that is in use. It may be expressed as the
 *                                  result of the calculation S/Sn, where S = Load on Transformer (in VA), Sn = Transformer Nameplate Rating (in VA).
 *                                  A value of NaN signifies the data is missing/unknown.
 */
class PowerTransformer @JvmOverloads constructor(mRID: String = "") : ConductingEquipment(mRID) {

    val primaryVoltage: Int?
        get() = if (ends.isEmpty()) baseVoltage?.nominalVoltage else ends[0].baseVoltage?.nominalVoltage ?: ends[0].ratedU

    private var _powerTransformerEnds: MutableList<PowerTransformerEnd>? = null
    var vectorGroup: VectorGroup = VectorGroup.UNKNOWN
    var transformerUtilisation: Double? = null

    /**
     * Override the [AssetInfo] as [PowerTransformerInfo].
     */
    override var assetInfo: PowerTransformerInfo? = null
        set(value) {
            if (value != null) {
                val invalidEnds = ends.filter { it.starImpedance != null }
                require(invalidEnds.isEmpty()) {
                    "Unable to use ${value.typeNameAndMRID()} for ${typeNameAndMRID()} because the following associated ends have a direct link to a star impedance: ${invalidEnds.map { it.typeNameAndMRID() }}."
                }
            }
            field = value
        }

    /**
     * The PowerTransformerEnd's for this PowerTransformer. The returned collection is read only.
     */
    val ends: List<PowerTransformerEnd> get() = _powerTransformerEnds.asUnmodifiable()

    /**
     * Get the number of entries in the [PowerTransformerEnd] collection.
     */
    fun numEnds() = _powerTransformerEnds?.size ?: 0

    /**
     * Get a [PowerTransformerEnd] of this [PowerTransformer] by its [PowerTransformerEnd.mRID]
     *
     * @param mRID the mRID of the required [PowerTransformerEnd]
     * @return The [PowerTransformerEnd] with the specified [mRID] if it exists, otherwise null
     */
    fun getEnd(mRID: String) = _powerTransformerEnds.getByMRID(mRID)

    /**
     * Get a [PowerTransformerEnd] of this [PowerTransformer] by its [PowerTransformerEnd.endNumber]
     *
     * @param endNumber the end number of the required [PowerTransformerEnd]
     * @return The [PowerTransformerEnd] with the specified [endNumber] if it exists, otherwise null
     */
    fun getEnd(endNumber: Int) = _powerTransformerEnds?.firstOrNull { it.endNumber == endNumber }

    /**
     * Add a [PowerTransformerEnd] to this [PowerTransformer]
     *
     * If [PowerTransformerEnd.endNumber] is 0 [end] will receive an endNumber of [numEnds] + 1 when added.
     * @throws IllegalStateException if the [PowerTransformerEnd] references another [PowerTransformer] or if a [PowerTransformerEnd] with
     *         the same endNumber already exists.
     * @return This [PowerTransformer] for fluent use
     */
    fun addEnd(end: PowerTransformerEnd): PowerTransformer {
        if (validateEnd(end)) return this

        if (end.endNumber == 0)
            end.endNumber = numEnds() + 1
        require(getEnd(end.endNumber) == null) { "Unable to add ${end.typeNameAndMRID()} to ${typeNameAndMRID()}. A ${getEnd(end.endNumber)!!.typeNameAndMRID()} already exists with endNumber ${end.endNumber}." }

        _powerTransformerEnds = _powerTransformerEnds ?: mutableListOf()
        _powerTransformerEnds!!.add(end)
        _powerTransformerEnds!!.sortBy { it.endNumber }

        return this
    }

    fun removeEnd(end: PowerTransformerEnd?): Boolean {
        val ret = _powerTransformerEnds.safeRemove(end)
        if (_powerTransformerEnds.isNullOrEmpty()) _powerTransformerEnds = null
        return ret
    }

    fun clearEnds(): PowerTransformer {
        _powerTransformerEnds = null
        return this
    }

    private fun validateEnd(end: PowerTransformerEnd): Boolean {
        if (validateReference(end, ::getEnd, "A PowerTransformerEnd"))
            return true

        require(end.powerTransformer === this) { "${end.typeNameAndMRID()} references another PowerTransformer ${end.powerTransformer}, expected ${this}." }
        return false
    }
}

