/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.services.common.extensions.asUnmodifiable

/**
 * Type of name. Possible values for attribute 'name' are implementation dependent but standard profiles may specify types. An enterprise may have multiple
 * IT systems each having its own local name for the same object, e.g. a planning system may have different names from an EMS. An object may also have
 * different names within the same IT system, e.g. localName as defined in CIM version 14. The definition from CIM14 is:
 * The localName is a human readable name of the object. It is a free text name local to a node in a naming hierarchy similar to a file directory structure.
 * A power system related naming hierarchy may be: Substation, VoltageLevel, Equipment etc. Children of the same parent in such a hierarchy have names that
 * typically are unique among them.
 *
 * @property name Name of the name type.
 * @property description Description of the name type.
 */
class NameType(val name: String) {

    private var namesIndex: MutableMap<String, Name> = mutableMapOf()
    private var namesMultiIndex: MutableMap<String, MutableList<Name>> = mutableMapOf()

    var description: String = ""

    /**
     * All names of this type.
     */
    val names: Sequence<Name> get() = namesMultiIndex.values.asSequence().flatten() + namesIndex.values.asSequence()

    fun hasName(name: String): Boolean = namesIndex.containsKey(name) || namesMultiIndex.containsKey(name)

    /**
     * Get all the [Name] instances for the provided [name].
     *
     * @return A list of [Name]
     */
    fun getNames(name: String): List<Name> =
        namesIndex[name]?.let { listOf(it) } ?: namesMultiIndex[name].asUnmodifiable()

    /**
     * Gets a [Name] for the given [name] and [identifiedObject] combination
     * or adds a new [Name] to this name type with the combination and returns
     * the new instance.
     */
    fun getOrAddName(name: String, identifiedObject: IdentifiedObject): Name {
        return when {
            namesIndex.containsKey(name) -> {
                val existing = namesIndex[name]!!
                if (existing.identifiedObject == identifiedObject) {
                    existing
                } else {
                    Name(name, this, identifiedObject).also {
                        namesMultiIndex[name] = mutableListOf(existing, it)
                        namesIndex.remove(name)
                    }
                }
            }
            namesMultiIndex.containsKey(name) -> {
                val names = namesMultiIndex[name]!!
                var nameObj = names.find { it.identifiedObject == identifiedObject }
                if (nameObj == null) {
                    nameObj = Name(name, this, identifiedObject)
                    names.add(nameObj)
                }
                nameObj
            }
            else -> {
                Name(name, this, identifiedObject).also {
                    namesIndex[name] = it
                }
            }
        }
    }

    /**
     * Removes the [name] from this name type.
     *
     * @return true if the name instance was sucessfully removed
     */
    fun removeName(name: Name): Boolean {
        if (name.type !== this) {
            return false
        }

        return when (namesIndex.remove(name.name)) {
            null -> {
                val removed = namesMultiIndex[name.name]?.remove(name) ?: false
                if (removed && namesMultiIndex[name.name]?.isEmpty() == true) {
                    namesMultiIndex.remove(name.name)
                }
                removed
            }
            else -> true
        }
    }

    /**
     * Removes all [Name] instances associated with name [name].
     *
     * @return true if a matching name was removed.
     */
    fun removeNames(name: String?): Boolean = (namesIndex.remove(name) ?: namesMultiIndex.remove(name)) != null

    fun clearNames(): NameType {
        namesIndex = mutableMapOf()
        namesMultiIndex = mutableMapOf()
        return this
    }

    override fun toString(): String {
        return "NameType(name='$name', description='$description')"
    }


}
