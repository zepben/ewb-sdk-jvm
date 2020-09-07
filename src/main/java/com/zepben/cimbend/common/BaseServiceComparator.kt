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

import com.zepben.cimbend.cim.iec61968.common.Document
import com.zepben.cimbend.cim.iec61968.common.Organisation
import com.zepben.cimbend.cim.iec61968.common.OrganisationRole
import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
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

        return differences
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : IdentifiedObject> compare(source: T, target: T): ObjectDifference<T> {
        val sourceType = getComparableType(source::class)
        val targetType = getComparableType(target::class)

        require(sourceType == targetType) { "source and target must be of the same type"}
        return requireNotNull(compareByType[sourceType]) {
            "INTERNAL ERROR: Attempted to compare Zepben CIM class ${source::class} which is not registered with the comparator."
        }.call(this, source, target) as ObjectDifference<T>
    }

    protected fun ObjectDifference<out IdentifiedObject>.compareIdentifiedObject(): ObjectDifference<out IdentifiedObject> = apply {
        compareValues(IdentifiedObject::mRID, IdentifiedObject::name, IdentifiedObject::description, IdentifiedObject::numDiagramObjects)
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
        if (packageName?.startsWith("com.zepben.cimbend.cim.") == true)
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
