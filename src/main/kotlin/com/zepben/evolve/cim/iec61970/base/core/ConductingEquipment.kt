/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.getByMRID
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.common.extensions.validateReference

/**
 * The parts of the AC power system that are designed to carry current or that are conductively connected through terminals.
 *
 * @property baseVoltage Base voltage of this conducting equipment.  Use only when there is no voltage level container used and only one base
 *                       voltage applies.  For example, not used for transformers.
 */
abstract class ConductingEquipment(mRID: String = "") : Equipment(mRID) {

    var baseVoltage: BaseVoltage? = null
    private val _terminals: MutableList<Terminal> = mutableListOf()

    /**
     * @return The value of the nominal voltage for the base voltage.
     */
    val baseVoltageValue: Int
        get() {
            return baseVoltage?.nominalVoltage ?: 0
        }

    /**
     * Conducting equipment have terminals that may be connected to other conducting equipment terminals
     * via connectivity nodes or topological nodes.
     *
     * The returned collection is read only.
     */
    val terminals: List<Terminal> get() = _terminals.asUnmodifiable()

    /**
     * Get the number of entries in the [Terminal] collection.
     */
    fun numTerminals(): Int = _terminals.size

    /**
     * Conducting equipment have terminals that may be connected to other conducting equipment terminals
     * via connectivity nodes or topological nodes.
     *
     * @param mRID the mRID of the required [Terminal]
     * @return The [Terminal] with the specified [mRID] if it exists, otherwise null
     */
    fun getTerminal(mRID: String): Terminal? = _terminals.getByMRID(mRID)

    /**
     * Conducting equipment have terminals that may be connected to other conducting equipment terminals
     * via connectivity nodes or topological nodes.
     *
     * @param sequenceNumber the sequence number of the required [Terminal]
     * @return The [Terminal] with the specified [sequenceNumber] if it exists, otherwise null
     */
    fun getTerminal(sequenceNumber: Int): Terminal? = _terminals.firstOrNull { it.sequenceNumber == sequenceNumber }

    /**
     * Add a [Terminal] to this [ConductingEquipment]
     *
     * If [Terminal.sequenceNumber] is 0 [terminal] will receive a sequenceNumber of [numTerminals] + 1 when added.
     * @throws IllegalStateException if the [Terminal] references another [ConductingEquipment] or if a [Terminal] with
     *         the same sequenceNumber already exists.
     * @return This [ConductingEquipment] for fluent use
     */
    fun addTerminal(terminal: Terminal): ConductingEquipment {
        if (validateTerminal(terminal)) return this

        if (terminal.sequenceNumber == 0)
            terminal.sequenceNumber = numTerminals() + 1
        require(getTerminal(terminal.sequenceNumber) == null) { "Unable to add ${terminal.typeNameAndMRID()} to ${typeNameAndMRID()}. A ${getTerminal(terminal.sequenceNumber)!!.typeNameAndMRID()} already exists with sequenceNumber ${terminal.sequenceNumber}." }

        _terminals.add(terminal)
        _terminals.sortBy { it.sequenceNumber }

        return this
    }

    fun removeTerminal(terminal: Terminal): Boolean = _terminals.remove(terminal)

    fun clearTerminals(): ConductingEquipment {
        _terminals.clear()
        return this
    }

    private fun validateTerminal(terminal: Terminal): Boolean {
        if (validateReference(terminal, ::getTerminal, "A Terminal"))
            return true

        if (terminal.conductingEquipment == null)
            terminal.conductingEquipment = this

        require(terminal.conductingEquipment === this) {
            "${terminal.typeNameAndMRID()} `conductingEquipment` property references ${terminal.conductingEquipment!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }
        return false
    }

    /**
     * Helper to get the first terminal for a [ConductingEquipment]. Will throw a [NullPointerException] if the terminal does not exist, so only call it
     * when you know the terminal will be there.
     */
    val t1: Terminal get() = getTerminal(1)!!

    /**
     * Helper to get the second terminal for a [ConductingEquipment]. Will throw a [NullPointerException] if the terminal does not exist, so only call it
     * when you know the terminal will be there.
     */
    val t2: Terminal get() = getTerminal(2)!!

    /**
     * Helper to get the third terminal for a [ConductingEquipment]. Will throw a [NullPointerException] if the terminal does not exist, so only call it
     * when you know the terminal will be there.
     */
    val t3: Terminal get() = getTerminal(3)!!
    
}
