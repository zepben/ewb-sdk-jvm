/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.common

import com.zepben.evolve.cim.iec61968.common.Document
import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.common.OrganisationRole
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.core.Name
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

abstract class BaseServiceComparator {

    @Suppress("UNCHECKED_CAST")
    val compareByType: Map<KType, KFunction<ObjectDifference<*>>> = this::class.memberFunctions
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
                } as PrivilegedExceptionAction<Boolean>)
            } catch (e: PrivilegedActionException) {
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
        val differences = ServiceDifferences({ source[it] }, { target[it] })

        source.sequenceOf<IdentifiedObject>().forEach { s ->
            val difference = target.get<IdentifiedObject>(s.mRID)?.let { t ->
                val sourceType = getComparableType(s::class)
                val targetType = getComparableType(t::class)
                if (sourceType != targetType) {
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

        target.sequenceOf<IdentifiedObject>().forEach { t ->
            if (!source.contains(t.mRID))
                differences.addToMissingFromSource(t.mRID)
        }

        fun Name.compareMatch(other: Name): Boolean =
            this.name == other.name &&
                this.type.name == other.type.name &&
                this.identifiedObject.mRID == other.identifiedObject.mRID

        source.nameTypes.forEach { s ->
            val t = target.getNameType(s.name)

            if (t == null) {
                differences.addMissingNameTypeFromTarget(s.name)
            } else {
                val descDiff = if (s.description != t.description) ValueDifference(s.description, t.description) else null

                val namesMissingFromTarget = mutableListOf<Name>()
                val namesMissingFromSource = mutableListOf<Name>()
                s.names.forEach { sName ->
                    if (!t.names.any { tName -> sName.compareMatch(tName) })
                        namesMissingFromTarget.add(sName)
                }

                t.names.forEach { tName ->
                    if (!s.names.any { sName -> tName.compareMatch(sName) })
                        namesMissingFromSource.add(tName)
                }

                if (descDiff != null || namesMissingFromTarget.isNotEmpty() || namesMissingFromSource.isNotEmpty()) {
                    differences.addNameTypeDifference(NameTypeDifference(descDiff, namesMissingFromTarget, namesMissingFromSource))
                }
            }
        }

        target.nameTypes.forEach { t ->
            if (source.getNameType(t.name) == null)
                differences.addMissingNameTypeFromSource(t.name)
        }

        return differences
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : IdentifiedObject> compare(source: T, target: T): ObjectDifference<T> {
        val sourceType = getComparableType(source::class)
        val targetType = getComparableType(target::class)

        require(sourceType == targetType) { "source and target must be of the same type" }
        return requireNotNull(compareByType[sourceType]) {
            "INTERNAL ERROR: Attempted to compare Zepben CIM class ${source::class} which is not registered with the comparator."
        }.call(this, source, target) as ObjectDifference<T>
    }

    protected fun ObjectDifference<out IdentifiedObject>.compareIdentifiedObject(): ObjectDifference<out IdentifiedObject> = apply {
        compareValues(IdentifiedObject::mRID, IdentifiedObject::name, IdentifiedObject::description, IdentifiedObject::numDiagramObjects)
        compareNames(IdentifiedObject::names)
    }

    protected fun ObjectDifference<out Document>.compareDocument(): ObjectDifference<out Document> =
        apply {
            compareIdentifiedObject()
            compareValues(Document::title, Document::createdDateTime, Document::authorName, Document::type, Document::status, Document::comment)
        }

    protected fun ObjectDifference<out OrganisationRole>.compareOrganisationRole(): ObjectDifference<out OrganisationRole> =
        apply {
            compareIdentifiedObject()
            compareIdReferences(OrganisationRole::organisation)
        }

    protected fun compareOrganisation(source: Organisation, target: Organisation): ObjectDifference<Organisation> =
        ObjectDifference(source, target).apply {
            compareIdentifiedObject()
        }

    private fun getComparableType(clazz: KClass<*>): KType? {
        val packageName = if (clazz.qualifiedName.isNullOrBlank()) null else clazz.java.packageName
        if (packageName?.startsWith("com.zepben.evolve.cim.") == true)
            return clazz.createType()

        return clazz.superclasses
            .asSequence()
            .map { getComparableType(it) }
            .filterNotNull()
            .firstOrNull()
    }

    protected fun <T : IdentifiedObject> ObjectDifference<T>.compareValues(
        vararg properties: KProperty1<in T, *>
    ): ObjectDifference<T> {
        properties.forEach { addIfDifferent(it.name, it.compareValues(source, target)) }
        return this
    }

    protected fun <T : IdentifiedObject> ObjectDifference<T>.compareDoubles(
        vararg properties: KProperty1<in T, Double>
    ): ObjectDifference<T> {
        properties.forEach { addIfDifferent(it.name, it.compareDoubles(source, target)) }
        return this
    }

    protected fun <T : IdentifiedObject, R : IdentifiedObject> ObjectDifference<T>.compareIdReferences(
        vararg properties: KProperty1<in T, R?>
    ): ObjectDifference<T> {
        properties.forEach { addIfDifferent(it.name, it.compareIdReference(source, target)) }
        return this
    }

    protected fun <T : IdentifiedObject> ObjectDifference<T>.compareNames(
        vararg properties: KProperty1<IdentifiedObject, Collection<Name>>
    ): ObjectDifference<T> {
        properties.forEach { addIfDifferent(it.name, it.compareNames(source, target)) }
        return this
    }

    protected fun <T : IdentifiedObject, R : IdentifiedObject> ObjectDifference<T>.compareIdReferenceCollections(
        vararg properties: KProperty1<in T, Collection<R>>
    ): ObjectDifference<T> {
        properties.forEach { addIfDifferent(it.name, it.compareIdReferenceCollection(source, target)) }
        return this
    }

    protected fun <T : IdentifiedObject, R : IdentifiedObject> ObjectDifference<T>.compareIndexedIdReferenceCollections(
        vararg properties: KProperty1<in T, List<R>>
    ): ObjectDifference<T> {
        properties.forEach { addIfDifferent(it.name, it.compareIndexedIdReferenceCollection(source, target)) }
        return this
    }

    protected fun <T : IdentifiedObject> ObjectDifference<T>.compareIndexedValueCollections(
        vararg properties: KProperty1<in T, List<*>>
    ): ObjectDifference<T> {
        properties.forEach { addIfDifferent(it.name, it.compareIndexedValueCollection(source, target)) }
        return this
    }

    protected fun <T : IdentifiedObject> ObjectDifference<T>.addIfDifferent(name: String, difference: Difference?) {
        if (difference != null) {
            differences[name] = difference
        }
    }
}
