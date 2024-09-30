/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal

class NetworkTraceStep<T>(
    val path: StepPath,
    val data: T
) {
    operator fun component1(): StepPath = path
    operator fun component2(): T = data
}

data class StepPath(
    val fromTerminal: Terminal,
    val toTerminal: Terminal,
    val numTerminalSteps: Int,
    val numEquipmentSteps: Int,
    // This will need to be added when we add clamps to the model. The proposed idea is that if an AcLineSegment has multiple clamps and your current
    // path is one of the clamps, one of the next StepPaths will be another clamp on the AcLineSegment, essentially jumping over the AcLineSegment for the Path.
    // If there is a cut on the AcLineSegment, you may need to know about it, primarily because the cut could create an open point between the clamps that needs
    // to prevent queuing as part of an "open test".
    // abstract val viaSegment: AcLineSegment? = null,
) {
    val fromEquipment: ConductingEquipment
        get() = fromTerminal.conductingEquipment ?: error("Network trace does not support terminals that do not have conducting equipment")
    val toEquipment: ConductingEquipment
        get() = toTerminal.conductingEquipment ?: error("Network trace does not support terminals that do not have conducting equipment")


    val tracedInternally: Boolean
        get() = fromEquipment == toEquipment
}

// TODO [Review]: This can be removed and we just have one StepPath. fromTerminal and toTerminal will always be non null.
//  MONDAY! Delete this, make the StepPath a final data class and fix everything!
//data class TerminalToTerminalPath(
//    public override val fromTerminal: Terminal,
//    public override val toTerminal: Terminal,
//    override val numTerminalSteps: Int,
//    override val numEquipmentSteps: Int,
//) : StepPath() {
//    override val fromEquipment: ConductingEquipment
//        get() = fromTerminal.conductingEquipment ?: error("Network trace does not support terminals that do not have conducting equipment")
//    override val toEquipment: ConductingEquipment
//        get() = toTerminal.conductingEquipment ?: error("Network trace does not support terminals that do not have conducting equipment")
//}

/**
 * Here for future clamp support. Clamps will always connect equipment to equipment without terminals with AcLineSegment.
 * It probably needs a better name before it is introduced though!
 */
//class DirectEquipmentPath(
//    override val fromEquipment: ConductingEquipment,
//    override val toEquipment: ConductingEquipment,
//    override val nTerminalSteps: Int,
//    override val nEquipmentSteps: Int,
//) : StepPath {
//    override val fromTerminal: Terminal? get() = null
//    override val toTerminal: Terminal? get() = null
//}
