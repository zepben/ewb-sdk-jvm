/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.feeder

import com.zepben.ewb.boilerplate.LazyMridMap
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.cim.iec61970.base.core.EquipmentContainer
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.Terminal

/**
 * [ZBEX]
 * A branch of LV network starting at a distribution substation and continuing until the end of the LV network.
 *
 * @property normalHeadTerminal [ZBEX] The normal head terminal of this LV feeder, typically the LV terminal of a distribution transformer, or the downstream
 * terminal of a LV switch below a distribution transformer.
 * @property normalEnergizingFeeders [ZBEX] The HV/MV feeders that energize this LV feeder.
 * @property currentEnergizingFeeders [ZBEX] The HV/MV feeders that energize this LV feeder in the current state of the network.
 * @property currentEquipment [ZBEX] Contained equipment using the current state of the network.
 * @property normalEnergizingLvSubstation [ZBEX] The normally energizing [LvSubstation] for this [LvFeeder].
 */
@ZBEX
class LvFeeder(mRID: String) : EquipmentContainer(mRID) {

    @ZBEX
    var normalHeadTerminal: Terminal? = null
        set(value) {
            field =
                if ((equipment.isEmpty() && currentEquipment.isEmpty()) || normalHeadTerminal == null) value
                else throw IllegalStateException(
                    "LvFeeder $mRID has equipment assigned to it. Cannot update normalHeadTerminal on a feeder with equipment assigned.",
                )
        }

    private var _normalEnergizingFeedersById: MutableMap<String, Feeder>? = null
    private var _currentEnergizingFeedersById: MutableMap<String, Feeder>? = null
    private var _currentEquipmentById: MutableMap<String, Equipment>? = null
    var normalEnergizingLvSubstation: LvSubstation? = null

    /**
     * [ZBEX] The HV/MV feeders that normally energize this LV feeder. The returned collection is read only.
     */
    @ZBEX
    val normalEnergizingFeeders: LazyMridMap<Feeder> get() = LazyMridMap(
        getter = { _normalEnergizingFeedersById },
        setter = { _normalEnergizingFeedersById = it },
        owner = { this },
        elementDescription = "A Feeder"
    )

    /**
     * [ZBEX] The HV/MV feeders that currently energize this LV feeder. The returned collection is read only.
     */
    @ZBEX
    val currentEnergizingFeeders: LazyMridMap<Feeder> get() = LazyMridMap(
        getter = { _currentEnergizingFeedersById },
        setter = { _currentEnergizingFeedersById = it },
        owner = { this },
        elementDescription = "A Feeder"
    )

    /**
     * Contained equipment using the current state of the network. The returned collection is read only.
     */
    override val currentEquipment: LazyMridMap<Equipment> get() = LazyMridMap(
        getter = { _currentEquipmentById },
        setter = { _currentEquipmentById = it },
        owner = { this },
        elementDescription = "A current Equipment",
    )


    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding dict.

    // region normalEnergizingFeeders boilerplate

    @Deprecated(
        message = "Use normalEnergizingFeeders.size instead.",
        replaceWith = ReplaceWith("normalEnergizingFeeders.size")
    )
    fun numNormalEnergizingFeeders(): Int = normalEnergizingFeeders.size

    @Deprecated(
        message = "Use normalEnergizingFeeders.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("normalEnergizingFeeders.getByMrid(mRID)")
    )
    fun getNormalEnergizingFeeder(mRID: String): Feeder? = normalEnergizingFeeders.getByMrid(mRID)

    @Deprecated(
        message = "Use normalEnergizingFeeders.add(feeder) instead.",
        replaceWith = ReplaceWith("also { it.normalEnergizingFeeders.add(feeder) }")
    )
    fun addNormalEnergizingFeeder(feeder: Feeder): LvFeeder {
        normalEnergizingFeeders.add(feeder)
        return this
    }

    @Deprecated(
        message = "Use normalEnergizingFeeders.remove(feeder) instead.",
        replaceWith = ReplaceWith("normalEnergizingFeeders.remove(feeder)")
    )
    fun removeNormalEnergizingFeeder(feeder: Feeder): Boolean =

    // endregion

        normalEnergizingFeeders.remove(feeder)

    @Deprecated(
        message = "Use normalEnergizingFeeders.clear() instead.",
        replaceWith = ReplaceWith("also { it.normalEnergizingFeeders.clear() }")
    )
    fun clearNormalEnergizingFeeders(): LvFeeder {
        normalEnergizingFeeders.clear()
        return this
    }



    @Deprecated(
        message = "Use currentEnergizingFeeders.size instead.",
        replaceWith = ReplaceWith("currentEnergizingFeeders.size")
    )
    fun numCurrentEnergizingFeeders(): Int = currentEnergizingFeeders.size

    @Deprecated(
        message = "Use currentEnergizingFeeders.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("currentEnergizingFeeders.getByMrid(mRID)")
    )
    fun getCurrentEnergizingFeeder(mRID: String): Feeder? = currentEnergizingFeeders.getByMrid(mRID)

    @Deprecated(
        message = "Use currentEnergizingFeeders.add(feeder) instead.",
        replaceWith = ReplaceWith("also { it.currentEnergizingFeeders.add(feeder) }")
    )
    fun addCurrentEnergizingFeeder(feeder: Feeder): LvFeeder {
        currentEnergizingFeeders.add(feeder)
        return this
    }

    @Deprecated(
        message = "Use currentEnergizingFeeders.remove(feeder) instead.",
        replaceWith = ReplaceWith("currentEnergizingFeeders.remove(feeder)")
    )
    fun removeCurrentEnergizingFeeder(feeder: Feeder): Boolean =
        currentEnergizingFeeders.remove(feeder)

    @Deprecated(
        message = "Use currentEnergizingFeeders.clear() instead.",
        replaceWith = ReplaceWith("also { it.currentEnergizingFeeders.clear() }")
    )
    fun clearCurrentEnergizingFeeders(): LvFeeder {
        currentEnergizingFeeders.clear()
        return this
    }



    @Deprecated(
        message = "Use currentEquipment.add(currentEquipment) instead.",
        replaceWith = ReplaceWith("also { it.currentEquipment.add(currentEquipment) }")
    )
    override fun addCurrentEquipment(equipment: Equipment): LvFeeder {
        // Note: Have to override return type
        currentEquipment.add(equipment)
        return this
    }

    @Deprecated(
        message = "Use currentEquipment.clear() instead.",
        replaceWith = ReplaceWith("also { it.currentEquipment.clear() }")
    )
    override fun clearCurrentEquipment(): LvFeeder {
        // Note: Have to override return type
        currentEquipment.clear()
        return this
    }

    //endregion


}
