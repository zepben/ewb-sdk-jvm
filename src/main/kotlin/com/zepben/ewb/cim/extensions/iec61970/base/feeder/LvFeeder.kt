/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.feeder

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.cim.iec61970.base.core.EquipmentContainer
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * [ZBEX]
 * A branch of LV network starting at a distribution substation and continuing until the end of the LV network.
 *
 * @property normalHeadTerminal [ZBEX] The normal head terminal of this LV feeder, typically the LV terminal of a distribution substation.
 * @property normalEnergizingFeeders [ZBEX] The HV/MV feeders that energize this LV feeder.
 * @property currentEnergizingFeeders [ZBEX] The HV/MV feeders that energize this LV feeder in the current state of the network.
 * @property currentEquipment [ZBEX] Contained equipment using the current state of the network.
 */
@ZBEX
class LvFeeder(mRID: String) : EquipmentContainer(mRID) {

    @ZBEX
    var normalHeadTerminal: Terminal? = null
        set(value) {
            field =
                if ((equipment.isEmpty() && currentEquipment.isEmpty()) || normalHeadTerminal == null) value
                else throw IllegalStateException(
                    "LvFeeder $mRID has equipment assigned to it. Cannot update normalHeadTerminal on a feeder with equipment assigned."
                )
        }

    private var _normalEnergizingFeedersById: MutableMap<String?, Feeder>? = null
    private var _currentEnergizingFeedersById: MutableMap<String?, Feeder>? = null
    private var _currentEquipmentById: MutableMap<String?, Equipment>? = null
    var normalEnergizingLvSubstation: LvSubstation? = null

    /**
     * [ZBEX] The HV/MV feeders that normally energize this LV feeder. The returned collection is read only.
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
     * Associate this [LvFeeder] with a [Feeder] in the normal state of the network.
     *
     * @param feeder the HV/MV feeder to associate with this LV feeder in the normal state of the network.
     * @return This [LvFeeder] for fluent use.
     */
    fun addNormalEnergizingFeeder(feeder: Feeder): LvFeeder {
        if (validateReference(feeder, ::getNormalEnergizingFeeder, "A Feeder"))
            return this

        _normalEnergizingFeedersById = _normalEnergizingFeedersById ?: mutableMapOf()
        _normalEnergizingFeedersById!!.putIfAbsent(feeder.mRID, feeder)

        return this
    }

    /**
     * Disassociate this [LvFeeder] from a [Feeder] in the normal state of the network.
     *
     * @param feeder the HV/MV feeder to disassociate from this LV feeder in the normal state of the network.
     * @return true if a matching feeder is removed from the collection.
     */
    fun removeNormalEnergizingFeeder(feeder: Feeder): Boolean {
        val ret = _normalEnergizingFeedersById?.remove(feeder.mRID)
        if (_normalEnergizingFeedersById.isNullOrEmpty()) _normalEnergizingFeedersById = null
        return ret != null
    }

    /**
     * Clear all [Feeder]'s associated with this [LvFeeder] in the normal state of the network.
     *
     * @return This [LvFeeder] for fluent use.
     */
    fun clearNormalEnergizingFeeders(): LvFeeder {
        _normalEnergizingFeedersById = null
        return this
    }

    /**
     * [ZBEX] The HV/MV feeders that currently energize this LV feeder. The returned collection is read only.
     */
    @ZBEX
    val currentEnergizingFeeders: Collection<Feeder> get() = _currentEnergizingFeedersById?.values.asUnmodifiable()

    /**
     * Get the number of entries in the current [Feeder] collection.
     */
    fun numCurrentEnergizingFeeders(): Int = _currentEnergizingFeedersById?.size ?: 0

    /**
     * Energizing feeder using the current state of the network.
     *
     * @param mRID the mRID of the required current [Feeder]
     * @return The [Feeder] with the specified [mRID] if it exists, otherwise null
     */
    fun getCurrentEnergizingFeeder(mRID: String): Feeder? = _currentEnergizingFeedersById?.get(mRID)

    /**
     * Associate this [LvFeeder] with a [Feeder] in the current state of the network.
     *
     * @param feeder the HV/MV feeder to associate with this LV feeder in the current state of the network.
     * @return This [LvFeeder] for fluent use.
     */
    fun addCurrentEnergizingFeeder(feeder: Feeder): LvFeeder {
        if (validateReference(feeder, ::getCurrentEnergizingFeeder, "A Feeder"))
            return this

        _currentEnergizingFeedersById = _currentEnergizingFeedersById ?: mutableMapOf()
        _currentEnergizingFeedersById!!.putIfAbsent(feeder.mRID, feeder)

        return this
    }

    /**
     * Disassociate this [LvFeeder] from a [Feeder] in the current state of the network.
     *
     * @param feeder the HV/MV feeder to disassociate from this LV feeder in the current state of the network.
     * @return true if a matching feeder is removed from the collection.
     */
    fun removeCurrentEnergizingFeeder(feeder: Feeder): Boolean {
        val ret = _currentEnergizingFeedersById?.remove(feeder.mRID)
        if (_currentEnergizingFeedersById.isNullOrEmpty()) _currentEnergizingFeedersById = null
        return ret != null
    }

    /**
     * Clear all [Feeder]'s associated with this [LvFeeder] in the current state of the network.
     *
     * @return This [LvFeeder] for fluent use.
     */
    fun clearCurrentEnergizingFeeders(): LvFeeder {
        _currentEnergizingFeedersById = null
        return this
    }

    /**
     * Contained equipment using the current state of the network. The returned collection is read only.
     */
    override val currentEquipment: Collection<Equipment> get() = _currentEquipmentById?.values.asUnmodifiable()

    /**
     * Get the number of entries in the current [Equipment] collection.
     */
    override fun numCurrentEquipment(): Int = _currentEquipmentById?.size ?: 0

    /**
     * Contained equipment using the current state of the network.
     *
     * @param mRID the mRID of the required current [Equipment]
     * @return The [Equipment] with the specified [mRID] if it exists, otherwise null
     */
    override fun getCurrentEquipment(mRID: String): Equipment? = _currentEquipmentById?.get(mRID)

    /**
     * Associate this [LvFeeder] with an [Equipment] in the current state of the network.
     *
     * @param equipment the equipment to associate with this LV feeder in the current state of the network.
     */
    override fun addCurrentEquipment(equipment: Equipment): LvFeeder {
        if (validateReference(equipment, ::getCurrentEquipment, "A current Equipment"))
            return this

        _currentEquipmentById = _currentEquipmentById ?: mutableMapOf()
        _currentEquipmentById!!.putIfAbsent(equipment.mRID, equipment)

        return this
    }

    /**
     * Disassociate this [LvFeeder] from an [Equipment] in the current state of the network.
     *
     * @param equipment the equipment to disassociate with this LV feeder in the current state of the network.
     */
    override fun removeCurrentEquipment(equipment: Equipment): Boolean {
        val ret = _currentEquipmentById?.remove(equipment.mRID)
        if (_currentEquipmentById.isNullOrEmpty()) _currentEquipmentById = null
        return ret != null
    }

    /**
     * Clear all [Equipment] associated with this [LvFeeder].
     */
    override fun clearCurrentEquipment(): LvFeeder {
        _currentEquipmentById = null
        return this
    }

}
