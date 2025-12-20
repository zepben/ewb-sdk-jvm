/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.feeder

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.EquipmentContainer
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.wires.Switch
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * [ZBEX] A collection of equipment for purposes other than generation or utilization, through which electric energy in bulk is passed for the distribution of energy to low voltage network.
 * @property normalEnergizingFeeders [ZBEX] The Feeders that nominally energize the substation. Also used for naming purposes.
 * @property normalEnergizedLvFeeders [ZBEX] The LvFeeders that are nominally energized by this LvSubstation. Also used for naming purposes.
 * @property currentEnergizingFeeders [ZBEX] The Feeders that currently energize the substation. Also used for naming purposes.
 */
@ZBEX
class LvSubstation(mRID: String) : EquipmentContainer(mRID) {

    private var _normalEnergizingFeedersById: MutableMap<String?, Feeder>? = null
    private var _currentEnergizingFeedersById: MutableMap<String?, Feeder>? = null
    private var _normalEnergizedLvFeedersById: MutableMap<String?, LvFeeder>? = null

    /**
     * [ZBEX] The HV/MV feeders that normally energize this [LvSubstation]. The returned collection is read only.
     */
    @ZBEX
    val normalEnergizingFeeders: Collection<Feeder> get() = _normalEnergizingFeedersById?.values.asUnmodifiable()

    /**
     * Get the number of entries in the normal [Feeder] collection.
     */
    fun numNormalEnergizingFeeders(): Int = _normalEnergizingFeedersById?.size ?: 0

    /**
     * Energizing feeder using the normal state of the network.
     *
     * @param mRID the mRID of the required normal [Feeder]
     * @return The [Feeder] with the specified [mRID] if it exists, otherwise null
     */
    fun getNormalEnergizingFeeder(mRID: String): Feeder? = _normalEnergizingFeedersById?.get(mRID)

    /**
     * Associate this [LvSubstation] with a [Feeder] in the normal state of the network.
     *
     * @param feeder the HV/MV feeder to associate with this [LvSubstation] in the normal state of the network.
     * @return This [LvSubstation] for fluent use.
     */
    fun addNormalEnergizingFeeder(feeder: Feeder): LvSubstation {
        if (validateReference(feeder, ::getNormalEnergizingFeeder, "A Feeder"))
            return this

        _normalEnergizingFeedersById = _normalEnergizingFeedersById ?: mutableMapOf()
        _normalEnergizingFeedersById!!.putIfAbsent(feeder.mRID, feeder)

        return this
    }

    /**
     * Disassociate this [LvSubstation] from a [Feeder] in the normal state of the network.
     *
     * @param feeder the HV/MV feeder to disassociate from this [LvSubstation] in the normal state of the network.
     * @return true if a matching feeder is removed from the collection.
     */
    fun removeNormalEnergizingFeeder(feeder: Feeder): Boolean {
        val ret = _normalEnergizingFeedersById?.remove(feeder.mRID)
        if (_normalEnergizingFeedersById.isNullOrEmpty()) _normalEnergizingFeedersById = null
        return ret != null
    }

    /**
     * Clear all [Feeder]'s associated with this [LvSubstation] in the normal state of the network.
     *
     * @return This [LvSubstation] for fluent use.
     */
    fun clearNormalEnergizingFeeders(): LvSubstation {
        _normalEnergizingFeedersById = null
        return this
    }

    @ZBEX
    val normalEnergizedLvFeeders: Collection<LvFeeder> get() = _normalEnergizedLvFeedersById?.values.asUnmodifiable()

    /**
     * Get the number of entries in the normal [LvFeeder] collection.
     */
    fun numNormalEnergizedLvFeeders(): Int = _normalEnergizedLvFeedersById?.size ?: 0

    /**
     * Retrieve an energized [LvFeeder] using the normal state of the network.
     *
     * @param mRID the mRID of the required normal [LvFeeder]
     * @return The [LvFeeder] with the specified [mRID] if it exists, otherwise null
     */
    fun getNormalEnergizedLvFeeder(mRID: String): LvFeeder? = _normalEnergizedLvFeedersById?.get(mRID)

    /**
     * @param lvFeeder the [LvFeeder] to associate with this feeder in the normal state of the network.
     */
    fun addNormalEnergizedLvFeeder(lvFeeder: LvFeeder): LvSubstation {
        if (validateReference(lvFeeder, ::getNormalEnergizedLvFeeder, "An LvFeeder"))
            return this

        _normalEnergizedLvFeedersById = _normalEnergizedLvFeedersById ?: mutableMapOf()
        _normalEnergizedLvFeedersById!!.putIfAbsent(lvFeeder.mRID, lvFeeder)

        return this
    }

    /**
     * @param lvFeeder the [LvFeeder] to disassociate from this HV/MV feeder in the normal state of the network.
     */
    fun removeNormalEnergizedLvFeeder(lvFeeder: LvFeeder): Boolean {
        val ret = _normalEnergizedLvFeedersById?.remove(lvFeeder.mRID)
        if (_normalEnergizedLvFeedersById.isNullOrEmpty()) _normalEnergizedLvFeedersById = null
        return ret != null
    }

    /**
     * Clear all [LvFeeder]'s associated with this [LvSubstation] in the normal state of the network.
     *
     * @return This [LvSubstation] for fluent use.
     */
    fun clearNormalEnergizedLvFeeders(): LvSubstation {
        _normalEnergizedLvFeedersById = null
        return this
    }

    /**
     * [ZBEX] The HV/MV feeders that currently energize this LV substation. The returned collection is read only.
     */
    @ZBEX
    val currentEnergizingFeeders: Collection<Feeder> get() = _currentEnergizingFeedersById?.values.asUnmodifiable()

    /**
     * Get the number of entries in the current [Feeder] collection.
     */
    fun numCurrentEnergizingFeeders(): Int = _currentEnergizingFeedersById?.size ?: 0

    /**
     * Retrieve an energizing feeder using the current state of the network.
     *
     * @param mRID the mRID of the required current [Feeder]
     * @return The [Feeder] with the specified [mRID] if it exists, otherwise null
     */
    fun getCurrentEnergizingFeeder(mRID: String): Feeder? = _currentEnergizingFeedersById?.get(mRID)

    /**
     * Associate this [LvSubstation] with a [Feeder] in the current state of the network.
     *
     * @param feeder the HV/MV feeder to associate with this [LvSubstation] in the current state of the network.
     * @return This [LvSubstation] for fluent use.
     */
    fun addCurrentEnergizingFeeder(feeder: Feeder): LvSubstation {
        if (validateReference(feeder, ::getCurrentEnergizingFeeder, "A Feeder"))
            return this

        _currentEnergizingFeedersById = _currentEnergizingFeedersById ?: mutableMapOf()
        _currentEnergizingFeedersById!!.putIfAbsent(feeder.mRID, feeder)

        return this
    }

    /**
     * Disassociate this [LvSubstation] from a [Feeder] in the current state of the network.
     *
     * @param feeder the HV/MV feeder to disassociate from this LvSubstation the current state of the network.
     * @return true if a matching feeder is removed from the collection.
     */
    fun removeCurrentEnergizingFeeder(feeder: Feeder): Boolean {
        val ret = _currentEnergizingFeedersById?.remove(feeder.mRID)
        if (_currentEnergizingFeedersById.isNullOrEmpty()) _currentEnergizingFeedersById = null
        return ret != null
    }

    /**
     * Clear all [Feeder]'s associated with this [LvSubstation] in the current state of the network.
     *
     * @return This [LvSubstation] for fluent use.
     */
    fun clearCurrentEnergizingFeeders(): LvSubstation {
        _currentEnergizingFeedersById = null
        return this
    }

    /**
     * Retrieves all normally energized LvFeeders that represent low voltage network connected below a switch on the edge of this LvSubstation. This is all LvFeeders in the normalEnergizedLvFeeders that has a normalHeadTerminal attached to a Switch.
     */
    fun normalEnergizedLvSwitchFeeders(): List<LvFeeder> = normalEnergizedLvFeeders.filter { it.normalHeadTerminal?.conductingEquipment is Switch }

}