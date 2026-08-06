/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.feeder

import com.zepben.ewb.boilerplate.collections.LazyMridMap
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.EquipmentContainer
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.wires.Switch

/**
 * [ZBEX] A collection of equipment for purposes other than generation or utilization, through which electric energy in bulk is passed for the distribution of energy to low voltage network.
 * @property normalEnergizingFeeders [ZBEX] The Feeders that nominally energize the substation. Also used for naming purposes.
 * @property normalEnergizedLvFeeders [ZBEX] The LvFeeders that are nominally energized by this LvSubstation. Also used for naming purposes.
 * @property currentEnergizingFeeders [ZBEX] The Feeders that currently energize the substation. Also used for naming purposes.
 */
@ZBEX
class LvSubstation(mRID: String) : EquipmentContainer(mRID) {

    private var _normalEnergizingFeedersById: MutableMap<String, Feeder>? = null
    private var _currentEnergizingFeedersById: MutableMap<String, Feeder>? = null
    private var _normalEnergizedLvFeedersById: MutableMap<String, LvFeeder>? = null

    /**
     * [ZBEX] The HV/MV feeders that normally energize this [LvSubstation]. The returned collection is read only.
     */
    val normalEnergizingFeeders: MridCollection<Feeder> get() = LazyMridMap(
        getter = { _normalEnergizingFeedersById },
        setter = { _normalEnergizingFeedersById = it },
        owner = this,
        elementDescription = "A Feeder"
    )

    @ZBEX
    val normalEnergizedLvFeeders: MridCollection<LvFeeder> get() = LazyMridMap(
        getter = { _normalEnergizedLvFeedersById },
        setter = { _normalEnergizedLvFeedersById = it },
        owner = this,
        elementDescription = "An LvFeeder"
    )

    /**
     * [ZBEX] The HV/MV feeders that currently energize this LV substation. The returned collection is read only.
     */
    @ZBEX
    val currentEnergizingFeeders: MridCollection<Feeder> get() = LazyMridMap(
        getter = { _currentEnergizingFeedersById },
        setter = { _currentEnergizingFeedersById = it },
        owner = this,
        elementDescription = "A Feeder"
    )

    /**
     * Retrieves all normally energized LvFeeders that represent low voltage network connected below a switch on the edge of this LvSubstation. This is all LvFeeders in the normalEnergizedLvFeeders that has a normalHeadTerminal attached to a Switch.
     */
    fun normalEnergizedLvSwitchFeeders(): List<LvFeeder> = normalEnergizedLvFeeders.filter { it.normalHeadTerminal?.conductingEquipment is Switch }


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
    fun addNormalEnergizingFeeder(feeder: Feeder): LvSubstation = apply {
        normalEnergizingFeeders.add(feeder)
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
    fun clearNormalEnergizingFeeders(): LvSubstation = apply {
        normalEnergizingFeeders.clear()
    }



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
    fun addNormalEnergizedLvFeeder(lvFeeder: LvFeeder): LvSubstation = apply {
        normalEnergizedLvFeeders.add(lvFeeder)
    }

    @Deprecated(
        message = "Use normalEnergizedLvFeeders.remove(lvFeeder) instead.",
        replaceWith = ReplaceWith("normalEnergizedLvFeeders.remove(lvFeeder)")
    )
    fun removeNormalEnergizedLvFeeder(lvFeeder: LvFeeder): Boolean =
        normalEnergizedLvFeeders.remove(lvFeeder)

    @Deprecated(
        message = "Use normalEnergizedLvFeeders.clear() instead.",
        replaceWith = ReplaceWith("also { it.normalEnergizedLvFeeders.clear() }")
    )
    fun clearNormalEnergizedLvFeeders(): LvSubstation = apply {
        normalEnergizedLvFeeders.clear()
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
    fun addCurrentEnergizingFeeder(feeder: Feeder): LvSubstation = apply {
        currentEnergizingFeeders.add(feeder)
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
    fun clearCurrentEnergizingFeeders(): LvSubstation = apply {
        currentEnergizingFeeders.clear()
    }

    //endregion

}
