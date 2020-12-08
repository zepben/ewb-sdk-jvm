/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.common

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.services.common.exceptions.UnsupportedIdentifiedObjectException
import com.zepben.evolve.services.common.extensions.nameAndMRID
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import java.util.*
import java.util.function.Predicate
import java.util.stream.Stream
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSuperclassOf
import kotlin.streams.asStream

/**
 * Base class for services that work with [IdentifiedObject]s. This allows multiple services to be implemented that work
 * with different subsets of the CIM to allow separation of concerns.
 *
 * This class provides a common set of functionality that all services will need.
 *
 * @property [name] a short description of the service
 */
abstract class BaseService(
    val name: String
) {
    protected val objectsByType: MutableMap<KClass<*>, MutableMap<String, IdentifiedObject>> = mutableMapOf()

    private val unresolvedReferences = mutableMapOf<String, MutableList<UnresolvedReference<IdentifiedObject, IdentifiedObject>>>()

    private val addFunctions: Map<KClass<out IdentifiedObject>, KFunction<*>> = findFunctionsForDispatch("add")
    private val removeFunctions: Map<KClass<out IdentifiedObject>, KFunction<*>> = findFunctionsForDispatch("remove")

    val supportedClasses: Set<Class<out IdentifiedObject>> = Collections.unmodifiableSet(addFunctions.keys.map { it.java }.toSet())
    val supportedKClasses: Set<KClass<out IdentifiedObject>> get() = addFunctions.keys

    init {
        check(addFunctions.keys == removeFunctions.keys) {
            "Add and remove functions should be defined in matching pairs. They don't seem to match...\n" +
                "add   : ${addFunctions.keys.sortedBy { it.simpleName }}\n" +
                "remove: ${removeFunctions.keys.sortedBy { it.simpleName }}"
        }
    }

    /**
     * Get an object associated with this service.
     *
     * @param T The type of object to look for. If this is a base class it will search all subclasses.
     * @param mRID The mRID of the object to find.
     *
     * @return The object identified by [mRID] as [T] if it was found, otherwise null.
     */
    inline operator fun <reified T : IdentifiedObject> get(mRID: String?): T? {
        return get(T::class, mRID)
    }

    /**
     * A Java interop version of [get]. Get an object associated with this service.
     *
     * @param T The type of object to look for. If this is a base class it will search all subclasses.
     * @param clazz The class representing [T].
     * @param mRID The mRID of the object to find.
     *
     * @return The object identified by [mRID] as [T] if it was found, otherwise null.
     */
    fun <T : IdentifiedObject> get(clazz: Class<T>, mRID: String?): T? {
        return get(clazz.kotlin, mRID)
    }

    /**
     * Get an object associated with this service. If the object exists with this service but is not an instance of [T]
     * a [ClassCastException] is thrown.
     *
     * @param T The type of object to look for. If this is a base class it will search all subclasses.
     * @param clazz The class representing [T].
     * @param mRID The mRID of the object to find.
     * @return The object identified by [mRID] as [T] if it was found, otherwise null.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : IdentifiedObject> get(clazz: KClass<T>, mRID: String?): T? {
        if (mRID == null)
            return null

        val exactTypeMap = objectsByType[clazz]
        if (exactTypeMap != null)
            return exactTypeMap[mRID] as T?
        else {
            for ((c, map) in objectsByType) {
                if (clazz.isSuperclassOf(c)) {
                    val io = map[mRID]
                    if (io != null)
                        return io as T
                }
            }
        }

        // If clazz is IdentifiedObject::class, then we know we have searched every object and it does not exist on the service.
        // However if clazz is a subclass of Identified object, it's possible that the object exists on the service as a different
        // type. So we look up by IdentifiedObject::class and cast to clazz to force a ClassCastException if that object exists
        // on the service as a type different than requested.
        return if (clazz == IdentifiedObject::class) {
            null
        } else {
            // Using java cast as null is valid and it also produces a more useful exception message.
            clazz.java.cast(get(IdentifiedObject::class, mRID))
        }
    }

    /**
     * Check if [mRID] has any associated object.
     *
     * @param mRID The mRID to search for.
     *
     * @return true if there is an object associated with the specified [mRID].
     */
    fun contains(mRID: String) = objectsByType.values.any { it.containsKey(mRID) }

    /**
     * Get the number of objects associated with this service.
     *
     * @param T The type of object to count. If this is a base class it will search all subclasses.
     *
     * @return The number of objects of the specified type.
     */
    inline fun <reified T : IdentifiedObject> num(): Int {
        return num(T::class)
    }

    /**
     * A Java interop version of [num]. Get the number of objects associated with this service.
     *
     * @param T The type of object to count. If this is a base class it will search all subclasses.
     * @param clazz The class representing [T].
     *
     * @return The number of objects of the specified type.
     */
    fun <T : IdentifiedObject> num(clazz: Class<T>): Int {
        return num(clazz.kotlin)
    }

    /**
     * Get the number of objects associated with this service.
     *
     * @param T The type of object to count. If this is a base class it will search all subclasses.
     * @param clazz The class representing [T].
     *
     * @return The number of objects of the specified type.
     */
    fun <T : IdentifiedObject> num(clazz: KClass<T>): Int {
        return sequenceOf(clazz).count()
    }

    /**
     * Attempts to add the [identifiedObject] to the service, if this service instance supports the type of [IdentifiedObject]
     * that is provided.
     *
     * If the service does support the [identifiedObject], it will be added as if you are calling the add function
     * directly on the instance where the corresponding add function is defined for this type of identified object.
     *
     * @throws [UnsupportedIdentifiedObjectException] if the service does not support the [identifiedObject].
     * @return the return value of the underlying add function.
     */
    fun tryAdd(identifiedObject: IdentifiedObject): Boolean {
        val func = addFunctions[identifiedObject::class]
            ?: throw UnsupportedIdentifiedObjectException("$name service does not support adding ${identifiedObject::class}")

        return func.call(this, identifiedObject) as Boolean
    }

    /**
     * Add to the service and return [cim] if successful or null if the add failed (typically due to mRID already existing)
     * @param cim The [IdentifiedObject] to add.
     *
     * @return [cim] if successfully added else null.
     */
    fun <T : IdentifiedObject> tryAddOrNull(cim: T): T? =
        try {
            if (tryAdd(cim))
                cim
            else
                null
        } catch (ex: UnsupportedIdentifiedObjectException){
            null
        }


    /**
     * Attempts to remove the [identifiedObject] to the service, if this service instance supports the type of [IdentifiedObject]
     * that is provided.
     *
     * If the service does support the [identifiedObject], it will be removed as if you are calling the remove function
     * directly on the instance where the corresponding remove function is defined for this type of identified object.
     *
     * @throws [UnsupportedIdentifiedObjectException] if the service does not support the [identifiedObject].
     * @return the return value of the underlying remove function.
     */
    fun tryRemove(identifiedObject: IdentifiedObject): Boolean {
        val func = removeFunctions[identifiedObject::class]
            ?: throw UnsupportedIdentifiedObjectException("$name service does not support removing ${identifiedObject::class}")

        return func.call(this, identifiedObject) as Boolean
    }

    /**
     * Associates the provided [identifiedObject] with this service. This should be called by derived classes within their
     * add functions for specific supported identified object types.
     *
     * The [identifiedObject] must have a unique MRID, otherwise false will be returned and the object will not be added.
     *
     * If there are any unresolved references to the [identifiedObject] at this point they will be resolved
     * as part of the addition. If the [identifiedObject] class type does not match the [ReferenceResolver.toClass] of
     * any unresolved references an [IllegalStateException] will be thrown.
     *
     * @param [identifiedObject] the object to add to this service
     * @throws [UnsupportedIdentifiedObjectException] if the [IdentifiedObject] is not supported by this service.
     * @throws [IllegalStateException] if any unresolved references have an incorrect class type.
     * @throws [IllegalArgumentException] if [identifiedObject] did not have a valid mRID.
     * @return true if the object is associated with this service, false if an object already exists in the service with
     * the same mRID.
     */
    protected fun add(identifiedObject: IdentifiedObject): Boolean {
        if (identifiedObject.mRID.isEmpty())
            throw IllegalArgumentException("Object [${identifiedObject.typeNameAndMRID()}] must have an mRID set to be added to the service.")

        if (!supportedKClasses.contains(identifiedObject::class)) {
            throw UnsupportedIdentifiedObjectException("Unsupported IdentifiedObject type: ${identifiedObject::class}")
        }

        val map = objectsByType.computeIfAbsent(identifiedObject::class) { mutableMapOf() }
        if (map.containsKey(identifiedObject.mRID)) return false

        // Check all the other types to make sure this MRID is actually unique
        if (objectsByType.any { (_, v) -> v.containsKey(identifiedObject.mRID) })
            return false

        unresolvedReferences.remove(identifiedObject.mRID)?.forEach {
            try {
                val castedIdentifiedObject = it.resolver.toClass.cast(identifiedObject)
                it.resolver.resolve(it.from, castedIdentifiedObject)
            } catch (ex: ClassCastException) {
                throw IllegalStateException(
                    "Expected a ${it.resolver.toClass.simpleName} when resolving ${identifiedObject.nameAndMRID()} references but got a ${identifiedObject::class.simpleName}. Make sure you sent the correct types in every reference.",
                    ex
                )
            }
        }

        map[identifiedObject.mRID] = identifiedObject
        return true
    }

    /**
     * Resolves a property reference between a [T] and a referenced [R] by looking up the [toMrid] in the service and
     * using the provided [boundResolver] to resolve the reference relationship for the [T] object within the [BoundReferenceResolver].
     *
     * If the [toMrid] object has not yet been added to the service, the reference resolution will be deferred until the
     * object with [toMrid] is added to the service, which will then use the resolver from the [boundResolver] at that
     * time to resolve the reference relationship.
     *
     * The [toMrid] should be the MRID of an object that is a subclass of type [R]. If it is not, an [IllegalStateException]
     * is thrown either immediately if the reference can be resolved now, or it will be thrown when the deferred resolution
     * is applied when the object is added to the service.
     *
     * @return true if the reference was resolved, otherwise false if it has been deferred.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : IdentifiedObject, R : IdentifiedObject> resolveOrDeferReference(
        boundResolver: BoundReferenceResolver<T, R>,
        toMrid: String?
    ): Boolean {
        if (toMrid == null || toMrid.isEmpty()) {
            return true
        }

        val (from, resolver, reverseResolver) = boundResolver
        try {
            val to = get(resolver.toClass, toMrid)
            return if (to != null) {
                resolver.resolve(from, to)
                if (reverseResolver != null) {
                    reverseResolver.resolve(to, from)

                    // Clean up any reverse unresolved references now that the reference has been resolved
                    unresolvedReferences[from.mRID]?.apply {
                        removeIf { it.toMrid == from.mRID && it.resolver == reverseResolver }
                        if (isEmpty())
                            unresolvedReferences.remove(from.mRID)
                    }
                }
                true
            } else {
                val unresolvedReference = UnresolvedReference(from, toMrid, resolver) as UnresolvedReference<IdentifiedObject, IdentifiedObject>
                unresolvedReferences.getOrPut(toMrid) { mutableListOf() }.add(unresolvedReference)
                false
            }
        } catch (ex: ClassCastException) {
            throw IllegalStateException("$toMrid didn't match the expected class ${resolver.toClass.simpleName}. Did you re-use an mRID?: ${ex.localizedMessage}", ex)
        }
    }

    /**
     * Gets a set of MRIDs that are unresolved references via the [referenceResolver].
     */
    fun <T : IdentifiedObject, R : IdentifiedObject> getUnresolvedReferenceMrids(referenceResolver: ReferenceResolver<T, R>): Set<String> =
        unresolvedReferences.values.asSequence()
            .flatMap { it.asSequence() }
            .filter { it.resolver == referenceResolver }
            .map { it.toMrid }
            .toSet()

    /**
     * Gets a set of MRIDs that are referenced by the [T] held by [boundResolver] that are unresolved.
     */
    fun <T : IdentifiedObject, R : IdentifiedObject> getUnresolvedReferenceMrids(boundResolver: BoundReferenceResolver<T, R>): Set<String> =
        unresolvedReferences.values.asSequence()
            .flatMap { it.asSequence() }
            .filter { it.from == boundResolver.from && it.resolver == boundResolver.resolver }
            .map { it.toMrid }
            .toSet()

    /**
     * Returns a sequence of all unresolved references.
     */
    fun unresolvedReferences(): Sequence<UnresolvedReference<*, *>> =
        unresolvedReferences.values.asSequence().flatMap { it.asSequence() }

    /**
     * Disassociate an object from this service.
     *
     * @param identifiedObject The object to disassociate from this service.
     *
     * @return true if the object is disassociated from this service.
     */
    protected fun remove(identifiedObject: IdentifiedObject) = objectsByType[identifiedObject::class]?.remove(identifiedObject.mRID) != null

    /**
     * Create a sequence of all instances of the specified type.
     *
     * @param T The type of object to add to the sequence. If this is a base class it will collect all subclasses.
     *
     * @return a [Sequence] containing all instances of type [T].
     */
    inline fun <reified T : IdentifiedObject> sequenceOf(): Sequence<T> {
        return sequenceOf(T::class)
    }

    /**
     * Create a sequence of all instances of the specified type.
     *
     * @param T The type of object to add to the sequence. If this is a base class it will collect all subclasses.
     * @param clazz The class representing [T].
     *
     * @return a [Sequence] containing all instances of type [T].
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : IdentifiedObject> sequenceOf(clazz: KClass<T>): Sequence<T> {
        return objectsByType[clazz]?.values?.asSequence()?.map { it as T }
            ?: objectsByType
                .asSequence()
                .filter { (c, _) -> clazz.isSuperclassOf(c) }
                .flatMap { (_, map) -> map.values.asSequence() }
                .map { it as T }
    }

    /**
     * A Java interop version of [sequenceOf]. Create a sequence of all instances of the specified type.
     *
     * @param T The type of object to add to the sequence. If this is a base class it will collect all subclasses.
     * @param clazz The class representing [T].
     *
     * @return a [Stream] containing all instances of type [T].
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : IdentifiedObject> streamOf(clazz: Class<T>): Stream<T> {
        return sequenceOf(clazz.kotlin).asStream()
    }

    /**
     * Collect all instances of the specified type that match a [filter] into a [List].
     *
     * @param T The type of object to collect. If this is a base class it will collect all subclasses.
     * @param filter The filter used to include items in the [List].
     *
     * @return a [List] containing all instances of type [T] that match [filter] stored in this service.
     */
    inline fun <reified T : IdentifiedObject> listOf(noinline filter: ((T) -> Boolean)? = null): List<T> {
        return listOf(T::class, filter)
    }

    /**
     * A Java interop version of [listOf]. Collect all instances of the specified type that match a [filter] into a [List].
     *
     * @param T The type of object to collect. If this is a base class it will collect all subclasses.
     * @param clazz The class representing [T].
     * @param filter The filter used to include items in the [List].
     *
     * @return a [List] containing all instances of type [T] that match [filter] stored in this service.
     */
    @JvmOverloads
    fun <T : IdentifiedObject> listOf(clazz: Class<T>, filter: Predicate<T>? = null): List<T> {
        return listOf(clazz.kotlin, filter?.let { it::test })
    }

    /**
     * Collect all instances of the specified type that match a [filter] into a [List].
     *
     * @param T The type of object to collect. If this is a base class it will collect all subclasses.
     * @param clazz The class representing [T].
     * @param filter The filter used to include items in the [List].
     *
     * @return a [List] containing all instances of type [T] that match [filter] stored in this service.
     */
    fun <T : IdentifiedObject> listOf(clazz: KClass<T>, filter: ((T) -> Boolean)? = null): List<T> {
        val sequence = sequenceOf(clazz)
        return if (filter != null)
            sequence.filter(filter).toList()
        else
            sequence.toList()
    }

    /**
     * Collect all instances of the specified type that match a [filter] into a [Set].
     *
     * @param T The type of object to collect. If this is a base class it will collect all subclasses.
     * @param filter The filter used to include items in the [Set].
     *
     * @return a [Set] containing all instances of type [T] that match [filter] stored in this service.
     */
    inline fun <reified T : IdentifiedObject> setOf(noinline filter: ((T) -> Boolean)? = null): Set<T> {
        return setOf(T::class, filter)
    }

    /**
     * A Java interop version of [setOf]. Collect all instances of the specified type that match a [filter] into a [Set].
     *
     * @param T The type of object to collect. If this is a base class it will collect all subclasses.
     * @param clazz The class representing [T].
     * @param filter The filter used to include items in the [Set].
     *
     * @return a [Set] containing all instances of type [T] that match [filter] stored in this service.
     */
    @JvmOverloads
    fun <T : IdentifiedObject> setOf(clazz: Class<T>, filter: Predicate<T>? = null): Set<T> {
        return setOf(clazz.kotlin, filter?.let { it::test })
    }

    /**
     * Collect all instances of the specified type that match a [filter] into a [Set].
     *
     * @param T The type of object to collect. If this is a base class it will collect all subclasses.
     * @param clazz The class representing [T].
     * @param filter The filter used to include items in the [Set].
     *
     * @return a [Set] containing all instances of type [T] that match [filter] stored in this service.
     */
    fun <T : IdentifiedObject> setOf(clazz: KClass<T>, filter: ((T) -> Boolean)? = null): Set<T> {
        val sequence = sequenceOf(clazz)
        return if (filter != null)
            sequence.filter(filter).toSet()
        else
            sequence.toSet()
    }

    /**
     * Collect all instances of the specified type that match a [filter] into a [Map], indexed by [IdentifiedObject.mRID]..
     *
     * @param T The type of object to collect. If this is a base class it will collect all subclasses.
     * @param filter The filter used to include items in the [Map].
     *
     * @return a [Map] containing all instances of type [T] that match [filter] stored in this service.
     */
    inline fun <reified T : IdentifiedObject> mapOf(noinline filter: ((T) -> Boolean)? = null): Map<String, T> {
        return mapOf(T::class, filter)
    }

    /**
     * A Java interop version of [mapOf]. Collect all instances of the specified type that match a [filter] into a [Map], indexed by [IdentifiedObject.mRID]..
     *
     * @param T The type of object to collect. If this is a base class it will collect all subclasses.
     * @param clazz The class representing [T].
     * @param filter The filter used to include items in the [Map].
     *
     * @return a [Map] containing all instances of type [T] that match [filter] stored in this service.
     */
    @JvmOverloads
    fun <T : IdentifiedObject> mapOf(clazz: Class<T>, filter: Predicate<T>? = null): Map<String, T> {
        return mapOf(clazz.kotlin, filter?.let { it::test })
    }

    /**
     * Collect all instances of the specified type that match a [filter] into a [Map], indexed by [IdentifiedObject.mRID]..
     *
     * @param T The type of object to collect. If this is a base class it will collect all subclasses.
     * @param clazz The class representing [T].
     * @param filter The filter used to include items in the [Map].
     *
     * @return a [Map] containing all instances of type [T] that match [filter] stored in this service.
     */
    fun <T : IdentifiedObject> mapOf(clazz: KClass<T>, filter: ((T) -> Boolean)? = null): Map<String, T> {
        val sequence = sequenceOf(clazz)
        return if (filter != null)
            sequence.filter(filter).associateBy { it.mRID }
        else
            sequence.associateBy { it.mRID }
    }

    @Suppress("UNCHECKED_CAST")
    private fun findFunctionsForDispatch(name: String): Map<KClass<out IdentifiedObject>, KFunction<*>> {
        val idObjType = IdentifiedObject::class.createType()
        return this::class.declaredMemberFunctions.asSequence()
            .filter { it.name == name }
            .filter { it.parameters.size == 2 }
            .filter { it.visibility == KVisibility.PUBLIC }
            .filter { it.parameters[1].type.isSubtypeOf(idObjType) }
            .map { (it.parameters[1].type.classifier as KClass<out IdentifiedObject>) to it }
            .onEach {
                require(it.second.returnType.classifier == Boolean::class) {
                    "return type for '${it.second}' needs to be Boolean"
                }

                require((it.second.parameters[0].type.classifier as KClass<*>).isFinal) {
                    "${it.second} does not accept a leaf class. " +
                        "Only leafs should be used to reduce chances of edge case issues and potential undefined behaviour"
                }
            }
            .toMap()
    }
}
