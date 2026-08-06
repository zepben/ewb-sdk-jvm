/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.relations

import com.zepben.ewb.boilerplate.Backfill
import com.zepben.ewb.boilerplate.collections.MridList
import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.cim.iec61970.base.core.Terminal

/** A list of [Terminal] for a given [ConductingEquipment]. */
class TerminalList(
    list: MutableList<Terminal> = mutableListOf(),
    owner: ConductingEquipment,
    elementDescription: String,
    backfill: Backfill<Terminal, ConductingEquipment>? = null,
    validate: ((Terminal) -> Unit)? = null,
    sortBy: ((Terminal) -> Comparable<*>?)? = null,
) : MridList<Terminal, ConductingEquipment>(
    list = list,
    owner = owner,
    elementDescription = elementDescription,
    backfill = backfill,
    validate = validate,
    sortBy = sortBy,
) {

    /** Returns the terminal with [sequenceNumber], or `null`. */
    fun getByNumber(sequenceNumber: Int): Terminal? =
        firstOrNull { it.sequenceNumber == sequenceNumber }
}
