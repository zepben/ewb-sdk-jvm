/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
