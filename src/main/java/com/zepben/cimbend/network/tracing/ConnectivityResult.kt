/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.network.tracing

import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment
import com.zepben.cimbend.cim.iec61970.base.core.Terminal
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.cimbend.network.model.NominalPhasePath
import java.util.*
import java.util.stream.Collectors

/**
 * Stores the connectivity between two terminals, including the mapping between the nominal phases.
 */
class ConnectivityResult private constructor(
    private val fromTerminal: Terminal,
    private val toTerminal: Terminal,
    nominalPhasePaths: Collection<NominalPhasePath>
) {
    private val nominalPhasePaths: List<NominalPhasePath>

    /**
     * @return Convenience method for getting the conducting equipment that owns the fromTerminal.
     */
    fun from(): ConductingEquipment? {
        return fromTerminal.conductingEquipment
    }

    /**
     * @return The terminal from which the connectivity was requested.
     */
    fun fromTerminal(): Terminal {
        return fromTerminal
    }

    /**
     * @return Convenience method for getting the conducting equipment that owns the toTerminal.
     */
    fun to(): ConductingEquipment? {
        return toTerminal.conductingEquipment
    }

    /**
     * @return The terminal which is connected to the requested terminal.
     */
    fun toTerminal(): Terminal {
        return toTerminal
    }

    /**
     * @return The nominal phases that are connected in the fromTerminal.
     */
    fun fromNominalPhases(): List<SinglePhaseKind> {
        return nominalPhasePaths.stream().map { obj: NominalPhasePath -> obj.from() }.collect(Collectors.toList())
    }

    /**
     * @return The nominal phases that are connected in the toTerminal.
     */
    fun toNominalPhases(): List<SinglePhaseKind> {
        return nominalPhasePaths.stream().map { obj: NominalPhasePath -> obj.to() }.collect(Collectors.toList())
    }

    /**
     * @return The mapping of nominal phase paths between the from and to terminals.
     */
    fun nominalPhasePaths(): List<NominalPhasePath> {
        return nominalPhasePaths
    }

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
        /**
         * @param fromTerminal The terminal for which the connectivity was requested.
         * @param toTerminal   The terminal which is connected to the requested terminal.
         * @return The ConnectivityResult Builder to use to construct the result.
         */
        fun between(fromTerminal: Terminal, toTerminal: Terminal, nominalPhasePaths: Collection<NominalPhasePath>): ConnectivityResult {
            return ConnectivityResult(fromTerminal, toTerminal, nominalPhasePaths)
        }
    }

    init {
        this.nominalPhasePaths = ArrayList(nominalPhasePaths)
        this.nominalPhasePaths.sortWith(Comparator.comparing { obj: NominalPhasePath -> obj.from() }.thenComparing { obj: NominalPhasePath -> obj.to() })
    }

}
