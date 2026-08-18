/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.boilerplate.collections.LazyMridMap
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation
import com.zepben.ewb.services.common.extensions.validateReference

/**
 * A collection of equipment for organizational purposes, used for grouping distribution resources.
 * The organization a feeder does not necessarily reflect connectivity or current operation state.
 * @property normalHeadTerminal The normal head terminal or terminals of the feeder.
 * @property normalEnergizingSubstation The substation that normally energizes this feeder.
 * @property normalEnergizedLvFeeders [ZBEX] The LV feeders that are normally energized by this feeder.
 * @property currentEnergizedLvFeeders [ZBEX] The LV feeders that are currently energized by this feeder.
 * @property normalEnergizedLvSubstations [ZBEX] The normal energized LvSubstations of the feeder. Also used for naming purposes.
 * @property currentEnergizedLvSubstations [ZBEX] The current energized LvSubstations of the feeder. Also used for naming purposes.
 */
class Feeder(mRID: String) : EquipmentContainer(mRID) {

    var normalHeadTerminal: Terminal? = null
        set(value) {
            field =
                if ((equipment.isEmpty() && currentEquipment.isEmpty()) || normalHeadTerminal == null) value else throw IllegalStateException("Feeder $mRID has equipment assigned to it. Cannot update normalHeadTerminal on a feeder with equipment assigned.")
        }

    var normalEnergizingSubstation: Substation? = null
    private var _currentEquipmentById: MutableMap<String, Equipment>? = null
    private var _normalEnergizedLvFeedersById: MutableMap<String, LvFeeder>? = null
    private var _currentEnergizedLvFeedersById: MutableMap<String, LvFeeder>? = null
    private var _normalEnergizedLvSubstationsById: MutableMap<String, LvSubstation>? = null
    private var _currentEnergizedLvSubstationsById: MutableMap<String, LvSubstation>? = null

    /**
     * Contained equipment using the current state of the network. The returned collection is read only.
     */
    override val currentEquipment: MridCollection<Equipment> get() = LazyMridMap(
        getter = { _currentEquipmentById },
        setter = { _currentEquipmentById = it },
        owner = this,
        elementDescription = "A current Equipment",
    )

    /**
     * Get the number of entries in the current [Equipment] collection.
     */
    @Deprecated(
        message = "Use currentEquipment.size instead.",
        replaceWith = ReplaceWith("currentEquipment.size")
    )
    override fun numCurrentEquipment(): Int = _currentEquipmentById?.size ?: 0

    /**
     * Contained equipment using the current state of the network.
     *
     * @param mRID the mRID of the required current [Equipment]
     * @return The [Equipment] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use currentEquipment.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("currentEquipment.getByMrid(mRID)")
    )
    override fun getCurrentEquipment(mRID: String): Equipment? = _currentEquipmentById?.get(mRID)

    /**
     * @param equipment the equipment to disassociate from this feeder in the current state of the network.
     */
    @Deprecated(
        message = "Use currentEquipment.remove(equipment) instead.",
        replaceWith = ReplaceWith("currentEquipment.remove(equipment)")
    )
    override fun removeCurrentEquipment(equipment: Equipment): Boolean {
        val ret = _currentEquipmentById?.remove(equipment.mRID)
        if (_currentEquipmentById.isNullOrEmpty()) _currentEquipmentById = null
        return ret != null
    }

    /**
     * The LV feeders that are normally energized by the feeder. The returned collection is read only.
     */
    val normalEnergizedLvFeeders: MridCollection<LvFeeder> get() = LazyMridMap(
        getter = { _normalEnergizedLvFeedersById },
        setter = { _normalEnergizedLvFeedersById = it },
        owner = this,
        elementDescription = "An LvFeeder"
    )

    /**
     * The LV feeders that are currently energized by the feeder. The returned collection is read only.
     */
    @ZBEX
    val currentEnergizedLvFeeders: MridCollection<LvFeeder> get() = LazyMridMap(
        getter = { _currentEnergizedLvFeedersById },
        setter = { _currentEnergizedLvFeedersById = it },
        owner = this,
        elementDescription = "An LvFeeder"
    )

    @ZBEX
    val normalEnergizedLvSubstations: MridCollection<LvSubstation> get() = LazyMridMap(
        getter = { _normalEnergizedLvSubstationsById },
        setter = { _normalEnergizedLvSubstationsById = it },
        owner = this,
        elementDescription = "An LvSubstation"
    )

    @ZBEX
    val currentEnergizedLvSubstations: MridCollection<LvSubstation> get() = LazyMridMap(
        getter = { _currentEnergizedLvSubstationsById },
        setter = { _currentEnergizedLvSubstationsById = it },
        owner = this,
        elementDescription = "An LvSubstation"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding dict.

    // region currentEquipment boilerplate

    /**
     * @param equipment the equipment to associate with this feeder in the current state of the network.
     */
    @Deprecated(
        message = "Use currentEquipment.add(currentEquipment) instead.",
        replaceWith = ReplaceWith("also { it.currentEquipment.add(currentEquipment) }")
    )
    override fun addCurrentEquipment(equipment: Equipment): Feeder {
        if (validateReference(equipment, ::getCurrentEquipment, "A current Equipment"))
            return this

        _currentEquipmentById = _currentEquipmentById ?: mutableMapOf()
        _currentEquipmentById!!.putIfAbsent(equipment.mRID, equipment)

        return this
    }

    /**
     * Clear all Equipment associated with this [Feeder]
     */
    @Deprecated(
        message = "Use currentEquipment.clear() instead.",
        replaceWith = ReplaceWith("also { it.currentEquipment.clear() }")
    )
    override fun clearCurrentEquipment(): Feeder {
        _currentEquipmentById = null
        return this
    }

    // endregion

    // region normalEnergizedLvFeeders boilerplate

    /**
     * Get the number of entries in the normal [LvFeeder] collection.
     */
    @Deprecated(
        message = "Use normalEnergizedLvFeeders.size instead.",
        replaceWith = ReplaceWith("normalEnergizedLvFeeders.size")
    )
    fun numNormalEnergizedLvFeeders(): Int = _normalEnergizedLvFeedersById?.size ?: 0

    /**
     * Energized LV feeder using the normal state of the network.
     *
     * @param mRID the mRID of the required normal [LvFeeder]
     * @return The [LvFeeder] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use normalEnergizedLvFeeders.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("normalEnergizedLvFeeders.getByMrid(mRID)")
    )
    fun getNormalEnergizedLvFeeder(mRID: String): LvFeeder? = _normalEnergizedLvFeedersById?.get(mRID)

    /**
     * @param lvFeeder the LV feeder to associate with this feeder in the normal state of the network.
     */
    @Deprecated(
        message = "Use normalEnergizedLvFeeders.add(lvFeeder) instead.",
        replaceWith = ReplaceWith("also { it.normalEnergizedLvFeeders.add(lvFeeder) }")
    )
    fun addNormalEnergizedLvFeeder(lvFeeder: LvFeeder): Feeder {
        if (validateReference(lvFeeder, ::getNormalEnergizedLvFeeder, "An LvFeeder"))
            return this

        _normalEnergizedLvFeedersById = _normalEnergizedLvFeedersById ?: mutableMapOf()
        _normalEnergizedLvFeedersById!!.putIfAbsent(lvFeeder.mRID, lvFeeder)

        return this
    }

    /**
     * @param lvFeeder the LV feeder to disassociate from this HV/MV feeder in the normal state of the network.
     */
    @Deprecated(
        message = "Use normalEnergizedLvFeeders.remove(lvFeeder) instead.",
        replaceWith = ReplaceWith("normalEnergizedLvFeeders.remove(lvFeeder)")
    )
    fun removeNormalEnergizedLvFeeder(lvFeeder: LvFeeder): Boolean {
        val ret = _normalEnergizedLvFeedersById?.remove(lvFeeder.mRID)
        if (_normalEnergizedLvFeedersById.isNullOrEmpty()) _normalEnergizedLvFeedersById = null
        return ret != null
    }

    // endregion

    /**
     * Clear all [LvFeeder]'s associated with this [Feeder] in the normal state of the network.
     *
     * @return This [LvFeeder] for fluent use.
     */
    @Deprecated(
        message = "Use normalEnergizedLvFeeders.clear() instead.",
        replaceWith = ReplaceWith("also { it.normalEnergizedLvFeeders.clear() }")
    )
    fun clearNormalEnergizedLvFeeders(): Feeder {
        _normalEnergizedLvFeedersById = null
        return this
    }



    /**
     * Get the number of entries in the current [LvFeeder] collection.
     */
    @Deprecated(
        message = "Use currentEnergizedLvFeeders.size instead.",
        replaceWith = ReplaceWith("currentEnergizedLvFeeders.size")
    )
    fun numCurrentEnergizedLvFeeders(): Int = _currentEnergizedLvFeedersById?.size ?: 0

    /**
     * Energized LV feeder using the current state of the network.
     *
     * @param mRID the mRID of the required current [LvFeeder]
     * @return The [LvFeeder] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use currentEnergizedLvFeeders.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("currentEnergizedLvFeeders.getByMrid(mRID)")
    )
    fun getCurrentEnergizedLvFeeder(mRID: String): LvFeeder? = _currentEnergizedLvFeedersById?.get(mRID)

    /**
     * @param lvFeeder the LV feeder to associate with this feeder in the current state of the network.
     */
    @Deprecated(
        message = "Use currentEnergizedLvFeeders.add(lvFeeder) instead.",
        replaceWith = ReplaceWith("also { it.currentEnergizedLvFeeders.add(lvFeeder) }")
    )
    fun addCurrentEnergizedLvFeeder(lvFeeder: LvFeeder): Feeder {
        if (validateReference(lvFeeder, ::getCurrentEnergizedLvFeeder, "An LvFeeder"))
            return this

        _currentEnergizedLvFeedersById = _currentEnergizedLvFeedersById ?: mutableMapOf()
        _currentEnergizedLvFeedersById!!.putIfAbsent(lvFeeder.mRID, lvFeeder)

        return this
    }

    /**
     * @param lvFeeder the LV feeder to disassociate from this HV/MV feeder in the current state of the network.
     */
    @Deprecated(
        message = "Use currentEnergizedLvFeeders.remove(lvFeeder) instead.",
        replaceWith = ReplaceWith("currentEnergizedLvFeeders.remove(lvFeeder)")
    )
    fun removeCurrentEnergizedLvFeeder(lvFeeder: LvFeeder): Boolean {
        val ret = _currentEnergizedLvFeedersById?.remove(lvFeeder.mRID)
        if (_currentEnergizedLvFeedersById.isNullOrEmpty()) _currentEnergizedLvFeedersById = null
        return ret != null
    }

    /**
     * Clear all [LvFeeder]'s associated with this [Feeder] in the current state of the network.
     *
     * @return This [LvFeeder] for fluent use.
     */
    @Deprecated(
        message = "Use currentEnergizedLvFeeders.clear() instead.",
        replaceWith = ReplaceWith("also { it.currentEnergizedLvFeeders.clear() }")
    )
    fun clearCurrentEnergizedLvFeeders(): Feeder {
        _currentEnergizedLvFeedersById = null
        return this
    }



    /**
     * Get the number of entries in the normal [LvSubstation] collection.
     */
    @Deprecated(
        message = "Use normalEnergizedLvSubstations.size instead.",
        replaceWith = ReplaceWith("normalEnergizedLvSubstations.size")
    )
    fun numNormalEnergizedLvSubstations(): Int = _normalEnergizedLvSubstationsById?.size ?: 0

    /**
     * Retrieve an energized LvSubstation using the normal state of the network.
     *
     * @param mRID the mRID of the required normal [LvSubstation]
     * @return The [LvSubstation] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use normalEnergizedLvSubstations.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("normalEnergizedLvSubstations.getByMrid(mRID)")
    )
    fun getNormalEnergizedLvSubstation(mRID: String): LvSubstation? = _normalEnergizedLvSubstationsById?.get(mRID)

    /**
     * Associate this [Feeder] with a [LvSubstation] in the normal state of the network.
     *
     * @param lvSubstation the [LvSubstation] to associate with this LV feeder in the normal state of the network.
     * @return This [Feeder] for fluent use.
     */
    @Deprecated(
        message = "Use normalEnergizedLvSubstations.add(lvSubstation) instead.",
        replaceWith = ReplaceWith("also { it.normalEnergizedLvSubstations.add(lvSubstation) }")
    )
    fun addNormalEnergizedLvSubstation(lvSubstation: LvSubstation): Feeder {
        if (validateReference(lvSubstation, ::getNormalEnergizedLvSubstation, "An LvSubstation"))
            return this

        _normalEnergizedLvSubstationsById = _normalEnergizedLvSubstationsById ?: mutableMapOf()
        _normalEnergizedLvSubstationsById!!.putIfAbsent(lvSubstation.mRID, lvSubstation)

        return this
    }

    /**
     * Disassociate this [Feeder] from a [LvSubstation] in the normal state of the network.
     *
     * @param lvSubstation the [LvSubstation] to disassociate from this LV feeder in the normal state of the network.
     * @return true if a matching [LvSubstation] is removed from the collection.
     */
    @Deprecated(
        message = "Use normalEnergizedLvSubstations.remove(lvSubstation) instead.",
        replaceWith = ReplaceWith("normalEnergizedLvSubstations.remove(lvSubstation)")
    )
    fun removeNormalEnergizedLvSubstation(lvSubstation: LvSubstation): Boolean {
        val ret = _normalEnergizedLvSubstationsById?.remove(lvSubstation.mRID)
        if (_normalEnergizedLvSubstationsById.isNullOrEmpty()) _normalEnergizedLvSubstationsById = null
        return ret != null
    }

    /**
     * Clear all [LvSubstation]'s associated with this [Feeder] in the normal state of the network.
     *
     * @return This [Feeder] for fluent use.
     */
    @Deprecated(
        message = "Use normalEnergizedLvSubstations.clear() instead.",
        replaceWith = ReplaceWith("also { it.normalEnergizedLvSubstations.clear() }")
    )
    fun clearNormalEnergizedLvSubstations(): Feeder {
        _normalEnergizedLvSubstationsById = null
        return this
    }



    /**
     * Get the number of entries in the current [LvSubstation] collection.
     */
    @Deprecated(
        message = "Use currentEnergizedLvSubstations.size instead.",
        replaceWith = ReplaceWith("currentEnergizedLvSubstations.size")
    )
    fun numCurrentEnergizedLvSubstations(): Int = _currentEnergizedLvSubstationsById?.size ?: 0

    /**
     * Retrieve an energized LvSubstation using the current state of the network.
     *
     * @param mRID the mRID of the required current [LvSubstation]
     * @return The [LvSubstation] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use currentEnergizedLvSubstations.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("currentEnergizedLvSubstations.getByMrid(mRID)")
    )
    fun getCurrentEnergizedLvSubstation(mRID: String): LvSubstation? = _currentEnergizedLvSubstationsById?.get(mRID)

    /**
     * Associate this [Feeder] with a [LvSubstation] in the current state of the network.
     *
     * @param lvSubstation the [LvSubstation] to associate with this LV feeder in the current state of the network.
     * @return This [Feeder] for fluent use.
     */
    @Deprecated(
        message = "Use currentEnergizedLvSubstations.add(lvSubstation) instead.",
        replaceWith = ReplaceWith("also { it.currentEnergizedLvSubstations.add(lvSubstation) }")
    )
    fun addCurrentEnergizedLvSubstation(lvSubstation: LvSubstation): Feeder {
        if (validateReference(lvSubstation, ::getCurrentEnergizedLvSubstation, "An LvSubstation"))
            return this

        _currentEnergizedLvSubstationsById = _currentEnergizedLvSubstationsById ?: mutableMapOf()
        _currentEnergizedLvSubstationsById!!.putIfAbsent(lvSubstation.mRID, lvSubstation)

        return this
    }

    /**
     * Disassociate this [Feeder] from a [LvSubstation] in the current state of the network.
     *
     * @param lvSubstation the [LvSubstation] to disassociate from this LV feeder in the current state of the network.
     * @return true if a matching [LvSubstation] is removed from the collection.
     */
    @Deprecated(
        message = "Use currentEnergizedLvSubstations.remove(lvSubstation) instead.",
        replaceWith = ReplaceWith("currentEnergizedLvSubstations.remove(lvSubstation)")
    )
    fun removeCurrentEnergizedLvSubstation(lvSubstation: LvSubstation): Boolean {
        val ret = _currentEnergizedLvSubstationsById?.remove(lvSubstation.mRID)
        if (_currentEnergizedLvSubstationsById.isNullOrEmpty()) _currentEnergizedLvSubstationsById = null
        return ret != null
    }

    /**
     * Clear all [LvSubstation]'s associated with this [Feeder] in the current state of the network.
     *
     * @return This [Feeder] for fluent use.
     */
    @Deprecated(
        message = "Use currentEnergizedLvSubstations.clear() instead.",
        replaceWith = ReplaceWith("also { it.currentEnergizedLvSubstations.clear() }")
    )
    fun clearCurrentEnergizedLvSubstations(): Feeder {
        _currentEnergizedLvSubstationsById = null
        return this
    }

    // endregion

}
