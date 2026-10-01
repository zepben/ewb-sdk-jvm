/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.relations

import com.zepben.ewb.boilerplate.collections.LazyList
import com.zepben.ewb.cim.iec61970.base.wires.PerLengthPhaseImpedance
import com.zepben.ewb.cim.iec61970.base.wires.PhaseImpedanceData
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind


/** A list of [PhaseImpedanceData] for a given [PerLengthPhaseImpedance]. */
class PhaseImpedanceDataList(
    getter: () -> MutableList<PhaseImpedanceData>?,
    setter: (MutableList<PhaseImpedanceData>?) -> Unit,
    validate: ((PhaseImpedanceData) -> Unit)? = null,
    sortBy: ((PhaseImpedanceData) -> Comparable<*>?)? = null
): LazyList<PhaseImpedanceData>(
    getter = getter,
    setter = setter,
    validate = validate,
    sortBy = sortBy
) {

    /**
     * Get the matrix entry for the corresponding to and from phases.
     *
     * @param fromPhase The "from" phase to lookup.
     * @param toPhase The "to" phase to lookup.
     * @return The matching [PhaseImpedanceData] or null if none was found.
     */
    operator fun get(fromPhase: SinglePhaseKind, toPhase: SinglePhaseKind): PhaseImpedanceData? =
        firstOrNull { it.fromPhase == fromPhase && it.toPhase == toPhase }

    /**
     * Get only the diagonal elements of the matrix, i.e toPhase == fromPhase.
     */
    fun diagonal(): List<PhaseImpedanceData> = filter { it.toPhase == it.fromPhase }

}
