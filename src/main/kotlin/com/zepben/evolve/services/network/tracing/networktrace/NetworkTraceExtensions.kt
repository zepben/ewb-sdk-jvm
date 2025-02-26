/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal


/**
 * Convenience extension function for [NetworkTrace.addStartItem] allowing you not to have to pass in `Unit` as the data when the [NetworkTrace] `T` is of type [Unit]
 *
 * @param start The starting terminal for the trace.
 * @param phases Phases to trace; `null` to ignore phases.
 */
fun NetworkTrace<Unit>.addStartItem(start: Terminal, phases: PhaseCode? = null): NetworkTrace<Unit> {
    addStartItem(start, Unit, phases)
    return this
}

/**
 * Convenience extension function for [NetworkTrace.addStartItem] allowing you not to have to pass in `Unit` as the data when the [NetworkTrace] `T` is of type [Unit]
 *
 * @param start The starting equipment to add each terminal of to the start equipment of the trace.
 * @param phases Phases to trace; `null` to ignore phases.
 */
fun NetworkTrace<Unit>.addStartItem(start: ConductingEquipment, phases: PhaseCode? = null): NetworkTrace<Unit> {
    addStartItem(start, Unit, phases)
    return this
}

/**
 * Convenience extension function for [NetworkTrace.run] allowing you not to have to pass in `Unit` as the data when the [NetworkTrace] `T` is of type [Unit]
 *
 * @param start The starting terminal for the trace.
 * @param phases Phases to trace; `null` to ignore phases.
 */
fun NetworkTrace<Unit>.run(start: Terminal, phases: PhaseCode? = null, canStopOnStartItem: Boolean = true): NetworkTrace<Unit> {
    this.run(start, Unit, phases, canStopOnStartItem)
    return this
}

/**
 * Convenience extension function for [NetworkTrace.run] allowing you not to have to pass in `Unit` as the data when the [NetworkTrace] `T` is of type [Unit]
 *
 * @param start The starting equipment to add each terminal of to the start equipment of the trace.
 * @param phases Phases to trace; `null` to ignore phases.
 */
fun NetworkTrace<Unit>.run(start: ConductingEquipment, phases: PhaseCode? = null, canStopOnStartItem: Boolean = true): NetworkTrace<Unit> {
    this.run(start, Unit, phases, canStopOnStartItem)
    return this
}
