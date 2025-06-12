/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.common

import com.zepben.evolve.cim.iec61968.assets.Asset
import com.zepben.evolve.cim.iec61968.assets.AssetContainer
import com.zepben.evolve.cim.iec61968.assets.Structure
import com.zepben.evolve.cim.iec61968.metering.EndDevice
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.*
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentEquipment
import com.zepben.evolve.cim.iec61970.base.meas.*
import com.zepben.evolve.cim.iec61970.base.protection.*
import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.evolve.cim.iec61970.base.scada.RemotePoint
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.BatteryUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PhotoVoltaicUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsWindUnit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.cim.iec61970.infiec61970.wires.generation.production.EvChargingUnit
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
     * IEC61970 base Auxiliary equipment
     * */

    protected fun removeInternal(auxiliaryEquipment: AuxiliaryEquipment, cascade: Boolean = false): Boolean {
        // Don't clean up terminal here because the terminal belongs to another conducting equipment.

        return removeInternal(auxiliaryEquipment as Equipment)
    }

    protected fun removeInternal(sensor: Sensor, cascade: Boolean = false): Boolean {

        return removeInternal(sensor as AuxiliaryEquipment)
    }

    protected fun removeInternal(currentTransformer: CurrentTransformer, cascade: Boolean = false): Boolean {

        return removeInternal(currentTransformer as Sensor)
    }

    protected fun removeInternal(potentialTransformer: PotentialTransformer, cascade: Boolean = false): Boolean {

        return removeInternal(potentialTransformer as Sensor)
    }

    protected fun removeInternal(faultIndicator: FaultIndicator, cascade: Boolean = false): Boolean {

        return removeInternal(faultIndicator as AuxiliaryEquipment)
    }

    /**
     * IEC61970 base core
     * */

    protected fun removeInternal(acDcTerminal: AcDcTerminal, cascade: Boolean = false): Boolean = removeInternal(acDcTerminal as IdentifiedObject)

    protected fun removeInternal(baseVoltage: BaseVoltage, cascade: Boolean = false): Boolean = removeInternal(baseVoltage as IdentifiedObject)

    protected fun removeInternal(conductingEquipment: ConductingEquipment, cascade: Boolean): Boolean {
        if (cascade) {
            conductingEquipment.terminals.forEach { t ->
                conductingEquipment.removeTerminal(t)
                removeInternal(t)
            }
        }

        return removeInternal(conductingEquipment as Equipment)
    }

    protected fun removeInternal(connectivityNode: ConnectivityNode, cascade: Boolean = false): Boolean {
        connectivityNode.terminals.forEach { t -> t.connectivityNode = null }

        return removeInternal(connectivityNode as IdentifiedObject)
    }

    protected fun removeInternal(connectivityNodeContainer: ConnectivityNodeContainer, cascade: Boolean = false): Boolean =
        removeInternal(connectivityNodeContainer as PowerSystemResource)

    protected fun removeInternal(curve: Curve, cascade: Boolean = false): Boolean = removeInternal(curve as IdentifiedObject)

    protected fun removeInternal(equipment: Equipment, cascade: Boolean = false): Boolean {
        equipment.containers.forEach { container -> container.removeEquipment(equipment) }
        equipment.currentContainers.forEach { container -> container.removeEquipment(equipment) }
        equipment.usagePoints.forEach { up -> up.removeEquipment(equipment) }

        return removeInternal(equipment as PowerSystemResource)
    }

    protected fun removeInternal(equipmentContainer: EquipmentContainer, cascade: Boolean = false): Boolean {
        equipmentContainer.equipment.forEach { eq ->
            eq.removeContainer(equipmentContainer)
        }

        return removeInternal(equipmentContainer as ConnectivityNodeContainer)
    }

    protected fun removeInternal(feeder: Feeder, cascade: Boolean, removeAssociatedFeeder: Boolean): Boolean {
        if (cascade) {
            val substation = feeder.normalEnergizingSubstation
            feeder.equipment
                .filter {
                    if (removeAssociatedFeeder)
                        it.containers.filterIsInstance<Feeder>().all { feeder -> feeder.normalEnergizingSubstation == substation }
                    else
                        it.containers.filterIsInstance<Feeder>().size == 1
                }
                .filter { it.containers.filterIsInstance<LvFeeder>().isEmpty() }
                .forEach { eq ->
                    removeEquipmentFromEquipmentContainerCascade(eq)
                }
            feeder.normalEnergizedLvFeeders
                .filter {
                    if (removeAssociatedFeeder)
                        it.normalEnergizingFeeders.all { feeder -> feeder.normalEnergizingSubstation == substation }
                    else
                        it.normalEnergizingFeeders.size == 1
                }
                .forEach { lvFeeder ->
                    lvFeeder.equipment.forEach { eq ->
                        removeEquipmentFromEquipmentContainerCascade(eq)
                    }
                    removeInternal(lvFeeder)
                }
        }

        return removeInternal(feeder as EquipmentContainer)
    }

    private fun removeEquipmentFromEquipmentContainerCascade(eq: Equipment) {
        (eq as? ConductingEquipment)?.let { removeInternal(it, true) }
            ?: (eq as? AuxiliaryEquipment)?.let { removeInternal(it) }
            ?: (eq as? PowerElectronicsUnit)?.let { removeInternal(it) }
            ?: removeInternal(eq)
    }

    protected fun removeInternal(geographicalRegion: GeographicalRegion, cascade: Boolean = false): Boolean {
        // Don't provide cascade removal at this level
        geographicalRegion.subGeographicalRegions.forEach { sgr ->
            sgr.geographicalRegion = null
        }

        return removeInternal(geographicalRegion as IdentifiedObject)
    }

    /**
     * Disassociate an object from this service.
     *
     * @param identifiedObject The object to disassociate from this service.
     *
     * @return true if the object is disassociated from this service.
     */
    private fun removeInternal(identifiedObject: IdentifiedObject): Boolean = objectsByType[identifiedObject::class]?.remove(identifiedObject.mRID) != null

    protected fun removeInternal(powerSystemResource: PowerSystemResource, cascade: Boolean = false): Boolean {
        powerSystemResource.assets.forEach { asset -> asset.removePowerSystemResource(powerSystemResource) }

        return removeInternal(powerSystemResource as IdentifiedObject)
    }

    protected fun removeInternal(site: Site, cascade: Boolean): Boolean {
        if (cascade) {
            site.equipment
                .filter { it.containers.size == 1 }
                .forEach { eq ->
                    removeEquipmentFromEquipmentContainerCascade(eq)
                }
        }

        return removeInternal(site as EquipmentContainer)
    }

    protected fun removeInternal(subGeographicalRegion: SubGeographicalRegion, cascade: Boolean = false): Boolean {
        subGeographicalRegion.geographicalRegion?.removeSubGeographicalRegion(subGeographicalRegion)
        subGeographicalRegion.substations.forEach { substation ->
            substation.subGeographicalRegion = null
        }

        return removeInternal(subGeographicalRegion as IdentifiedObject)
    }

    protected fun removeInternal(substation: Substation, cascade: Boolean, removeAssociatedFeeders: Boolean): Boolean {
        if (cascade) {
            substation.equipment
                .filter { it.containers.filterIsInstance<Feeder>().isEmpty() }
                .forEach { eq ->
                    removeEquipmentFromEquipmentContainerCascade(eq)
                }
            if (removeAssociatedFeeders) {
                substation.feeders.forEach { feeder ->
                    removeInternal(feeder, cascade = true, removeAssociatedFeeder = true)
                }
            }
        }
        substation.feeders.forEach { feeder ->
            feeder.normalEnergizingSubstation = null
        }
        substation.circuits.forEach { circuit ->
            circuit.removeEndSubstation(substation)
        }
        substation.loops.forEach { loop ->
            loop.removeSubstation(substation)
        }

        return removeInternal(substation as EquipmentContainer)
    }

    protected fun removeInternal(terminal: Terminal, cascade: Boolean = false): Boolean {
        terminal.conductingEquipment?.removeTerminal(terminal)
        terminal.connectivityNode?.removeTerminal(terminal)

        return removeInternal(terminal as AcDcTerminal)
    }


    /**
     * IEC61970 base equivalents
     */
    protected fun removeInternal(equivalentBranch: EquivalentBranch, cascade: Boolean): Boolean =
        removeInternal(equivalentBranch as EquivalentEquipment, cascade)

    protected fun removeInternal(equivalentEquipment: EquivalentEquipment, cascade: Boolean): Boolean =
        removeInternal(equivalentEquipment as ConductingEquipment, cascade)


    /**
     * IEC61970 base equivalents
     */
    protected fun removeInternal(accumulator: Accumulator, cascade: Boolean = false): Boolean = removeInternal(accumulator as Measurement)

    protected fun removeInternal(analog: Analog, cascade: Boolean = false): Boolean = removeInternal(analog as Measurement)

    protected fun removeInternal(control: Control, cascade: Boolean = false): Boolean {
        control.remoteControl?.control = null

        return removeInternal(control as IoPoint)
    }

    protected fun removeInternal(discrete: Discrete, cascade: Boolean = false): Boolean = removeInternal(discrete as Measurement)

    protected fun removeInternal(ioPoint: IoPoint, cascade: Boolean = false): Boolean = removeInternal(ioPoint as IdentifiedObject)

    protected fun removeInternal(measurement: Measurement, cascade: Boolean = false): Boolean {
        measurement.remoteSource?.measurement = null

        return removeInternal(measurement as IdentifiedObject)
    }


    /**
     * IEC61970 base protection
     */
    protected fun removeInternal(currentRelay: CurrentRelay, cascade: Boolean = false): Boolean = removeInternal(currentRelay as ProtectionRelayFunction)

    protected fun removeInternal(distanceRelay: DistanceRelay, cascade: Boolean = false): Boolean = removeInternal(distanceRelay as ProtectionRelayFunction)

    protected fun removeInternal(protectionRelayScheme: ProtectionRelayScheme, cascade: Boolean = false): Boolean {
        protectionRelayScheme.system?.removeScheme(protectionRelayScheme)
        protectionRelayScheme.functions.forEach { it.removeScheme(protectionRelayScheme) }

        return removeInternal(protectionRelayScheme as IdentifiedObject)
    }

    protected fun removeInternal(protectionRelaySystem: ProtectionRelaySystem, cascade: Boolean = false): Boolean {
        protectionRelaySystem.schemes.forEach { it.system = null }

        return removeInternal(protectionRelaySystem as Equipment)
    }

    protected fun removeInternal(voltageRelay: VoltageRelay, cascade: Boolean = false): Boolean = removeInternal(voltageRelay as ProtectionRelayFunction)


    /**
     * IEC61970 base scada
     */
    protected fun removeInternal(remoteControl: RemoteControl, cascade: Boolean = false): Boolean {
        remoteControl.control?.remoteControl = null

        return removeInternal(remoteControl as RemotePoint)
    }

    protected fun removeInternal(remotePoint: RemotePoint, cascade: Boolean = false): Boolean = removeInternal(remotePoint as IdentifiedObject)

    protected fun removeInternal(remoteSource: RemoteSource, cascade: Boolean = false): Boolean {
        remoteSource.measurement?.remoteSource = null

        return removeInternal(remoteSource as RemotePoint)
    }


    /**
     * IEC61970 base wires
     */
    protected fun removeInternal(acLineSegment: AcLineSegment, cascade: Boolean): Boolean {
        if (cascade) {
            acLineSegment.cuts.forEach { cut ->
                removeInternal(cut)
            }
            acLineSegment.clamps.forEach { clamp ->
                removeInternal(clamp)
            }
        }
        acLineSegment.cuts.forEach { cut ->
            cut.acLineSegment = null
        }
        acLineSegment.clamps.forEach { clamp ->
            clamp.acLineSegment = null
        }

        return removeInternal(acLineSegment as Conductor, cascade)
    }

    protected fun removeInternal(breaker: Breaker, cascade: Boolean): Boolean = removeInternal(breaker as ProtectedSwitch, cascade)

    protected fun removeInternal(busbarSection: BusbarSection, cascade: Boolean): Boolean = removeInternal(busbarSection as Connector, cascade)

    protected fun removeInternal(clamp: Clamp, cascade: Boolean): Boolean {
        clamp.acLineSegment?.removeClamp(clamp)

        return removeInternal(clamp as ConductingEquipment, cascade)
    }

    protected fun removeInternal(conductor: Conductor, cascade: Boolean): Boolean {
        return removeInternal(conductor as ConductingEquipment, cascade)
    }

    protected fun removeInternal(connector: Connector, cascade: Boolean): Boolean = removeInternal(connector as ConductingEquipment, cascade)

    protected fun removeInternal(cut: Cut, cascade: Boolean): Boolean {
        cut.acLineSegment?.removeCut(cut)

        return removeInternal(cut as Switch, cascade)
    }

    protected fun removeInternal(disconnector: Disconnector, cascade: Boolean): Boolean = removeInternal(disconnector as Switch, cascade)

    protected fun removeInternal(earthFaultCompensator: EarthFaultCompensator, cascade: Boolean): Boolean =
        removeInternal(earthFaultCompensator as ConductingEquipment, cascade)

    protected fun removeInternal(energyConnection: EnergyConnection, cascade: Boolean): Boolean =
        removeInternal(energyConnection as ConductingEquipment, cascade)

    protected fun removeInternal(energyConsumer: EnergyConsumer, cascade: Boolean): Boolean {
        energyConsumer.phases.forEach { phase ->
            removeInternal(phase)
        }
        return removeInternal(energyConsumer as EnergyConnection, cascade)
    }

    protected fun removeInternal(energyConsumerPhase: EnergyConsumerPhase, cascade: Boolean): Boolean {
        energyConsumerPhase.energyConsumer?.removePhase(energyConsumerPhase)

        return removeInternal(energyConsumerPhase as PowerSystemResource, cascade)
    }

    protected fun removeInternal(energySource: EnergySource, cascade: Boolean): Boolean {
        energySource.phases.forEach { phase ->
            removeInternal(phase)
        }
        return removeInternal(energySource as EnergyConnection, cascade)
    }

    protected fun removeInternal(energySourcePhase: EnergySourcePhase, cascade: Boolean): Boolean {
        energySourcePhase.energySource?.removePhase(energySourcePhase)

        return removeInternal(energySourcePhase as PowerSystemResource, cascade)
    }

    protected fun removeInternal(fuse: Fuse, cascade: Boolean): Boolean = removeInternal(fuse as Switch, cascade)

    protected fun removeInternal(ground: Ground, cascade: Boolean): Boolean =
        removeInternal(ground as ConductingEquipment, cascade)

    protected fun removeInternal(groundDisconnector: GroundDisconnector, cascade: Boolean): Boolean = removeInternal(groundDisconnector as Switch, cascade)

    protected fun removeInternal(groundingImpedance: GroundingImpedance, cascade: Boolean): Boolean =
        removeInternal(groundingImpedance as EarthFaultCompensator, cascade)

    protected fun removeInternal(jumper: Jumper, cascade: Boolean): Boolean = removeInternal(jumper as Switch, cascade)

    protected fun removeInternal(junction: Junction, cascade: Boolean): Boolean = removeInternal(junction as Connector, cascade)

    protected fun removeInternal(line: Line, cascade: Boolean): Boolean = removeInternal(line as EquipmentContainer, cascade)

    protected fun removeInternal(linearShuntCompensator: LinearShuntCompensator, cascade: Boolean): Boolean =
        removeInternal(linearShuntCompensator as ShuntCompensator, cascade)

    protected fun removeInternal(loadBreakSwitch: LoadBreakSwitch, cascade: Boolean): Boolean =
        removeInternal(loadBreakSwitch as ProtectedSwitch, cascade)

    protected fun removeInternal(perLengthImpedance: PerLengthImpedance, cascade: Boolean): Boolean =
        removeInternal(perLengthImpedance as PerLengthLineParameter, cascade)

    protected fun removeInternal(perLengthLineParameter: PerLengthLineParameter, cascade: Boolean): Boolean =
        removeInternal(perLengthLineParameter as IdentifiedObject)

    protected fun removeInternal(perLengthPhaseImpedance: PerLengthPhaseImpedance, cascade: Boolean): Boolean =
        removeInternal(perLengthPhaseImpedance as PerLengthImpedance, cascade)

    protected fun removeInternal(perLengthSequenceImpedance: PerLengthSequenceImpedance, cascade: Boolean): Boolean =
        removeInternal(perLengthSequenceImpedance as PerLengthImpedance, cascade)

    protected fun removeInternal(petersenCoil: PetersenCoil, cascade: Boolean): Boolean =
        removeInternal(petersenCoil as EarthFaultCompensator, cascade)

    protected fun removeInternal(powerElectronicsConnection: PowerElectronicsConnection, cascade: Boolean): Boolean {
        powerElectronicsConnection.units.forEach { unit -> removeInternal(unit) }
        powerElectronicsConnection.phases.forEach { phase -> removeInternal(phase) }

        return removeInternal(powerElectronicsConnection as RegulatingCondEq, cascade)
    }

    protected fun removeInternal(powerTransformer: PowerTransformer, cascade: Boolean): Boolean {
        powerTransformer.ends.forEach { end ->
            removeInternal(end, cascade)
        }

        return removeInternal(powerTransformer as ConductingEquipment, cascade)
    }

    protected fun removeInternal(powerTransformerEnd: PowerTransformerEnd, cascade: Boolean): Boolean =
        removeInternal(powerTransformerEnd as TransformerEnd, cascade)

    protected fun removeInternal(protectedSwitch: ProtectedSwitch, cascade: Boolean): Boolean {
        protectedSwitch.relayFunctions.forEach { rf ->
            rf.removeProtectedSwitch(protectedSwitch)
        }

        return removeInternal(protectedSwitch as Switch, cascade)
    }

    protected fun removeInternal(ratioTapChanger: RatioTapChanger, cascade: Boolean): Boolean {
        ratioTapChanger.transformerEnd?.ratioTapChanger = null

        return removeInternal(ratioTapChanger as TapChanger, cascade)
    }

    protected fun removeInternal(reactiveCapabilityCurve: ReactiveCapabilityCurve, cascade: Boolean): Boolean =
        removeInternal(reactiveCapabilityCurve as Curve, cascade)

    protected fun removeInternal(recloser: Recloser, cascade: Boolean): Boolean =
        removeInternal(recloser as ProtectedSwitch, cascade)

    protected fun removeInternal(regulatingCondEq: RegulatingCondEq, cascade: Boolean): Boolean {
        regulatingCondEq.regulatingControl?.removeRegulatingCondEq(regulatingCondEq)

        return removeInternal(regulatingCondEq as EnergyConnection, cascade)
    }

    protected fun removeInternal(regulatingControl: RegulatingControl, cascade: Boolean): Boolean =
        removeInternal(regulatingControl as PowerSystemResource, cascade)

    protected fun removeInternal(rotatingMachine: RotatingMachine, cascade: Boolean): Boolean {
        return removeInternal(rotatingMachine as RegulatingCondEq, cascade)
    }

    protected fun removeInternal(seriesCompensator: SeriesCompensator, cascade: Boolean): Boolean {
        return removeInternal(seriesCompensator as ConductingEquipment, cascade)
    }

    protected fun removeInternal(shuntCompensator: ShuntCompensator, cascade: Boolean): Boolean {
        return removeInternal(shuntCompensator as RegulatingCondEq, cascade)
    }

    protected fun removeInternal(staticVarCompensator: StaticVarCompensator, cascade: Boolean): Boolean {
        return removeInternal(staticVarCompensator as RegulatingCondEq, cascade)
    }

    protected fun removeInternal(switch: Switch, cascade: Boolean): Boolean {
        return removeInternal(switch as ConductingEquipment, cascade)
    }

    protected fun removeInternal(synchronousMachine: SynchronousMachine, cascade: Boolean): Boolean =
        removeInternal(synchronousMachine as RotatingMachine, cascade)

    protected fun removeInternal(tapChanger: TapChanger, cascade: Boolean): Boolean {
        if (cascade) {
            tapChanger.tapChangerControl?.let { removeInternal(it) }
        }

        return removeInternal(tapChanger as PowerSystemResource, cascade)
    }

    protected fun removeInternal(tapChangerControl: TapChangerControl, cascade: Boolean): Boolean =
        removeInternal(tapChangerControl as RegulatingControl, cascade)

    protected fun removeInternal(transformerEnd: TransformerEnd, cascade: Boolean): Boolean {
        if (cascade) {
            transformerEnd.ratioTapChanger?.let { removeInternal(it) }
        }

        return removeInternal(transformerEnd as IdentifiedObject)
    }

    protected fun removeInternal(transformerStarImpedance: TransformerStarImpedance, cascade: Boolean): Boolean =
        removeInternal(transformerStarImpedance as IdentifiedObject)


    /**
     * IEC61970 feeder
     */
    protected fun removeInternal(lvFeeder: LvFeeder, cascade: Boolean): Boolean {
        if (cascade) {
            lvFeeder.equipment
                .filter { it.containers.filterIsInstance<LvFeeder>().size == 1 }
                .filter { it != lvFeeder.normalHeadTerminal?.conductingEquipment }
                .forEach { eq ->
                    removeEquipmentFromEquipmentContainerCascade(eq)
                }
        }

        lvFeeder.normalEnergizingFeeders.forEach { feeder -> feeder.removeNormalEnergizedLvFeeder(lvFeeder) }

        return removeInternal(lvFeeder as EquipmentContainer)
    }

    protected fun removeInternal(circuit: Circuit, cascade: Boolean): Boolean {
        circuit.loop?.removeCircuit(circuit)
        circuit.endSubstations.forEach { substation ->
            substation.removeCircuit(circuit)
        }

        return removeInternal(circuit as Line, cascade)
    }

    protected fun removeInternal(loop: Loop, cascade: Boolean): Boolean {
        loop.circuits.forEach { circuit ->
            circuit.loop = null
        }
        loop.substations.forEach { substation ->
            substation.removeLoop(loop)
        }
        loop.energizingSubstations.forEach { substation ->
            substation.removeLoop(loop)
        }

        return removeInternal(loop as IdentifiedObject)
    }

    /**
     * IEC61970 base wires.generation.production
     */
    protected fun removeInternal(batteryUnit: BatteryUnit, cascade: Boolean): Boolean = removeInternal(batteryUnit as PowerElectronicsUnit, cascade)

    protected fun removeInternal(photoVoltaicUnit: PhotoVoltaicUnit, cascade: Boolean): Boolean = removeInternal(photoVoltaicUnit as PowerElectronicsUnit, cascade)

    protected fun removeInternal(powerElectronicsUnit: PowerElectronicsUnit, cascade: Boolean = false): Boolean {
        powerElectronicsUnit.powerElectronicsConnection?.removeUnit(powerElectronicsUnit)

        return removeInternal(powerElectronicsUnit as Equipment)
    }

    protected fun removeInternal(powerElectronicsWindUnit: PowerElectronicsWindUnit, cascade: Boolean): Boolean = removeInternal(powerElectronicsWindUnit as PowerElectronicsUnit, cascade)


    /**
     * INFIEC61970 - wires.generation.production
     */
    protected fun removeInternal(evChargingUnit: EvChargingUnit, cascade: Boolean): Boolean =
        removeInternal(evChargingUnit as PowerElectronicsUnit, cascade)




    protected fun removeInternal(asset: Asset, cascade: Boolean = false): Boolean {
        asset.powerSystemResources.forEach { psr -> psr.removeAsset(asset) }

        return removeInternal(asset as IdentifiedObject)
    }

    protected fun removeInternal(assetContainer: AssetContainer, cascade: Boolean = false): Boolean = removeInternal(assetContainer as Asset)

    protected fun removeInternal(structure: Structure, cascade: Boolean = false): Boolean = removeInternal(structure as AssetContainer)

    protected fun removeInternal(endDevice: EndDevice, cascade: Boolean = false): Boolean {
        endDevice.usagePoints.forEach { up ->
            up.removeEndDevice(endDevice)
        }

        return removeInternal(endDevice as AssetContainer)
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
