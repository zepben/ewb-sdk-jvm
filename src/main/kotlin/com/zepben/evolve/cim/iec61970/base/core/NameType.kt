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
    private var namesMultiIndex: MutableMap<String, MutableMap<IdentifiedObject, Name>> = mutableMapOf()

    var description: String = ""

    /**
     * All names of this type.
     */
    val names: Sequence<Name> get() = namesMultiIndex.flatMap { it.value.values }.asSequence() + namesIndex.values.asSequence()

    /**
     * Check if the [NameType] contains a [Name].
     *
     * @param name the name of the required [Name] you are searching
     * @return A [Boolean] indicating if a matching [Name] can be found in the [NameType]
     */
    fun hasName(name: String): Boolean = namesIndex.containsKey(name) || namesMultiIndex.containsKey(name)

    /**
     * Get all the [Name] instances for the provided [IdentifiedObject].
     *
     * @param obj the [IdentifiedObject] you are finding the [Name] for
     * @return A list of [Name]
     */
    fun getNames(obj: IdentifiedObject): List<Name> =
        namesIndex.values
            .toList()
            .filter { name -> name.identifiedObject == obj } +
            namesMultiIndex
                .flatMap { it.value.values }
                .filter { name -> name.identifiedObject == obj }


    /**
     * Get all the [Name] instances for the provided [name].
     *
     * @return A Collection of [Name]
     */
    fun getNames(name: String): Collection<Name> =
        namesIndex[name]?.let { listOf(it) } ?: namesMultiIndex[name]?.values.asUnmodifiable()

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
                        namesMultiIndex[name] = mutableMapOf(existing.identifiedObject to existing, it.identifiedObject to it)
                        namesIndex.remove(name)
                        identifiedObject.addName(this, name)
                    }
                }
            }

            namesMultiIndex.containsKey(name) -> {
                val names = namesMultiIndex[name]!!
                var nameObj = names[identifiedObject]
                if (nameObj == null) {
                    nameObj = Name(name, this, identifiedObject)
                    names[identifiedObject] = nameObj
                    identifiedObject.addName(this, name)
                }
                nameObj
            }

            else -> {
                Name(name, this, identifiedObject).also {
                    namesIndex[name] = it
                    identifiedObject.addName(this, name)
                }
            }
        }
    }

    /**
     * Removes the [name] from this name type.
     *
     * @return true if the name instance was successfully removed
     */
    fun removeName(name: Name): Boolean {
        if (name.type !== this) {
            return false
        }

        return when (namesIndex.remove(name.name)) {
            null -> {
                val removed = namesMultiIndex[name.name]?.remove(name.identifiedObject) != null
                if (removed) {
                    // Remove name from associated identified object when it's removed from the nameType
                    name.identifiedObject.removeName(name)
                    if (namesMultiIndex[name.name]?.isEmpty() == true) {
                        namesMultiIndex.remove(name.name)
                    }
                }
                removed
            }

            else -> {
                // Remove name from associated identified object when it's removed from the nameType
                name.identifiedObject.removeName(name)
                true
            }
        }
    }

    /**
     * Removes all [Name] instances associated with name [name].
     *
     * @return true if a matching name was removed.
     */
    fun removeNames(name: String?): Boolean {
        // Calling removeName from identifiedObject will remove the name from both the identifiedObject and the nameType
        namesIndex[name]?.let { n ->
            return n.identifiedObject.removeName(n)
        }
        namesMultiIndex[name]?.values.let { names ->
            var removed = false
            names?.toList()?.forEach { name ->
                removed = name.identifiedObject.removeName(name) || removed
            }
            return removed
        }
    }

    /**
     * Removes all [Name] instances associated with this [NameType].
     *
     * @return this [NameType]
     */
    fun clearNames(): NameType {
        // Remove name from associated identified object when it's removed from the nameType
        namesIndex.toMap().forEach { (_, name) ->
            name.identifiedObject.removeName(name)
        }
        namesMultiIndex.flatMap { it.value.values }.forEach { name ->
            name.identifiedObject.removeName(name)
        }
        // Clean name from nameType indexes
        namesIndex = mutableMapOf()
        namesMultiIndex = mutableMapOf()
        return this
    }

    override fun toString(): String {
        return "NameType(name='$name', description='$description')"
    }


}
