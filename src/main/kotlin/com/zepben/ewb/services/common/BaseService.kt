/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common

import com.zepben.evolve.cim.iec61968.assets.Asset
import com.zepben.evolve.cim.iec61968.assets.AssetContainer
import com.zepben.evolve.cim.iec61968.assets.Structure
import com.zepben.evolve.cim.iec61968.metering.EndDevice
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.AuxiliaryEquipment
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.Sensor
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.wires.Conductor
import com.zepben.evolve.cim.iec61970.base.wires.EnergyConnection
import com.zepben.evolve.cim.iec61970.base.wires.RegulatingCondEq
import com.zepben.evolve.cim.iec61970.base.wires.RotatingMachine
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsUnit
import com.zepben.evolve.services.common.exceptions.UnsupportedIdentifiedObjectException
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.nameAndMRID
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.common.meta.MetadataCollection
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
 * @property name a short description of the service
 * @property metadata The [MetadataCollection] associated with this service.
 */
abstract class BaseService(
    val name: String,
    val metadata: MetadataCollection
) {

    /**
     * A collection of objects store by this service, indexed by its class type and mRID.
     */
    protected val objectsByType: MutableMap<KClass<*>, MutableMap<String, IdentifiedObject>> = mutableMapOf()

    /**
     * A map of references between mRID's that as yet have not been resolved - typically when transferring services between systems.
     * The key is the toMrid of the [UnresolvedReference]s, and the value is a list of [UnresolvedReference]s for that specific object.
     * For example, if an AcLineSegment with mRID 'acls1' is present in the service, but the service is missing its 'location' with mRID 'location-l1'
     * and 'perLengthSequenceImpedance' with mRID 'plsi-1', the following key value pairs would be present:
     * ```kotlin
     * {
     *   "plsi-1": [
     *     UnresolvedReference(from=AcLineSegment('acls1'), toMrid='plsi-1', resolver=ReferenceResolver(fromClass=AcLineSegment, toClass=PerLengthSequenceImpedance, resolve=...), ...)
     *   ],
     *   "location-l1": [
     *     UnresolvedReference(from=AcLineSegment('acls1'), toMrid='location-l1', resolver=ReferenceResolver(fromClass=AcLineSegment, toClass=Location, resolve=...), ...)
     *   ]
     * }
     * ```
     *
     * [resolve] in [ReferenceResolver] will be the function used to populate the relationship between the [IdentifiedObject]s either when
     * [resolveOrDeferReference] is called if the other side of the reference exists in the service, or otherwise when the second object is added to the service.
     */
    private val unresolvedReferencesTo = mutableMapOf<String, MutableSet<UnresolvedReference<IdentifiedObject, IdentifiedObject>>>()

    /**
     * An index of the unresolved references by their [UnresolvedReference.from] mRID. For the above example this will be a dictionary of the form:
     * ```kotlin
     * {
     *   "acls1": [
     *     UnresolvedReference(from=AcLineSegment('acls1'), toMrid='location-l1', resolver=ReferenceResolver(fromClass=AcLineSegment, toClass=Location, resolve=...), ...)
     *     UnresolvedReference(from=AcLineSegment('acls1'), toMrid='plsi-1', resolver=ReferenceResolver(fromClass=AcLineSegment, toClass=PerLengthSequenceImpedance, resolve=...), ...)
     *   ]
     * }
     * ```
     */
    private val unresolvedReferencesFrom = mutableMapOf<String, MutableSet<UnresolvedReference<IdentifiedObject, IdentifiedObject>>>()

    private val addFunctions: Map<KClass<out IdentifiedObject>, KFunction<*>> = findFunctionsForDispatch("add")
    private val removeFunctions: Map<KClass<out IdentifiedObject>, KFunction<*>> = findFunctionsForDispatch("remove")

    /**
     * A list of Java classes supported by this service.
     */
    val supportedClasses: Set<Class<out IdentifiedObject>> = Collections.unmodifiableSet(addFunctions.keys.map { it.java }.toSet())

    /**
     * A list of Kotlin classes supported by this service.
     */
    val supportedKClasses: Set<KClass<out IdentifiedObject>> get() = addFunctions.keys

    private var _nameTypes: MutableMap<String, NameType> = mutableMapOf()

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
    inline operator fun <reified T : IdentifiedObject> get(mRID: String?): T? = get(T::class, mRID)

    /**
     * A Java interop version of [get]. Get an object associated with this service.
     *
     * @param T The type of object to look for. If this is a base class it will search all subclasses.
     * @param clazz The class representing [T].
     * @param mRID The mRID of the object to find.
     *
     * @return The object identified by [mRID] as [T] if it was found, otherwise null.
     */
    fun <T : IdentifiedObject> get(clazz: Class<T>, mRID: String?): T? = get(clazz.kotlin, mRID)

    /**
     * The name types associated with this service. The returned collection is read only.
     */
    val nameTypes: Collection<NameType> get() = _nameTypes.values.asUnmodifiable()

    /**
     * Associates the provided [nameType] with this service.
     *
     * @param [nameType] the [NameType] to add to this service
     * @return true if the object is associated with this service, false if an object already exists in the service with
     * the same name.
     */
    fun addNameType(nameType: NameType): Boolean {
        if (_nameTypes.containsKey(nameType.name)) {
            return false
        }
        _nameTypes[nameType.name] = nameType

        return true
    }

    /**
     * Gets the [NameType] for the provided type name associated with this service.
     *
     * @param [type] the type name.
     * @return The [NameType] identified by [type] if it was found, otherwise null.
     */
    fun getNameType(type: String): NameType? = _nameTypes[type]

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
        mRID ?: return null

        if (clazz != IdentifiedObject::class)
            objectsByType[clazz]?.let { return it[mRID] as T? }

        return clazz.java.cast(
            objectsByType.values
                .asSequence()
                .mapNotNull { it[mRID] }
                .firstOrNull()
        )
    }

    /**
     * Check if [mRID] has any associated object.
     *
     * @param mRID The mRID to search for.
     *
     * @return true if there is an object associated with the specified [mRID].
     */
    fun contains(mRID: String): Boolean = objectsByType.values.any { it.containsKey(mRID) }

    /**
     * Get the number of objects associated with this service.
     *
     * @param T The type of object to count. If this is a base class it will search all subclasses.
     *
     * @return The number of objects of the specified type.
     */
    inline fun <reified T : IdentifiedObject> num(): Int = num(T::class)

    /**
     * A Java interop version of [num]. Get the number of objects associated with this service.
     *
     * @param T The type of object to count. If this is a base class it will search all subclasses.
     * @param clazz The class representing [T].
     *
     * @return The number of objects of the specified type.
     */
    fun <T : IdentifiedObject> num(clazz: Class<T>): Int = num(clazz.kotlin)

    /**
     * Get the number of objects associated with this service.
     *
     * @param T The type of object to count. If this is a base class it will search all subclasses.
     * @param clazz The class representing [T].
     *
     * @return The number of objects of the specified type.
     */
    fun <T : IdentifiedObject> num(clazz: KClass<T>): Int = sequenceOf(clazz).count()

    /**
     * Attempts to add the [identifiedObject] to the service, if this service instance supports the type of [IdentifiedObject]
     * that is provided.
     *
     * If the service does support the [identifiedObject], it will be added as if you are calling the add function
     * directly on the instance where the corresponding "add" function is defined for this type of identified object.
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
        } catch (ex: UnsupportedIdentifiedObjectException) {
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
        if (map.containsKey(identifiedObject.mRID)) return map[identifiedObject.mRID] === identifiedObject

        // Check all the other types to make sure this MRID is actually unique
        if (objectsByType.any { (_, v) -> v.containsKey(identifiedObject.mRID) })
            return false

        unresolvedReferencesTo.remove(identifiedObject.mRID)?.forEach {
            try {
                val castedIdentifiedObject = it.resolver.toClass.cast(identifiedObject)

                it.resolver.resolve(it.from, castedIdentifiedObject)
                it.reverseResolver?.resolve(castedIdentifiedObject, it.from)

                unresolvedReferencesFrom[it.from.mRID]?.let { urs ->
                    urs.remove(it)
                    if (urs.isEmpty())
                        unresolvedReferencesFrom.remove(it.from.mRID)
                }
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
        if (toMrid.isNullOrEmpty()) {
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
                    unresolvedReferencesTo[from.mRID]?.apply {
                        removeIf { it.toMrid == from.mRID && it.resolver == reverseResolver }
                        if (isEmpty())
                            unresolvedReferencesTo.remove(from.mRID)
                    }
                    unresolvedReferencesFrom[to.mRID]?.apply {
                        removeIf { it.toMrid == from.mRID && it.resolver == reverseResolver }
                        if (isEmpty())
                            unresolvedReferencesFrom.remove(to.mRID)
                    }
                }
                true
            } else {
                val ur = UnresolvedReference(from, toMrid, resolver, reverseResolver) as UnresolvedReference<IdentifiedObject, IdentifiedObject>
                unresolvedReferencesTo.getOrPut(toMrid) { mutableSetOf() }.add(ur)
                unresolvedReferencesFrom.getOrPut(from.mRID) { mutableSetOf() }.add(ur)
                false
            }
        } catch (ex: ClassCastException) {
            throw IllegalStateException(
                "$toMrid didn't match the expected class ${resolver.toClass.simpleName}. Did you re-use an mRID?: ${ex.localizedMessage}",
                ex
            )
        }
    }

    /**
     * Check if there are [UnresolvedReference]s in the service
     *
     * @param mRID The mRID to check for [UnresolvedReference]s. If null, will check if any unresolved references exist in the service.
     *
     * @return true if at least one reference exists.
     */
    fun hasUnresolvedReferences(mRID: String? = null): Boolean =
        if (mRID != null) unresolvedReferencesTo.containsKey(mRID) else unresolvedReferencesTo.isNotEmpty()

    /**
     * Get the number of [UnresolvedReference]s in this service.
     *
     * @param mRID The mRID to check the number of [UnresolvedReference]s for. If null, will default to number of all unresolved references in the service.
     *
     * @return The number of [UnresolvedReference]s
     */
    fun numUnresolvedReferences(mRID: String? = null): Int =
        mRID?.let { unresolvedReferencesTo[mRID]?.size ?: 0 } ?: unresolvedReferencesTo.values.sumOf { it.size }

    /**
     *
     * Gets a set of MRIDs that are unresolved references via the [referenceResolver].
     */
    fun <T : IdentifiedObject, R : IdentifiedObject> getUnresolvedReferenceMrids(referenceResolver: ReferenceResolver<T, R>): Set<String> =
        unresolvedReferencesTo.values.asSequence()
            .flatMap { it.asSequence() }
            .filter { it.resolver == referenceResolver }
            .map { it.toMrid }
            .toSet()

    /**
     * Gets a set of MRIDs that are referenced by the [T] held by [boundResolver] that are unresolved.
     */
    fun <T : IdentifiedObject, R : IdentifiedObject> getUnresolvedReferenceMrids(boundResolver: BoundReferenceResolver<T, R>): Set<String> =
        unresolvedReferencesTo.values.asSequence()
            .flatMap { it.asSequence() }
            .filter { it.from == boundResolver.from && it.resolver == boundResolver.resolver }
            .map { it.toMrid }
            .toSet()

    /**
     * Get the [UnresolvedReference]s that [mRID] has to other objects.
     * @param mRID The mRID to get unresolved references for.
     * @return a sequence of the [UnresolvedReference]s that need to be resolved for [mRID].
     */
    fun getUnresolvedReferencesFrom(mRID: String): Sequence<UnresolvedReference<*, *>> = unresolvedReferencesFrom[mRID]?.asSequence() ?: emptySequence()

    /**
     * Get the [UnresolvedReference]s that other objects have to [mRID].
     * @param mRID The mRID to get unresolved references for.
     * @return a sequence of the [UnresolvedReference]s that need to be resolved for [mRID].
     */
    fun getUnresolvedReferencesTo(mRID: String): Sequence<UnresolvedReference<*, *>> = unresolvedReferencesTo[mRID]?.asSequence() ?: emptySequence()

    /**
     * Returns a sequence of all unresolved references.
     */
    fun unresolvedReferences(): Sequence<UnresolvedReference<*, *>> =
        unresolvedReferencesTo.values.asSequence().flatMap { it.asSequence() }

    /**
     * Disassociate an object from this service.
     *
     * @param identifiedObject The object to disassociate from this service.
     *
     * @return true if the object is disassociated from this service.
     */
    protected fun remove(identifiedObject: IdentifiedObject): Boolean = objectsByType[identifiedObject::class]?.remove(identifiedObject.mRID) != null


    protected fun remove(powerSystemResource: PowerSystemResource): Boolean {
        powerSystemResource.assets.forEach { asset -> asset.removePowerSystemResource(powerSystemResource) }

        return remove(powerSystemResource as IdentifiedObject)
    }

    protected fun remove(equipment: Equipment): Boolean {
        equipment.containers.forEach { container -> container.removeEquipment(equipment) }
        equipment.currentContainers.forEach { container -> container.removeEquipment(equipment) }
        equipment.usagePoints.forEach { up -> up.removeEquipment(equipment) }

        return remove(equipment as PowerSystemResource)
    }

    protected fun remove(auxiliaryEquipment: AuxiliaryEquipment): Boolean {
        // Don't clean up terminal here because the terminal belongs to another conducting equipment.

        return remove(auxiliaryEquipment as Equipment)
    }

    protected fun remove(sensor: Sensor): Boolean {

        return remove(sensor as Equipment)
    }

    protected fun remove(conductingEquipment: ConductingEquipment, cascade: Boolean): Boolean {
        if (cascade) {
            conductingEquipment.terminals.forEach { t ->
                conductingEquipment.removeTerminal(t)
                remove(t)
            }
        }

        return remove(conductingEquipment as Equipment)
    }

    protected fun remove(conductor: Conductor, cascade: Boolean): Boolean {
        return remove(conductor as ConductingEquipment, cascade)
    }

    protected fun remove(powerElectronicsUnit: PowerElectronicsUnit): Boolean {
        powerElectronicsUnit.powerElectronicsConnection?.removeUnit(powerElectronicsUnit)

        return remove(powerElectronicsUnit as Equipment)
    }

    protected fun remove(asset: Asset): Boolean {
        asset.powerSystemResources.forEach { psr -> psr.removeAsset(asset) }

        return remove(asset as IdentifiedObject)
    }

    protected fun remove(assetContainer: AssetContainer): Boolean = remove(assetContainer as Asset)

    protected fun remove(structure: Structure): Boolean = remove(structure as AssetContainer)

    protected fun remove(endDevice: EndDevice): Boolean {
        endDevice.usagePoints.forEach { up ->
            up.removeEndDevice(endDevice)
        }

        return remove(endDevice as AssetContainer)
    }

    protected fun remove(energyConnection: EnergyConnection, cascade: Boolean): Boolean = remove(energyConnection as ConductingEquipment, cascade)

    protected fun remove(regulatingCondEq: RegulatingCondEq, cascade: Boolean): Boolean {
        regulatingCondEq.regulatingControl?.removeRegulatingCondEq(regulatingCondEq)

        return remove(regulatingCondEq as EnergyConnection, cascade)
    }

    protected fun remove(rotatingMachine: RotatingMachine, cascade: Boolean): Boolean {
        return remove(rotatingMachine as RegulatingCondEq, cascade)
    }


    /**
     * Create a sequence of all instances of the specified type.
     *
     * @param T The type of object to add to the sequence. If this is a base class it will collect all subclasses.
     *
     * @return a [Sequence] containing all instances of type [T].
     */
    inline fun <reified T : IdentifiedObject> sequenceOf(): Sequence<T> = sequenceOf(T::class)

    /**
     * Create a sequence of all instances of the specified type.
     *
     * @param T The type of object to add to the sequence. If this is a base class it will collect all subclasses.
     * @param clazz The class representing [T].
     *
     * @return a [Sequence] containing all instances of type [T].
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : IdentifiedObject> sequenceOf(clazz: KClass<T>): Sequence<T> =
        objectsByType[clazz]?.values?.asSequence()?.map { it as T }
            ?: objectsByType
                .asSequence()
                .filter { (c, _) -> clazz.isSuperclassOf(c) }
                .flatMap { (_, map) -> map.values.asSequence() }
                .map { it as T }

    /**
     * A Java interop version of [sequenceOf]. Create a sequence of all instances of the specified type.
     *
     * @param T The type of object to add to the sequence. If this is a base class it will collect all subclasses.
     * @param clazz The class representing [T].
     *
     * @return a [Stream] containing all instances of type [T].
     */
    fun <T : IdentifiedObject> streamOf(clazz: Class<T>): Stream<T> = sequenceOf(clazz.kotlin).asStream()

    /**
     * Collect all instances of the specified type that match a [filter] into a [List].
     *
     * @param T The type of object to collect. If this is a base class it will collect all subclasses.
     * @param filter The filter used to include items in the [List].
     *
     * @return a [List] containing all instances of type [T] that match [filter] stored in this service.
     */
    inline fun <reified T : IdentifiedObject> listOf(noinline filter: ((T) -> Boolean)? = null): List<T> = listOf(T::class, filter)

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
    fun <T : IdentifiedObject> listOf(clazz: Class<T>, filter: Predicate<T>? = null): List<T> = listOf(clazz.kotlin, filter?.let { it::test })

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
    inline fun <reified T : IdentifiedObject> setOf(noinline filter: ((T) -> Boolean)? = null): Set<T> = setOf(T::class, filter)

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
    fun <T : IdentifiedObject> setOf(clazz: Class<T>, filter: Predicate<T>? = null): Set<T> = setOf(clazz.kotlin, filter?.let { it::test })

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
    inline fun <reified T : IdentifiedObject> mapOf(noinline filter: ((T) -> Boolean)? = null): Map<String, T> = mapOf(T::class, filter)

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
    fun <T : IdentifiedObject> mapOf(clazz: Class<T>, filter: Predicate<T>? = null): Map<String, T> = mapOf(clazz.kotlin, filter?.let { it::test })

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
