/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.common

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import java.util.*
import kotlin.collections.LinkedHashMap

class ServiceDifferences internal constructor(
    private val sourceLookup: (mRID: String) -> IdentifiedObject?,
    private val targetLookup: (mRID: String) -> IdentifiedObject?
) {
    private val missingFromTarget: MutableSet<String> = LinkedHashSet()
    private val missingFromSource: MutableSet<String> = LinkedHashSet()
    private val modifications: MutableMap<String, ObjectDifference<*>> = LinkedHashMap()

    fun missingFromTarget(): Set<String> {
        return Collections.unmodifiableSet(missingFromTarget)
    }

    fun missingFromSource(): Set<String> {
        return Collections.unmodifiableSet(missingFromSource)
    }

    fun modifications(): Map<String, ObjectDifference<out IdentifiedObject>> {
        return Collections.unmodifiableMap(modifications)
    }

    fun addToMissingFromTarget(id: String) {
        missingFromTarget.add(id)
    }

    fun addToMissingFromSource(id: String) {
        missingFromSource.add(id)
    }

    fun addModifications(id: String, difference: ObjectDifference<out IdentifiedObject>) {
        modifications[id] = difference
    }

    override fun toString(): String {
        val sb = StringBuilder("Missing From Target:")
        missingFromTarget.forEach { addIndentedLine(sb, sourceLookup(it).toString()) }

        sb.append("\nMissing From Source:")
        missingFromSource.forEach { addIndentedLine(sb, targetLookup(it).toString()) }

        sb.append("\nModifications:")
        modifications.forEach { (k, v) -> addIndentedLine(sb, "$k: $v") }

        return sb.toString()
    }

    companion object {
        private fun addIndentedLine(sb: StringBuilder, line: String) = sb.append(indentedNewLine).append(line)

        private val indentedNewLine = System.lineSeparator() + "   "
    }
}
