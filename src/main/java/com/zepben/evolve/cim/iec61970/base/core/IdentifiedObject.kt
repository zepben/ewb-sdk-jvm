/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import java.util.*

/**
 * This is a root class to provide common identification for all classes needing identification and naming attributes.
 *
 * @property mRID Master resource identifier issued by a model authority. The mRID is unique within an exchange context. Global uniqueness
 *                is easily achieved by using a UUID,  as specified in RFC 4122, for the mRID. The use of UUID is strongly recommended.
 *                For CIMXML data files in RDF syntax conforming to IEC 61970-552 Edition 1, the mRID is mapped to rdf:ID or rdf:about attributes
 *                that identify CIM object elements.
 * @property name is any free human readable and possibly non unique text naming the object.
 * @property description a free human readable text describing or naming the object. It may be non unique and may not correlate to a naming hierarchy.
 * @property numDiagramObjects Number of DiagramObject's known to associate with this [IdentifiedObject]
 */
abstract class IdentifiedObject(mRID: String = "") {

    private var _names: MutableList<Name>? = null

    val mRID: String = if (mRID.isEmpty()) UUID.randomUUID().toString() else mRID
    var name: String = ""
    var description: String = ""
    var numDiagramObjects: Int = 0

    /**
     * @return True if this [IdentifiedObject] has at least 1 DiagramObject associated with it, false otherwise.
     */
    val hasDiagramObjects: Boolean
        get() = numDiagramObjects > 0

    override fun toString(): String {
        return javaClass.simpleName + "{" +
            "id='" + mRID + '\'' +
            '}'
    }

    /**
     * The names for this identified object. The returned collection is read only.
     */
    val names: Collection<Name> get() = _names.asUnmodifiable()

    /**
     * Get the number of entries in the [Name] collection.
     */
    fun numNames(): Int = _names?.size ?: 0

    /**
     * The individual name information of the identified object.
     *
     * @param name the name of the required [Name]
     * @param type the name of the required [NameType]
     * @return The [Name] with the specified [name] if it exists, otherwise null
     */
    fun getName(type: String, name: String): Name? = _names?.getByTypeAndName(type, name)

    fun addName(name: Name): IdentifiedObject {
        require(name.identifiedObject === this) { "Attempting to add a Name to ${typeNameAndMRID()} that does not reference this identified object" }

        if (getName(name.type.name, name.name) != null)
            return this

        _names = _names ?: mutableListOf()
        _names!!.add(name)

        return this
    }

    fun removeName(name: Name?): Boolean {
        val ret = _names?.remove(name) == true
        if (_names.isNullOrEmpty()) _names = null
        return ret
    }

    fun clearNames(): IdentifiedObject {
        _names = null
        return this
    }

    private fun Iterable<Name>?.getByTypeAndName(type: String,  name: String): Name? {
        return this?.firstOrNull { it.type.name == type && it.name == name }
    }
}
