/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.boilerplate.Backfill
import com.zepben.ewb.boilerplate.MridList
import com.zepben.ewb.boilerplate.RefMridList
import com.zepben.ewb.services.common.extensions.asUnmodifiable


/**
 * The parts of the AC power system that are designed to carry current or that are conductively connected through terminals.
 *
 * @property baseVoltage Base voltage of this conducting equipment.  Use only when there is no voltage level container used and only one base
 *                       voltage applies.  For example, not used for transformers.
 */
abstract class ConductingEquipment(mRID: String) : Equipment(mRID) {

    var baseVoltage: BaseVoltage? = null
    private val _terminals: MutableList<Terminal> = mutableListOf()

    /**
     * @return The value of the nominal voltage for the base voltage.
     */
    val baseVoltageValue: Int
        get() {
            return baseVoltage?.nominalVoltage ?: 0
        }

    internal val terminalsInternal: TerminalList get() = RefMridList(
        _terminals,
        this,
        "A Terminal",
        backfill = Backfill(
            { it._conductingEquipment },
            { it, ce -> it._conductingEquipment = ce },
            Terminal::conductingEquipment
        ),
        validate = { validateTerminal(it) },
        sortBy = { it.sequenceNumber }
    )

    /**
     * Conducting equipment have terminals that may be connected to other conducting equipment terminals
     * via connectivity nodes or topological nodes.
     *
     * The returned collection is read only.
     */
     val terminals: List<Terminal> = _terminals.asUnmodifiable()



    /**
     * The maximum number of terminals that this conducting equipment can have.
     */
    open val maxTerminals: Int get() = Int.MAX_VALUE

    private fun validateTerminal(terminal: Terminal) {

        check(numTerminals() < maxTerminals) {
            "Unable to add ${terminal.typeNameAndMRID()} to ${typeNameAndMRID()}. This conducting equipment already has the maximum number of terminals ($maxTerminals)."
        }

        if (terminal.sequenceNumber == 0)
            terminal.sequenceNumber = numTerminals() + 1
        require(terminalsInternal.getByNumber(terminal.sequenceNumber) == null) { "Unable to add ${terminal.typeNameAndMRID()} to ${typeNameAndMRID()}. A ${getTerminal(terminal.sequenceNumber)!!.typeNameAndMRID()} already exists with sequenceNumber ${terminal.sequenceNumber}." }

    }

    /**
     * Helper to get the first terminal for a [ConductingEquipment]. Will throw a [NullPointerException] if the terminal does not exist, so only call it
     * when you know the terminal will be there.
     */
    val t1: Terminal get() = terminalsInternal.getByNumber(1)!!

    /**
     * Helper to get the second terminal for a [ConductingEquipment]. Will throw a [NullPointerException] if the terminal does not exist, so only call it
     * when you know the terminal will be there.
     */
    val t2: Terminal get() = terminalsInternal.getByNumber(2)!!

    /**
     * Helper to get the third terminal for a [ConductingEquipment]. Will throw a [NullPointerException] if the terminal does not exist, so only call it
     * when you know the terminal will be there.
     */
    val t3: Terminal get() = terminalsInternal.getByNumber(3)!!


    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region terminals boilerplate

    @Deprecated(
        message = "Use terminalsInternal.size instead.",
        replaceWith = ReplaceWith("terminalsInternal.size")
    )
    fun numTerminals(): Int = terminalsInternal.size

    @Deprecated(
        message = "Use terminalsInternal.getByMrid(mRID) instead.",
        replaceWith = ReplaceWith("terminalsInternal.getByMrid(mRID)")
    )
    fun getTerminal(mRID: String): Terminal? = terminalsInternal.getByMrid(mRID)

    @Deprecated(
        message = "Use terminalsInternal.getByNumber(sequenceNumber) instead.",
        replaceWith = ReplaceWith("terminalsInternal.getByNumber(sequenceNumber)")
    )
    fun getTerminal(sequenceNumber: Int): Terminal? = terminalsInternal.getByNumber(sequenceNumber)

    @Deprecated(
        message = "Use terminalsInternal.addInternal(terminal) instead.",
        replaceWith = ReplaceWith("also { it.terminalsInternal.addInternal(terminal) }")
    )
    fun addTerminal(terminal: Terminal): ConductingEquipment {
        terminalsInternal.add(terminal)
        return this
    }

    @Deprecated(
        message = "Use terminalsInternal.removeInternal(terminal) instead.",
        replaceWith = ReplaceWith("terminalsInternal.removeInternal(terminal)")
    )
    fun removeTerminal(terminal: Terminal): Boolean =
        terminalsInternal.remove(terminal)

    @Deprecated(
        message = "Use terminalsInternal.clear() instead.",
        replaceWith = ReplaceWith("also { it.terminalsInternal.clear() }")
    )
    fun clearTerminals(): ConductingEquipment {
        terminalsInternal.clear()
        return this
    }

    // endregion

    // endregion
}

typealias TerminalList = MridList<Terminal>

fun TerminalList.getByNumber(sequenceNumber: Int): Terminal? =
        firstOrNull { it.sequenceNumber == sequenceNumber }
