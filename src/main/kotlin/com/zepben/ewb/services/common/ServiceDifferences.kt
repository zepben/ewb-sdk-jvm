/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.ewb.services.common.extensions.asUnmodifiable

class ServiceDifferences internal constructor(
    private val sourceObjLookup: (mRID: String) -> IdentifiedObject?,
    private val targetObjLookup: (mRID: String) -> IdentifiedObject?,
    private val sourceNameTypeLookup: (mRID: String) -> NameType?,
    private val targetNameTypeLookup: (mRID: String) -> NameType?
) {

    private val missingFromTarget = linkedSetOf<String>()
    private val missingFromSource = linkedSetOf<String>()
    private val modifications = linkedMapOf<String, ObjectDifference<*>>()

    fun missingFromTarget(): Set<String> = missingFromTarget.asUnmodifiable()
    fun missingFromSource(): Set<String> = missingFromSource.asUnmodifiable()
    fun modifications(): Map<String, ObjectDifference<*>> = modifications.asUnmodifiable()

    fun addToMissingFromTarget(id: String) {
        missingFromTarget.add(id)
    }

    fun addToMissingFromSource(id: String) {
        missingFromSource.add(id)
    }

    fun addModifications(id: String, difference: ObjectDifference<*>) {
        modifications[id] = difference
    }

    override fun toString(): String {
        val sb = StringBuilder("Missing From Target:")
        sb.indentEach(missingFromTarget) { "${sourceObjLookup(it) ?: sourceNameTypeLookup(it) ?: it}" }

        sb.append("\nMissing From Source:")
        sb.indentEach(missingFromSource) { "${targetObjLookup(it) ?: targetNameTypeLookup(it) ?: it}" }

        sb.append("\nModifications:")
        sb.indentEach(modifications.entries) { (k, v) -> "$k: $v" }

        return sb.toString()
    }

    private fun <T> StringBuilder.indentEach(items: Iterable<T>, format: (T) -> String) =
        items.forEach { append("\n   ").append(format(it)) }

}
