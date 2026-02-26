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
import com.zepben.ewb.services.common.exceptions.UnsupportedIdentifiedObjectException
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.ewb.services.diagram.DiagramService
import com.zepben.ewb.services.network.NetworkService


/**
 *
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
     * The [ChangeSet] modelled within this [VariantService]
     */
    val changeSet by lazy { variantService.listOf<ChangeSet>().first() }

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
     * Get the reverse modification object for [objectModification].
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
     * Create and add a new ObjectCreation for the provided [Identifiable]
     *
     * @param newObject The object that has been added.
     */
    fun addCreation(newObject: Identifiable) {
        ObjectCreation().also { creation ->
            creation.targetObjectMRID = newObject.mRID
            creation.changeSet = changeSet
            changeSet.addMember(creation)
            variantService.add(creation)
        }
        addToNew(newObject)
    }

    /**
     * Create and add a new ObjectModification with changes from [newObject], and save the
     * [originalObject] as a reverse modification.
     *
     * @param newObject The object that has been changed.
     * @param originalObject The original object, before changes.
     */
    fun addModification(newObject: Identifiable, originalObject: Identifiable) {
        ObjectModification().also { modification ->
            modification.targetObjectMRID = newObject.mRID
            modification.changeSet = changeSet
            changeSet.addMember(modification)
            variantService.add(modification)
        }

        addToNew(newObject)
        addToOriginal(originalObject) // ObjectReverseModification
    }

    /**
     * Create and add a new ObjectDeletion for the provided [Identifiable]
     *
     * @param originalObject The object to delete.
     */
    fun addDeletion(originalObject: Identifiable) {
        ObjectDeletion().also { deletion ->
            deletion.targetObjectMRID = originalObject.mRID
            deletion.changeSet = changeSet
            changeSet.addMember(deletion)
            variantService.add(deletion)
        }

        addToOriginal(originalObject)
    }

    /**
     * Add a base object that has no changes to the original service.
     * This object may be needed as it is referenced by other objects
     * that have been changed.
     */
    fun addOriginal(originalObject: Identifiable): Boolean =
        addToOriginal(originalObject)

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

    private fun addToNew(identifiable: Identifiable) {
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
    }

}
