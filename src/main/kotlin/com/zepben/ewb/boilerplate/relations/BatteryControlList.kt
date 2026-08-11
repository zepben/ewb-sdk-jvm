/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.relations

import com.zepben.ewb.boilerplate.Backfill
import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControlMode
import com.zepben.ewb.cim.iec61970.base.generation.production.BatteryUnit


/** A list of [BatteryControl] for a given [BatteryUnit]. */
class BatteryControlList(
    getter: () -> MutableList<BatteryControl>?,
    setter: (MutableList<BatteryControl>?) -> Unit,
    owner: BatteryUnit,
    elementDescription: String,
    backfill: Backfill<BatteryControl, BatteryUnit>? = null,
    validate: ((BatteryControl) -> Unit)? = null,
    sortBy: ((BatteryControl) -> Comparable<*>?)? = null
) : LazyMridList<BatteryControl, BatteryUnit>(
    getter = getter,
    setter = setter,
    owner = owner,
    elementDescription = elementDescription,
    backfill = backfill,
    validate = validate,
    sortBy = sortBy,
) {

    /**
     * Get a [BatteryControl] of this [BatteryUnit] by its [BatteryControl.controlMode]
     *
     * @param controlMode the control mode of the required [BatteryControl]
     * @return The [BatteryControl] with the specified [BatteryControlMode] if it exists, otherwise null
     */
    fun getByMode(controlMode: BatteryControlMode): BatteryControl? = firstOrNull { it.controlMode == controlMode }

}
