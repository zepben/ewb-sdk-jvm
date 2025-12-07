/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common

import com.zepben.ewb.cim.iec61968.common.Document
import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61968.common.OrganisationRole
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.Name
import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.DataSet
import java.security.AccessController
import java.security.PrivilegedActionException
import java.security.PrivilegedExceptionAction
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.isAccessible

/**
 * The base class for comparing services.
 */
abstract class BaseServiceComparator {

    @Suppress("UNCHECKED_CAST")
    private val compareByType: Map<KType, KFunction<ObjectDifference<*>>> = this::class.memberFunctions
        .asSequence()
        .filter { it.name.startsWith("compare") }
        .filter { it.parameters.size == 3 }
        .filter { it.parameters[1].type == it.parameters[2].type }
        .filter { it.parameters[1].name == "source" }
        .filter { it.parameters[2].name == "target" }
        .filter { it.returnType.classifier == ObjectDifference::class }
        .filter {
            try {
                AccessController.doPrivileged(PrivilegedExceptionAction {
                    it.isAccessible = true
                    true
                })
            } catch (_: PrivilegedActionException) {
                false
            }
        }
        .associateBy({ it.parameters[1].type }, { it as KFunction<ObjectDifference<*>> })

    /**
     * Run the compare with the specified optional checks
     *
     * @param source  The network service to use as the source
     * @param target  The network service to use as the target
     * @return The differences detected between the source and the target
     */
    fun compare(
        source: BaseService,
        target: BaseService
    ): ServiceDifferences {
        val differences = ServiceDifferences({ source[it] }, { target[it] }, { source.getNameType(it) }, { target.getNameType(it) })

        source.sequenceOf<IdentifiedObject>().forEach { s ->
            val difference = target.get<IdentifiedObject>(s.mRID)?.let { t ->
                val sourceType = getComparableType(s::class)
                if (sourceType != getComparableType(t::class)) {
                    differences.addToMissingFromSource(s.mRID)
                    null
                } else {
                    requireNotNull(compareByType[sourceType]) {
                        "INTERNAL ERROR: Attempted to compare Zepben CIM class ${s::class} which is not registered with the comparator."
                    }.call(this, s, t)
                }
            }

            if (difference == null)  // Wasn't present in target
                differences.addToMissingFromTarget(s.mRID)
            else if (difference.differences.isNotEmpty())  // present, but not the same
                differences.addModifications(s.mRID, difference)
        }

        target.sequenceOf<IdentifiedObject>()
            .filter { !source.contains(it.mRID) }
            .forEach { differences.addToMissingFromSource(it.mRID) }

        source.nameTypes.forEach { s ->
            val difference = target.getNameType(s.name)?.let { t -> compareNameType(s, t) }

            if (difference == null)  // Wasn't present in target
                differences.addToMissingFromTarget(s.name)
            else if (difference.differences.isNotEmpty())  // present, but not the same
                differences.addModifications(s.name, difference)
        }

        target.nameTypes
            .filter { source.getNameType(it.name) == null }
            .forEach { differences.addToMissingFromSource(it.name) }

        return differences
    }

    /**
     * Compare two objects, returning the difference.
     *
     * @param source The first object to compare.
     * @param target The second object to compare.
     * @return The [ObjectDifference] between the [source] and [target].
     * @throws IllegalArgumentException If the [source] and [target] objects aren't pf the same type, or
     * if their type hasn't been registered with the comparator
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> compare(source: T, target: T): ObjectDifference<T> {
        val sourceType = getComparableType(source::class)
        val targetType = getComparableType(target::class)

        require(sourceType == targetType) { "source and target must be of the same type" }
        return requireNotNull(compareByType[sourceType]) {
            "INTERNAL ERROR: Attempted to compare Zepben CIM class ${source::class} which is not registered with the comparator."
        }.call(this, source, target) as ObjectDifference<T>
    }

    /**
     * Compare the [IdentifiedObject] members.
     *
     * @receiver The [ObjectDifference] to populate with any differences.
     * @return The [ObjectDifference] being populated for fluent use.
     */
    protected fun ObjectDifference<out IdentifiedObject>.compareIdentifiedObject(): ObjectDifference<out IdentifiedObject> = apply {
        compareValues(IdentifiedObject::mRID, IdentifiedObject::name, IdentifiedObject::description, IdentifiedObject::numDiagramObjects)
        compareNames(IdentifiedObject::names)
    }

    private fun compareNameType(source: NameType, target: NameType): ObjectDifference<NameType> =
        ObjectDifference(source, target).apply {
            compareValues(NameType::description)

            fun Name.compareMatch(other: Name): Boolean =
                this.name == other.name &&
                    this.type.name == other.type.name &&
                    this.identifiedObject.mRID == other.identifiedObject.mRID

            val differences = ObjectCollectionDifference()

            source.names.forEach { sName ->
                if (!target.names.any { tName -> sName.compareMatch(tName) })
                    differences.missingFromTarget.add(sName)
            }

            target.names.forEach { tName ->
                if (!source.names.any { sName -> tName.compareMatch(sName) })
                    differences.missingFromSource.add(tName)
            }

            addIfDifferent(NameType::names.name, differences.nullIfEmpty())
        }

    /**
     * Compare the [Document] members.
     *
     * @receiver The [ObjectDifference] to populate with any differences.
     * @return The [ObjectDifference] being populated for fluent use.
     */
    protected fun ObjectDifference<out Document>.compareDocument(): ObjectDifference<out Document> =
        apply {
            compareIdentifiedObject()
            compareValues(Document::title, Document::createdDateTime, Document::authorName, Document::type, Document::status, Document::comment)
        }

    /**
     * Compare the [OrganisationRole] members.
     *
     * @receiver The [ObjectDifference] to populate with any differences.
     * @return The [ObjectDifference] being populated for fluent use.
     */
    protected fun ObjectDifference<out OrganisationRole>.compareOrganisationRole(): ObjectDifference<out OrganisationRole> =
        apply {
            compareIdentifiedObject()
            compareIdReferences(OrganisationRole::organisation)
        }

    /**
     * Compare the [Organisation] members.
     *
     * @receiver The [ObjectDifference] to populate with any differences.
     * @return The [ObjectDifference] being populated for fluent use.
     */
    // Used via reflection
    @Suppress("Unused")
    protected fun compareOrganisation(source: Organisation, target: Organisation): ObjectDifference<Organisation> =
        ObjectDifference(source, target).apply {
            compareIdentifiedObject()
        }

    private fun getComparableType(clazz: KClass<*>): KType? {
        val packageName = if (clazz.qualifiedName.isNullOrBlank()) null else clazz.java.packageName
        if (packageName?.startsWith("com.zepben.ewb.cim.") == true)
            return clazz.createType()

        return clazz.superclasses
            .asSequence()
            .map { getComparableType(it) }
            .filterNotNull()
            .firstOrNull()
    }

    /**
     * Compare the values of the given [properties].
     *
     * @param properties The member properties to compare.
     * @receiver The [ObjectDifference] to populate with any differences.
     * @return The [ObjectDifference] being populated for fluent use.
     */
    fun <T> ObjectDifference<T>.compareValues(
        vararg properties: KProperty1<in T, *>
    ): ObjectDifference<T> {
        properties.forEach { addIfDifferent(it.name, it.compareValues(source, target)) }
        return this
    }

    /**
     * Compare the given [properties] by checking they reference an object with the same mRID. Differences
     * in the referenced object won't be reported here, instead only being reported when the referenced
     * objects are compared.
     *
     * @param properties The member properties to compare.
     * @receiver The [ObjectDifference] to populate with any differences.
     * @return The [ObjectDifference] being populated for fluent use.
     */
    fun <T : IdentifiedObject, R : IdentifiedObject> ObjectDifference<T>.compareIdReferences(
        vararg properties: KProperty1<in T, R?>
    ): ObjectDifference<T> {
        properties.forEach { addIfDifferent(it.name, it.compareIdReference(source, target)) }
        return this
    }

    private fun <T : IdentifiedObject> ObjectDifference<T>.compareNames(
        vararg properties: KProperty1<IdentifiedObject, Collection<Name>>
    ): ObjectDifference<T> {
        properties.forEach { addIfDifferent(it.name, it.compareNames(source, target)) }
        return this
    }

    /**
     * Compare the given [properties] by checking they are a [Collection] of references to objects with the
     * same mRIDs, in any order. Differences in the referenced object won't be reported here, instead only
     * being reported when the referenced objects are compared.
     *
     * @param properties The member properties to compare.
     * @receiver The [ObjectDifference] to populate with any differences.
     * @return The [ObjectDifference] being populated for fluent use.
     */
    fun <T : IdentifiedObject, R : IdentifiedObject> ObjectDifference<T>.compareIdReferenceCollections(
        vararg properties: KProperty1<in T, Collection<R>>
    ): ObjectDifference<T> {
        properties.forEach { addIfDifferent(it.name, it.compareIdReferenceCollection(source, target)) }
        return this
    }

    /**
     * Compare the given [properties] by checking they are a [Collection] of references to objects with the
     * same mRIDs, in the same order. Differences in the referenced object won't be reported here, instead
     * only being reported when the referenced objects are compared.
     *
     * @param properties The member properties to compare.
     * @receiver The [ObjectDifference] to populate with any differences.
     * @return The [ObjectDifference] being populated for fluent use.
     */
    fun <T : IdentifiedObject, R : IdentifiedObject> ObjectDifference<T>.compareIndexedIdReferenceCollections(
        vararg properties: KProperty1<in T, List<R>>
    ): ObjectDifference<T> {
        properties.forEach { addIfDifferent(it.name, it.compareIndexedIdReferenceCollection(source, target)) }
        return this
    }

    /**
     * Compare the given [properties] by checking their values are the same, in the same order.
     *
     * @param properties The member properties to compare.
     * @receiver The [ObjectDifference] to populate with any differences.
     * @return The [ObjectDifference] being populated for fluent use.
     */
    fun <T> ObjectDifference<T>.compareIndexedValueCollections(
        vararg properties: KProperty1<in T, List<*>>
    ): ObjectDifference<T> {
        properties.forEach { addIfDifferent(it.name, it.compareIndexedValueCollection(source, target)) }
        return this
    }

    /**
     * Compare the given [property] by checking its values are the same, in any order.
     *
     * @param property The member property to compare.
     * @param keySelector A function to extract a comparable key from the objects in the collection, used to
     * determine which elements should be compared to each other.
     * @receiver The [ObjectDifference] to populate with any differences.
     * @return The [ObjectDifference] being populated for fluent use.
     */
    fun <T, R, K : Comparable<K>> ObjectDifference<T>.compareUnorderedValueCollection(
        property: KProperty1<in T, Collection<R>>,
        keySelector: (R) -> K
    ): ObjectDifference<T> {
        addIfDifferent(property.name, property.compareUnorderedValueCollection(source, target, keySelector))
        return this
    }

    /**
     * Adds a difference in a property, if there was one, to the overall collection of differences.
     *
     * @param name The name of the property being compared.
     * @param difference The difference in the property if there was one, otherwise `null`
     */
    fun <T> ObjectDifference<T>.addIfDifferent(name: String, difference: Difference?) {
        if (difference != null) {
            differences[name] = difference
        }
    }

}
