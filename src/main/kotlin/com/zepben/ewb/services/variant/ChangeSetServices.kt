/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.variant

import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61970.base.core.Identifiable
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.*
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.exceptions.UnsupportedIdentifiedObjectException
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.ewb.services.diagram.DiagramService
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.whenNetworkServiceObject
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*


/**
 * Contains a set of network, diagram, and customer services representing the contents of all changesets contained
 * within [variantService].
 *
 * Note change sets represented by this service must only be populated against one base model of the network.
 *
 * @param newNetworkService Contains the ObjectCreation and ObjectModifications to the NetworkService.
 * @param originalNetworkService Contains the ObjectDeletion and ObjectReverseModifications to the NetworkService.
 * @param newDiagramService Contains the ObjectCreation and ObjectModifications to the DiagramService.
 * @param originalDiagramService Contains the ObjectDeletion and ObjectReverseModifications to the DiagramService.
 * @param newCustomerService Contains the ObjectCreation and ObjectModifications to the CustomerService.
 * @param originalCustomerService Contains the ObjectDeletion and ObjectReverseModifications to the CustomerService.
 */
class ChangeSetServices(
    val variantService: VariantService = VariantService(),
    val newNetworkService: NetworkService = NetworkService(),
    val originalNetworkService: NetworkService = NetworkService(),
    val newDiagramService: DiagramService = DiagramService(),
    val originalDiagramService: DiagramService = DiagramService(),
    val newCustomerService: CustomerService = CustomerService(),
    val originalCustomerService: CustomerService = CustomerService(),
)
{

    /**
     * Helper to get a change set from the variantService.
     */
    fun getChangeSet(mRID: String): ChangeSet? = variantService.get<ChangeSet>(mRID)

    /**
     * Helper to get all change sets from the variantService.
     */
    fun changeSets(): Sequence<ChangeSet> = variantService.sequenceOf<ChangeSet>()

    fun get(changeSetMember: ChangeSetMember): Identifiable {
        require(variantService.get<ChangeSetMember>(changeSetMember.mRID) != null) { "${changeSetMember.typeNameAndMRID()} must be present in the VariantService" }
        return when (changeSetMember) {
            is ObjectCreation,
            is ObjectModification -> getFromNew(changeSetMember.targetObjectMRID)
            is ObjectDeletion -> getFromOriginal(changeSetMember.targetObjectMRID)
            else -> throw IllegalStateException("${changeSetMember.typeNameAndMRID()} class is unhandled")

        }
    }

    /**
     * Fetch the created objects for a collection of [ObjectCreation]s
     */
    fun created(obj: Collection<ObjectCreation>): Sequence<Identifiable> = obj.map { getFromNew(it.targetObjectMRID) }.asSequence()

    /**
     * Fetch the modified objects for a collection of [ObjectModification]s
     */
    fun modified(obj: Collection<ObjectModification>): Sequence<Identifiable> = obj.map { getFromNew(it.targetObjectMRID) }.asSequence()

    /**
     * Fetch the deleted objects for a collection of [ObjectDeletion]s
     */
    fun deleted(obj: Collection<ObjectDeletion>): Sequence<Identifiable> = obj.map { getFromOriginal(it.targetObjectMRID) }.asSequence()

    /**
     * Fetch the reverse modification for a collection of [ObjectModification]s
     */
    fun original(obj: Collection<ObjectModification>): Sequence<Identifiable> = obj.map { getFromOriginal(it.targetObjectMRID) }.asSequence()

    /**
     * Get the reverse modification object for [objectModification], which should be the object from the base model of the variant.
     */
    fun getReverseModification(objectModification: ObjectModification): Identifiable =
        getFromOriginal(objectModification.targetObjectMRID)

    private fun getFromNew(mRID: String): Identifiable =
        newNetworkService[mRID] ?: newDiagramService[mRID] ?: newCustomerService[mRID]
        ?: throw IllegalStateException("$mRID was not found in any new service - have you removed it or messed with the service?")

    private fun getFromOriginal(mRID: String): Identifiable =
        originalNetworkService[mRID] ?: originalDiagramService[mRID] ?: originalCustomerService[mRID]
        ?: throw IllegalStateException("$mRID was not found in any new service - have you removed it or messed with the service?")


    /**
     * Helper function to add a changeset to [variantService]
     */
    fun addChangeSet(changeSet: ChangeSet): Boolean = variantService.add(changeSet)

    /**
     * Create and add a new ObjectCreation for the provided [Identifiable]
     *
     * @param newObject The object that has been added.
     */
    fun addCreation(changeSet: ChangeSet, newObject: Identifiable): Boolean {
        ObjectCreation().also { creation ->
            creation.targetObjectMRID = newObject.mRID
            creation.changeSet = changeSet
            changeSet.addMember(creation)
            variantService.add(creation)
        }

        return addToNew(newObject)
    }

    /**
     * Create and add a new ObjectModification with changes from [newObject], and save the
     * [originalObject] as a reverse modification.
     *
     * @param newObject The object that has been changed.
     * @param originalObject The original object, before changes.
     * @return
     */
    fun addModification(changeSet: ChangeSet, newObject: Identifiable, originalObject: Identifiable): Boolean {
        ObjectModification().also { modification ->
            modification.targetObjectMRID = newObject.mRID
            modification.changeSet = changeSet
            changeSet.addMember(modification)
            variantService.add(modification)
        }

        addModificationAssociations(originalObject, originalNetworkService) // TODO test this and logic may be bad...
        addModificationAssociations(newObject, newNetworkService) // TODO test this
        addToOriginal(originalObject) // ObjectReverseModification - we don't care about whether this succeeded as it should have be false if the obj is already present.
        return addToNew(newObject)
    }

    /**
     * Create and add a new ObjectDeletion for the provided [Identifiable]
     *
     * @param originalObject The object to delete.
     */
    fun addDeletion(changeSet: ChangeSet, originalObject: Identifiable): Boolean {
        ObjectDeletion().also { deletion ->
            deletion.targetObjectMRID = originalObject.mRID
            deletion.changeSet = changeSet
            changeSet.addMember(deletion)
            variantService.add(deletion)
        }

        return addToOriginal(originalObject)
    }

    /**
     * Add a base object that has no changes to the original service.
     * This object may be needed as it is referenced by other objects
     * that have been changed.
     *
     * @param originalObject The object to add, should be an object from the base model.
     */
    fun addOriginal(originalObject: Identifiable): Boolean =
        addToOriginal(originalObject)

    /**
     * @return true if the object was added to any service, false if the object already existed in one of the services and couldn't be added.
     * @throws UnsupportedIdentifiedObjectException if [identifiable] is not supported by any service.
     */
    private fun addToOriginal(identifiable: Identifiable): Boolean =
        try {
            originalNetworkService.tryAdd(identifiable).let { added ->
                if (added) {
                    if (identifiable is Organisation) {
                        originalCustomerService.add(identifiable)   // TODO: double check this... does this make sense?
                    } else {
                        true
                    }
                } else {
                    false
                }
            }
        } catch (e: UnsupportedIdentifiedObjectException) {
            try {
                originalDiagramService.tryAdd(identifiable)
            } catch (e: UnsupportedIdentifiedObjectException) {
                originalCustomerService.tryAdd(identifiable)
            }
        }

    /**
     * @return true if the object was added to any service, false if the object already existed in one of the services and couldn't be added.
     * @throws UnsupportedIdentifiedObjectException if [identifiable] is not supported by any service.
     */
    private fun addToNew(identifiable: Identifiable): Boolean =
        try {
            newNetworkService.tryAdd(identifiable).let { added ->
                if (added) {
                    if (identifiable is Organisation) {
                        newCustomerService.add(identifiable)   // TODO: double check this... does this make sense?
                    } else {
                        true
                    }
                } else {
                    false
                }
            }
        } catch (e: UnsupportedIdentifiedObjectException) {
            try {
                newDiagramService.tryAdd(identifiable)
            } catch (e: UnsupportedIdentifiedObjectException) {
                newCustomerService.tryAdd(identifiable)
            }
        }

    private fun addModificationAssociations(io: Identifiable, service: BaseService) {
        io::class.memberProperties
            .filter { it.visibility == KVisibility.PUBLIC }
            .filterIsInstance<KMutableProperty<*>>()
            .filter { it.getter.call(io) != null }
            .forEach { prop ->
                val returnType = prop.returnType
                if (returnType.isSubtypeOf(Identifiable::class.createType())) {
                    val associatedIdentifiable = prop.getter.call(io) as Identifiable?
                    associatedIdentifiable?.let { service.tryAdd(it) }
                } else if (returnType.isSubtypeOf(Iterable::class.starProjectedType)
                    && returnType.arguments.isNotEmpty()
                    && returnType.arguments[0].type?.isSubtypeOf(Identifiable::class.createType()) == true
                ) {
                    val associatedIdentifiables = prop.getter.call(io) as Iterable<Identifiable>?
                    associatedIdentifiables?.forEach {
                        service.tryAdd(it)
                    }
                }

            }
    }

}
