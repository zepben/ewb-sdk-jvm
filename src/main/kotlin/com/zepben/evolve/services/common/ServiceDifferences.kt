/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.common

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import java.util.*

class ServiceDifferences internal constructor(
    private val sourceLookup: (mRID: String) -> IdentifiedObject?,
    private val targetLookup: (mRID: String) -> IdentifiedObject?
) {
    private val missingFromTarget = linkedSetOf<String>()
    private val missingFromSource = linkedSetOf<String>()
    private val modifications = linkedMapOf<String, ObjectDifference<*>>()

    private val missingNameTypeFromTarget = linkedSetOf<String>()
    private val missingNameTypeFromSource = linkedSetOf<String>()
    private val nameTypeDifferences = mutableListOf<NameTypeDifference>()

    fun missingFromTarget(): Set<String> {
        return Collections.unmodifiableSet(missingFromTarget)
    }

    fun missingFromSource(): Set<String> {
        return Collections.unmodifiableSet(missingFromSource)
    }

    fun modifications(): Map<String, ObjectDifference<out IdentifiedObject>> {
        return Collections.unmodifiableMap(modifications)
    }

    fun missingNameTypeFromTarget(): Set<String> = missingNameTypeFromTarget.asUnmodifiable()
    fun missingNameTypeFromSource(): Set<String> = missingNameTypeFromSource.asUnmodifiable()
    fun nameTypeDifferences(): List<NameTypeDifference> = nameTypeDifferences.asUnmodifiable()

    fun addToMissingFromTarget(id: String) {
        missingFromTarget.add(id)
    }

    fun addToMissingFromSource(id: String) {
        missingFromSource.add(id)
    }

    fun addModifications(id: String, difference: ObjectDifference<out IdentifiedObject>) {
        modifications[id] = difference
    }

    fun addMissingNameTypeFromTarget(id: String) {
        missingNameTypeFromTarget.add(id)
    }

    fun addMissingNameTypeFromSource(id: String) {
        missingNameTypeFromSource.add(id)
    }

    fun addNameTypeDifference(diff: NameTypeDifference) {
        nameTypeDifferences.add(diff)
    }

    override fun toString(): String {
        val sb = StringBuilder("Missing From Target:")
        missingFromTarget.forEach { addIndentedLine(sb, sourceLookup(it).toString()) }

        sb.append("\nMissing From Source:")
        missingFromSource.forEach { addIndentedLine(sb, targetLookup(it).toString()) }

        sb.append("\nModifications:")
        modifications.forEach { (k, v) -> addIndentedLine(sb, "$k: $v") }

        sb.append("\nMissing Name Types From Target:")
        missingNameTypeFromTarget.forEach { addIndentedLine(sb, sourceLookup(it).toString()) }

        sb.append("\nMissing Name Types From Source:")
        missingNameTypeFromSource.forEach { addIndentedLine(sb, targetLookup(it).toString()) }

        sb.append("\nName Type differences:")
        nameTypeDifferences.forEach { addIndentedLine(sb, it.toString()) }

        return sb.toString()
    }

    companion object {
        private fun addIndentedLine(sb: StringBuilder, line: String) = sb.append(indentedNewLine).append(line)

        private val indentedNewLine = System.lineSeparator() + "   "
    }
}
