/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

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

    // Changed to use mutableSet to prevent duplicated entries from addName function
    private var _names: MutableSet<Name>? = null

    val mRID: String = mRID.ifEmpty { UUID.randomUUID().toString() }
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
    val names: Collection<Name> get() = _names?.toSet() ?: emptySet()

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

    /**
     * The individual name information of the identified object.
     *
     * @param name the name of the required [Name]
     * @param type the required [NameType]
     * @return The [Name] with the specified [name] if it exists, otherwise null
     */
    fun getName(type: NameType, name: String): Name? = _names?.getByTypeAndName(type.name, name)

    /**
     * All name information of the identified object given a [NameType].
     *
     * @param type the required [NameType]
     * @return List of [Name] with the specified [type] if it exists, otherwise null
     */
    fun getNames(type: NameType): List<Name>? = _names?.filter { it.type == type }?.takeUnless { it.isEmpty() }

    /**
     * All name information of the identified object given the name of a [NameType].
     *
     * @param type the name of the required [NameType]
     * @return List of [Name] with the specified [type] if it exists, otherwise null
     */
    fun getNames(type: String): List<Name>? = _names?.filter { it.type.name == type }?.takeUnless { it.isEmpty() }

    /**
     * Add a [Name] to the [IdentifiedObject]
     *
     * @param type the required [NameType]
     * @param name the name of the new [Name]
     * @return this [IdentifiedObject] with a newly added [Name]
     */
    fun addName(type: NameType, name: String): IdentifiedObject {

        _names = _names ?: mutableSetOf()
        _names!!.add(type.getOrAddName(name, this))

        return this
    }

    /**
     * Remove a [Name] from the [IdentifiedObject]
     *
     * @param name the [Name] to be removed from the [IdentifiedObject]
     * @return A [Boolean] to indicate if the [name] is successfully removed
     */
    fun removeName(name: Name?): Boolean {
        val ret = _names?.remove(name) == true
        // Remove names from nameType if nameType contains the name
        if (ret) name!!.type.removeName(name)
        if (_names.isNullOrEmpty()) _names = null
        return ret
    }

    /**
     * Remove all [Name] from the [IdentifiedObject]
     *
     * @return this [IdentifiedObject]
     */
    fun clearNames(): IdentifiedObject {
        // Remove names from nameType
        _names?.toList()?.forEach {
            removeName(it)
        }
        _names = null
        return this
    }

    private fun Iterable<Name>?.getByTypeAndName(type: String, name: String): Name? {
        return this?.firstOrNull { it.type.name == type && it.name == name }
    }
}
