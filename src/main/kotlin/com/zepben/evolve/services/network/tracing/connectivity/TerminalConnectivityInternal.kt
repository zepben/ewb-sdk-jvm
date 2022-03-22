/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.services.network.tracing.phases.NominalPhasePath
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

/**
 * Helper class for finding the paths through equipment.
 */
class TerminalConnectivityInternal {

    /**
     * Find the connectivity between the two terminals. The function assumes they are on the same conducting equipment.
     *
     * @param terminal The terminal you are moving from
     * @param otherTerminal The terminal you are moving to
     * @param includePhases The nominal phases on the [terminal] you want to use.
     *
     * @return The connectivity between [terminal] and [otherTerminal]. If the conducting equipment is a power transformer the nominal phase paths may
     *         contain an entry with the 'from' phase set to NONE and the 'to' phase set to N, indicating a neutral has been added by the transformer1.
     */
    @JvmOverloads
    fun between(
        terminal: Terminal,
        otherTerminal: Terminal,
        includePhases: Set<SPK> = terminal.phases.singlePhases.toSet()
    ): ConnectivityResult =
        if (terminal.conductingEquipment is PowerTransformer)
            transformerTerminalConnectivity(terminal, otherTerminal, includePhases)
        else
            straightTerminalConnectivity(terminal, otherTerminal, includePhases)

    private fun transformerTerminalConnectivity(
        terminal: Terminal,
        otherTerminal: Terminal,
        includePhases: Set<SPK>
    ): ConnectivityResult =
        ConnectivityResult.between(
            terminal,
            otherTerminal,
            (TransformerPhasePaths.lookup[terminal.phases]?.let { it[otherTerminal.phases] } ?: emptyList())
                .filter { (it.from in includePhases) || (it.from == SPK.NONE) }
        )

    fun straightTerminalConnectivity(
        terminal: Terminal,
        otherTerminal: Terminal,
        includePhases: Set<SPK>
    ): ConnectivityResult =
        ConnectivityResult.between(
            terminal,
            otherTerminal,
            terminal.phases.singlePhases.toSet()
                .intersect(otherTerminal.phases.singlePhases.toSet())
                .filter { it in includePhases }
                .map { NominalPhasePath(it, it) }
        )

}
