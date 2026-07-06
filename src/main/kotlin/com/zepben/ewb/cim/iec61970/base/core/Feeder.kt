/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.boilerplate.LazyMridMap
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation

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
    override val currentEquipment: LazyMridMap<Equipment> get() = LazyMridMap(
        getter = { _currentEquipmentById },
        setter = { _currentEquipmentById = it },
        owner = { this },
        elementDescription = "A current Equipment",
    )

    /**
     * The LV feeders that are normally energized by the feeder. The returned collection is read only.
     */
    val normalEnergizedLvFeeders: LazyMridMap<LvFeeder> get() = LazyMridMap(
        getter = { _normalEnergizedLvFeedersById },
        setter = { _normalEnergizedLvFeedersById = it },
        owner = { this },
        elementDescription = "An LvFeeder"
    )

    /**
     * The LV feeders that are currently energized by the feeder. The returned collection is read only.
     */
    @ZBEX
    val currentEnergizedLvFeeders: LazyMridMap<LvFeeder> get() = LazyMridMap(
        getter = { _currentEnergizedLvFeedersById },
        setter = { _currentEnergizedLvFeedersById = it },
        owner = { this },
        elementDescription = "An LvFeeder"
    )

    @ZBEX
    val normalEnergizedLvSubstations: LazyMridMap<LvSubstation> get() = LazyMridMap(
        getter = { _normalEnergizedLvSubstationsById },
        setter = { _normalEnergizedLvSubstationsById = it },
        owner = { this },
        elementDescription = "An LvSubstation"
    )

    @ZBEX
    val currentEnergizedLvSubstations: LazyMridMap<LvSubstation> get() = LazyMridMap(
        getter = { _currentEnergizedLvSubstationsById },
        setter = { _currentEnergizedLvSubstationsById = it },
        owner = { this },
        elementDescription = "An LvSubstation"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding dict.

    // region currentEquipment boilerplate

    @Deprecated(
        message = "Use currentEquipment.add(currentEquipment) instead.",
        replaceWith = ReplaceWith("also { it.currentEquipment.add(currentEquipment) }")
    )
    override fun addCurrentEquipment(equipment: Equipment): Feeder {
        // Note: Have to override return type
        currentEquipment.add(equipment)
        return this
    }

    @Deprecated(
        message = "Use currentEquipment.clear() instead.",
        replaceWith = ReplaceWith("also { it.currentEquipment.clear() }")
    )
    override fun clearCurrentEquipment(): Feeder {
        // Note: Have to override return type
        currentEquipment.clear()
        return this
    }

    // endregion

    // region normalEnergizedLvFeeders boilerplate

    @Deprecated(
        message = "Use normalEnergizedLvFeeders.size instead.",
        replaceWith = ReplaceWith("normalEnergizedLvFeeders.size")
    )
    fun numNormalEnergizedLvFeeders(): Int = normalEnergizedLvFeeders.size

    @Deprecated(
        message = "Use normalEnergizedLvFeeders.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("normalEnergizedLvFeeders.getByMrid(mRID)")
    )
    fun getNormalEnergizedLvFeeder(mRID: String): LvFeeder? = normalEnergizedLvFeeders.getByMrid(mRID)

    @Deprecated(
        message = "Use normalEnergizedLvFeeders.add(lvFeeder) instead.",
        replaceWith = ReplaceWith("also { it.normalEnergizedLvFeeders.add(lvFeeder) }")
    )
    fun addNormalEnergizedLvFeeder(lvFeeder: LvFeeder): Feeder {
        normalEnergizedLvFeeders.add(lvFeeder)
        return this
    }

    @Deprecated(
        message = "Use normalEnergizedLvFeeders.remove(lvFeeder) instead.",
        replaceWith = ReplaceWith("normalEnergizedLvFeeders.remove(lvFeeder)")
    )
    fun removeNormalEnergizedLvFeeder(lvFeeder: LvFeeder): Boolean =

    // endregion

        normalEnergizedLvFeeders.remove(lvFeeder)

    @Deprecated(
        message = "Use normalEnergizedLvFeeders.clear() instead.",
        replaceWith = ReplaceWith("also { it.normalEnergizedLvFeeders.clear() }")
    )
    fun clearNormalEnergizedLvFeeders(): Feeder {
        normalEnergizedLvFeeders.clear()
        return this
    }



    @Deprecated(
        message = "Use currentEnergizedLvFeeders.size instead.",
        replaceWith = ReplaceWith("currentEnergizedLvFeeders.size")
    )
    fun numCurrentEnergizedLvFeeders(): Int = currentEnergizedLvFeeders.size

    @Deprecated(
        message = "Use currentEnergizedLvFeeders.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("currentEnergizedLvFeeders.getByMrid(mRID)")
    )
    fun getCurrentEnergizedLvFeeder(mRID: String): LvFeeder? = currentEnergizedLvFeeders.getByMrid(mRID)

    @Deprecated(
        message = "Use currentEnergizedLvFeeders.add(lvFeeder) instead.",
        replaceWith = ReplaceWith("also { it.currentEnergizedLvFeeders.add(lvFeeder) }")
    )
    fun addCurrentEnergizedLvFeeder(lvFeeder: LvFeeder): Feeder {
        currentEnergizedLvFeeders.add(lvFeeder)
        return this
    }

    @Deprecated(
        message = "Use currentEnergizedLvFeeders.remove(lvFeeder) instead.",
        replaceWith = ReplaceWith("currentEnergizedLvFeeders.remove(lvFeeder)")
    )
    fun removeCurrentEnergizedLvFeeder(lvFeeder: LvFeeder): Boolean =
        currentEnergizedLvFeeders.remove(lvFeeder)

    @Deprecated(
        message = "Use currentEnergizedLvFeeders.clear() instead.",
        replaceWith = ReplaceWith("also { it.currentEnergizedLvFeeders.clear() }")
    )
    fun clearCurrentEnergizedLvFeeders(): Feeder {
        currentEnergizedLvFeeders.clear()
        return this
    }



    @Deprecated(
        message = "Use normalEnergizedLvSubstations.size instead.",
        replaceWith = ReplaceWith("normalEnergizedLvSubstations.size")
    )
    fun numNormalEnergizedLvSubstations(): Int = normalEnergizedLvSubstations.size

    @Deprecated(
        message = "Use normalEnergizedLvSubstations.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("normalEnergizedLvSubstations.getByMrid(mRID)")
    )
    fun getNormalEnergizedLvSubstation(mRID: String): LvSubstation? = normalEnergizedLvSubstations.getByMrid(mRID)

    @Deprecated(
        message = "Use normalEnergizedLvSubstations.add(lvSubstation) instead.",
        replaceWith = ReplaceWith("also { it.normalEnergizedLvSubstations.add(lvSubstation) }")
    )
    fun addNormalEnergizedLvSubstation(lvSubstation: LvSubstation): Feeder {
        normalEnergizedLvSubstations.add(lvSubstation)
        return this
    }

    @Deprecated(
        message = "Use normalEnergizedLvSubstations.remove(lvSubstation) instead.",
        replaceWith = ReplaceWith("normalEnergizedLvSubstations.remove(lvSubstation)")
    )
    fun removeNormalEnergizedLvSubstation(lvSubstation: LvSubstation): Boolean =
        normalEnergizedLvSubstations.remove(lvSubstation)

    @Deprecated(
        message = "Use normalEnergizedLvSubstations.clear() instead.",
        replaceWith = ReplaceWith("also { it.normalEnergizedLvSubstations.clear() }")
    )
    fun clearNormalEnergizedLvSubstations(): Feeder {
        normalEnergizedLvSubstations.clear()
        return this
    }



    @Deprecated(
        message = "Use currentEnergizedLvSubstations.size instead.",
        replaceWith = ReplaceWith("currentEnergizedLvSubstations.size")
    )
    fun numCurrentEnergizedLvSubstations(): Int = currentEnergizedLvSubstations.size

    @Deprecated(
        message = "Use currentEnergizedLvSubstations.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("currentEnergizedLvSubstations.getByMrid(mRID)")
    )
    fun getCurrentEnergizedLvSubstation(mRID: String): LvSubstation? = currentEnergizedLvSubstations.getByMrid(mRID)

    @Deprecated(
        message = "Use currentEnergizedLvSubstations.add(lvSubstation) instead.",
        replaceWith = ReplaceWith("also { it.currentEnergizedLvSubstations.add(lvSubstation) }")
    )
    fun addCurrentEnergizedLvSubstation(lvSubstation: LvSubstation): Feeder {
        currentEnergizedLvSubstations.add(lvSubstation)
        return this
    }

    @Deprecated(
        message = "Use currentEnergizedLvSubstations.remove(lvSubstation) instead.",
        replaceWith = ReplaceWith("currentEnergizedLvSubstations.remove(lvSubstation)")
    )
    fun removeCurrentEnergizedLvSubstation(lvSubstation: LvSubstation): Boolean =
        currentEnergizedLvSubstations.remove(lvSubstation)

    @Deprecated(
        message = "Use currentEnergizedLvSubstations.clear() instead.",
        replaceWith = ReplaceWith("also { it.currentEnergizedLvSubstations.clear() }")
    )
    fun clearCurrentEnergizedLvSubstations(): Feeder {
        currentEnergizedLvSubstations.clear()
        return this
    }

    // endregion

}
