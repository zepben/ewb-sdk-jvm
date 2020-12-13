/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.tracing.phases.NominalPhasePath

/**
 * Stores the connectivity between two terminals, including the mapping between the nominal phases.
 *
 * @property fromTerminal The terminal from which the connectivity was requested.
 * @property toTerminal The terminal which is connected to the requested terminal.
 * @property nominalPhasePaths The mapping of nominal phase paths between the from and to terminals.
 *
 * @property from the conducting equipment that owns the fromTerminal.
 * @property to the conducting equipment that owns the toTerminal.
 * @property fromNominalPhases The nominal phases that are connected in the fromTerminal.
 * @property toNominalPhases The nominal phases that are connected in the toTerminal.
 */
@Suppress("MemberVisibilityCanBePrivate")
class ConnectivityResult private constructor(
    val fromTerminal: Terminal,
    val toTerminal: Terminal,
    val nominalPhasePaths: List<NominalPhasePath>
) {
    val from: ConductingEquipment? get() = fromTerminal.conductingEquipment
    val to: ConductingEquipment? get() = toTerminal.conductingEquipment

    val fromNominalPhases: List<SinglePhaseKind> get() = nominalPhasePaths.map { it.from }
    val toNominalPhases: List<SinglePhaseKind> get() = nominalPhasePaths.map { it.to }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConnectivityResult

        if (fromTerminal != other.fromTerminal) return false
        if (toTerminal != other.toTerminal) return false
        if (nominalPhasePaths != other.nominalPhasePaths) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fromTerminal.hashCode()
        result = 31 * result + toTerminal.hashCode()
        result = 31 * result + nominalPhasePaths.hashCode()
        return result
    }

    override fun toString(): String {
        return "ConnectivityResult{" +
            "fromTerminal=${fromTerminal.mRID}" +
            ", toTerminal=${toTerminal.mRID}" +
            ", nominalPhasePaths=$nominalPhasePaths" +
            '}'
    }

    companion object {

        private val sortByPath: Comparator<NominalPhasePath> = compareBy<NominalPhasePath> { it.from }.thenBy { it.to }

        /**
         * @param fromTerminal The terminal for which the connectivity was requested.
         * @param toTerminal   The terminal which is connected to the requested terminal.
         * @return The ConnectivityResult Builder to use to construct the result.
         */
        fun between(fromTerminal: Terminal, toTerminal: Terminal, nominalPhasePaths: Collection<NominalPhasePath>): ConnectivityResult =
            ConnectivityResult(fromTerminal, toTerminal, nominalPhasePaths.sortedWith(sortByPath))

    }

}
