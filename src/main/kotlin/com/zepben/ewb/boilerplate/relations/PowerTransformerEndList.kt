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
import com.zepben.ewb.cim.iec61970.base.core.ConnectivityNode
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.PowerTransformer
import com.zepben.ewb.cim.iec61970.base.wires.PowerTransformerEnd

/** A list of [PowerTransformerEnd] for a given [PowerTransformer]. */
class PowerTransformerEndList(
    getter: () -> MutableList<PowerTransformerEnd>?,
    setter: (MutableList<PowerTransformerEnd>?) -> Unit,
    owner: PowerTransformer,
    elementDescription: String,
    backfill: Backfill<PowerTransformerEnd, PowerTransformer>? = null,
    validate: ((PowerTransformerEnd) -> Unit)? = null,
    sortBy: ((PowerTransformerEnd) -> Comparable<*>?)? = null
) : LazyMridList<PowerTransformerEnd, PowerTransformer>(
    getter = getter,
    setter = setter,
    owner = owner,
    elementDescription = elementDescription,
    backfill = backfill,
    validate = validate,
    sortBy = sortBy,
) {

    /**
     * Get a [PowerTransformerEnd] by its [com.zepben.ewb.cim.iec61970.base.wires.TransformerEnd.endNumber]
     *
     * @param endNumber the end number of the required [PowerTransformerEnd]
     * @return The [PowerTransformerEnd] with the specified [endNumber] if it exists, otherwise null
     */
    fun getByEndNumber(endNumber: Int): PowerTransformerEnd? =
        firstOrNull { it.endNumber == endNumber }

    /**
     * Get a [PowerTransformerEnd] by its [com.zepben.ewb.cim.iec61970.base.wires.TransformerEnd.terminal]
     *
     * @param terminal the terminal of the required [PowerTransformerEnd]
     * @return The [PowerTransformerEnd] with the specified [terminal] if it exists, otherwise null
     */
    fun getByTerminal(terminal: Terminal): PowerTransformerEnd? =
        firstOrNull { it.terminal == terminal }

    /**
     * Get a [PowerTransformerEnd] by its [Terminal] [ConnectivityNode].
     *
     * @param connectivityNode the [ConnectivityNode] of the required [PowerTransformerEnd]
     * @return The [PowerTransformerEnd] with the specified [Terminal] if it exists, otherwise null
     */
    fun getByNode(connectivityNode: ConnectivityNode): PowerTransformerEnd? =
        firstOrNull { it.terminal?.connectivityNode == connectivityNode }

}
