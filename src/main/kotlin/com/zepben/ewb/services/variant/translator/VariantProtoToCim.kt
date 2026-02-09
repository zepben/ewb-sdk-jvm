/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.variant.translator

import com.zepben.protobuf.cim.iec61970.base.core.NameType as PBNameType
import com.zepben.protobuf.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject as PBNetworkModelProject
import com.zepben.protobuf.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent as PBNetworkModelProjectComponent
import com.zepben.protobuf.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency as PBAnnotatedProjectDependency
import com.zepben.protobuf.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage as PBNetworkModelProjectStage
import com.zepben.protobuf.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet as PBChangeSet
import com.zepben.protobuf.cim.iec61970.infiec61970.part303.genericdataset.ChangeSetMember as PBChangeSetMember
import com.zepben.protobuf.cim.iec61970.infiec61970.part303.genericdataset.DataSet as PBDataSet
import com.zepben.protobuf.cim.iec61970.infiec61970.part303.genericdataset.ObjectCreation as PBObjectCreation
import com.zepben.protobuf.cim.iec61970.infiec61970.part303.genericdataset.ObjectDeletion as PBObjectDeletion
import com.zepben.protobuf.cim.iec61970.infiec61970.part303.genericdataset.ObjectModification as PBObjectModification
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent
import com.zepben.ewb.cim.iec61970.base.core.EquipmentContainer
import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSetMember
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.DataSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectCreation
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectDeletion
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectModification
import com.zepben.ewb.services.common.Resolvers
import com.zepben.ewb.services.common.extensions.getOrThrow
import com.zepben.ewb.services.common.translator.AddFromPbResult
import com.zepben.ewb.services.common.translator.BaseProtoToCim
import com.zepben.ewb.services.common.translator.getOrAddFromPb
import com.zepben.ewb.services.common.translator.toCim
import com.zepben.ewb.services.common.translator.toInstant
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.translator.addFromPb
import com.zepben.ewb.services.variant.VariantService
import com.zepben.protobuf.vc.VariantChangeSetMember
import com.zepben.protobuf.vc.VariantChangeSetMember.ChangeSetMemberCase.OBJECTCREATION
import com.zepben.protobuf.vc.VariantChangeSetMember.ChangeSetMemberCase.OBJECTDELETION
import com.zepben.protobuf.vc.VariantChangeSetMember.ChangeSetMemberCase.OBJECTMODIFICATION
import com.zepben.protobuf.vc.VariantChangeSetMember.ChangeSetMemberCase.CHANGESETMEMBER_NOT_SET
import com.zepben.protobuf.vc.VariantIdentifiedObject
import com.zepben.protobuf.vc.VariantIdentifiedObject.IdentifiedObjectCase.*

fun VariantService.addFromPb(pb: VariantIdentifiedObject): AddFromPbResult =
    when (pb.identifiedObjectCase) {
        NETWORKMODELPROJECT -> getOrAddFromPb(pb.networkModelProject.mRID()) { addFromPb(pb.networkModelProject )}
        NETWORKMODELPROJECTSTAGE -> getOrAddFromPb(pb.networkModelProjectStage.mRID()) { addFromPb(pb.networkModelProjectStage )}
        ANNOTATEDPROJECTDEPENDENCY -> getOrAddFromPb(pb.annotatedProjectDependency.mRID()) { addFromPb(pb.annotatedProjectDependency) } // FIXME: move
        OTHER, IDENTIFIEDOBJECT_NOT_SET, null -> throw UnsupportedOperationException(
            "Identified object type ${pb.identifiedObjectCase} is not supported by the variant service"
        )
    }

//fun NetworkModelProjectService.addFromPb(pb: VariantDataSet): AddFromPbResult =
//    when (pb.dataSetCase) {
//        CHANGESET -> getOrAddFromPb<ChangeSet>(pb.changeSet.mRID()) { addFromPb(pb.changeSet) }
//        VariantDataSet.DataSetCase.OTHER, DATASET_NOT_SET, null -> throw UnsupportedOperationException(
//            "dataset object type ${pb.dataSetCase} is not supported by the variant service"
//        )
//    }

fun VariantService.addFromPb(pb: VariantChangeSetMember): ChangeSetMember =
    when (pb.changeSetMemberCase) {
        OBJECTCREATION -> toCim(pb.objectCreation, this)
        OBJECTDELETION -> toCim(pb.objectDeletion, this)
        OBJECTMODIFICATION -> toCim(pb.objectModification, this)
        VariantChangeSetMember.ChangeSetMemberCase.OTHER, CHANGESETMEMBER_NOT_SET, null -> throw UnsupportedOperationException(
            "changeSetMember object type ${pb.changeSetMemberCase} is not supported by the variant service"
        )
    }

/**
 * Convert the protobuf [PBNetworkModelProject] into its CIM counterpart.
 *
 * @param pb The protobuf [PBNetworkModelProject] to convert.
 * @param networkService The [VariantService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [NetworkModelProject].
 */
fun toCim(pb: PBNetworkModelProject, networkService: VariantService): NetworkModelProject =
    NetworkModelProject(pb.mRID()).apply {
        externalStatus = pb.externalStatusSet.takeUnless { pb.hasExternalStatusNull() }
        forecastCommissionDate = pb.forecastCommissionDateSet.takeUnless { pb.hasForecastCommissionDateNull() }?.toInstant()
        externalDriver = pb.externalDriverSet.takeUnless { pb.hasExternalDriverNull() }
        pb.childrenList.forEach { child ->
            networkService.resolveOrDeferReference(Resolvers.networkModelProjectComponents(this), child.mRID())
        }
        toCim(pb.nmpc, this, networkService)
    }

/**
 * Convert the protobuf [PBNetworkModelProjectStage] into its CIM counterpart.
 *
 * @param pb The protobuf [PBNetworkModelProjectStage] to convert.
 * @param networkService The [VariantService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [NetworkModelProjectStage].
 */
fun toCim(pb: PBNetworkModelProjectStage, networkService: VariantService): NetworkModelProjectStage =
    NetworkModelProjectStage(pb.mRID()).apply {
        plannedCommissionedDate = pb.plannedCommissionedDateSet.takeUnless { pb.hasPlannedCommissionedDateNull() }?.toInstant()
        commissionedDate = pb.commissionedDateSet.takeUnless { pb.hasCommissionedDateNull() }?.toInstant()
        confidenceLevel = pb.confidenceLevelSet.takeUnless { pb.hasConfidenceLevelNull() }
        baseModelVersion = pb.baseModelVersionSet.takeUnless { pb.hasBaseModelVersionNull() }
        lastConflictCheckedAt = pb.lastConflictCheckedAtSet.takeUnless { pb.hasLastConflictCheckedAtNull() }?.toInstant()
        userComments = pb.userCommentsSet.takeUnless { pb.hasUserCommentsNull() }
        setChangeSetMRID(pb.changeSetMRID)

        if ( !pb.hasChangeSetNull() ) {
            setChangeSet(toCim(pb.changeSetSet, networkService))
        }
        pb.dependingStageList.forEach {
            addDependingStage(toCim(it, networkService))
        }
        pb.dependentOnStageList.forEach {
            addDependentOnStage(toCim(it, networkService))
        }
        pb.equipmentContainersList.forEach { ec ->
            networkService.addFromPb(ec).identifiedObject?.let {
                addEquipmentContainer(it as EquipmentContainer)
            }
        }
    }.also {
        toCim(pb.nmpc, it, networkService)
    }

/**
 * Convert the protobuf [PBNetworkModelProjectComponent] into its CIM counterpart.
 *
 * @param pb The protobuf [PBNetworkModelProjectComponent] to convert.
 * @param networkService The [VariantService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [NetworkModelProjectComponent].
 */
fun toCim(pb: PBNetworkModelProjectComponent, cim: NetworkModelProjectComponent, networkService: VariantService): NetworkModelProjectComponent =
    cim.apply {
        created = pb.createdSet.takeUnless { pb.hasCreatedNull() }?.toInstant()
        updated = pb.updatedSet.takeUnless { pb.hasUpdatedNull() }?.toInstant()
        closed = pb.closedSet.takeUnless { pb.hasClosedNull() }?.toInstant()

        networkService.resolveOrDeferReference(Resolvers.networkModelProjects(this), pb.parentMRID)
    }

/**
 * Convert the protobuf [PBAnnotatedProjectDependency] into its CIM counterpart.
 *
 * @param pb The protobuf [PBAnnotatedProjectDependency] to convert.
 * @param networkService The [VariantService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [AnnotatedProjectDependency].
 */
fun toCim(pb: PBAnnotatedProjectDependency, networkService: VariantService): AnnotatedProjectDependency =
    AnnotatedProjectDependency(
        pb.mRID(),
    ).apply { //FIXME: DIS HAX
        dependencyType = mapDependencyKind.toCim(pb.dependencyType)
        networkService.resolveOrDeferReference(Resolvers.dependentnetworkModelProjectStage(this), pb.dependencyDependentOnStageMRID)
        networkService.resolveOrDeferReference(Resolvers.dependingnetworkModelProjectStage(this), pb.dependencyDependingStageMRID)
    }.also {
        toCim(pb.io, it, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBAnnotatedProjectDependency] to the [VariantService].
 */
fun VariantService.addFromPb(pb: PBAnnotatedProjectDependency): AnnotatedProjectDependency? = tryAddOrNull(toCim(pb, this))

/**
 * Convert the protobuf [PBDataSet] into its CIM counterpart.
 *
 * @param pb The protobuf [PBDataSet] to convert.
 * @param networkService The [VariantService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [DataSet].
 */
fun toCim(pb: PBDataSet, cim: DataSet, networkService: VariantService): DataSet =
    cim.apply {
        description = pb.descriptionSet.takeUnless { pb.hasDescriptionNull() }
        name = pb.nameSet.takeUnless { pb.hasNameNull() }
    }

/**
 * An extension to add a converted copy of the protobuf [PBNameType] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBNameType): NameType = toCim(pb, this) // Special case

/**
 * Convert the protobuf [PBChangeSet] into its CIM counterpart.
 *
 * @param pb The protobuf [PBChangeSet] to convert.
 * @param networkService The [VariantService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ChangeSet].
 */
fun toCim(pb: PBChangeSet, networkService: VariantService): ChangeSet =
    ChangeSet(pb.mRID()).also {
        networkService.dataSetsByMRID[it.mRID] = it
    }.apply {
        pb.changeSetMembersList.forEach {
            when (it.changeSetMemberCase) {
                OBJECTCREATION -> toCim(it.objectCreation, networkService)
                OBJECTDELETION -> toCim(it.objectDeletion, networkService)
                OBJECTMODIFICATION -> toCim(it.objectModification, networkService)
                VariantChangeSetMember.ChangeSetMemberCase.OTHER, CHANGESETMEMBER_NOT_SET, null -> throw UnsupportedOperationException(
                    "changeSetMember object type ${it.changeSetMemberCase} is not supported by the variant service"
                )
            }.also { csm ->
                addChangeSetMember( csm )
            }
        }
    }.also {
        toCim(pb.dataset, it, networkService)
    }

/**
 * Convert the protobuf [PBChangeSetMember] into its CIM counterpart.
 *
 * @param pb The protobuf [PBChangeSetMember] to convert.
 * @param service The [VariantService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ChangeSetMember].
 */
fun toCim(pb: PBChangeSetMember, cim: ChangeSetMember, service: VariantService): ChangeSetMember =
    cim.apply {
        setChangeSet(service.getOrThrow(pb.changeSetMRID, "${pb::class} ${pb.changeSetMRID}"))
        targetObjectMRID = pb.targetObjectMRID
    }

/**
 * Convert the protobuf [PBObjectCreation] into its CIM counterpart.
 *
 * @param pb The protobuf [PBObjectCreation] to convert.
 * @param networkService The [VariantService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ObjectCreation].
 */
fun toCim(pb: PBObjectCreation, networkService: VariantService): ObjectCreation =
    ObjectCreation().also {
        toCim(pb.csm, it, networkService)
    }

/**
 * Convert the protobuf [PBObjectDeletion] into its CIM counterpart.
 *
 * @param pb The protobuf [PBObjectDeletion] to convert.
 * @param networkService The [VariantService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ObjectDeletion].
 */
fun toCim(pb: PBObjectDeletion, networkService: VariantService): ObjectDeletion =
    ObjectDeletion().also {
        toCim(pb.csm, it, networkService)
    }

/**
 * Convert the protobuf [PBObjectModification] into its CIM counterpart.
 *
 * @param pb The protobuf [PBObjectModification] to convert.
 * @param networkService The [VariantService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ObjectModification].
 */
fun toCim(pb: PBObjectModification, networkService: VariantService): ObjectModification =
    ObjectModification().also {
        toCim(pb.csm, it, networkService)
        pb.objectReverseModification?.let { orm ->
            orm.csm.targetObjectMRID?.let { objectReverseModificationTargetObjectMRID ->
                it.setObjectReverseModification(objectReverseModificationTargetObjectMRID)
            }
        }
    }

/**
 * An extension to add a converted copy of the protobuf [PBNetworkModelProject] to the [VariantService].
 */
fun VariantService.addFromPb(pb: PBNetworkModelProject): NetworkModelProject? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBNetworkModelProjectStage] to the [VariantService].
 */
fun VariantService.addFromPb(pb: PBNetworkModelProjectStage): NetworkModelProjectStage? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBChangeSet] to the [VariantService].
 */
fun VariantService.addFromPb(pb: PBChangeSet): ChangeSet = toCim(pb, this)

// #################################
// # Class for Java friendly usage #
// #################################

/**
 * A helper class for Java friendly convertion from protobuf objects to their CIM counterparts.
 *
 * @property networkService The [VariantService] all converted objects should be added to.
 */
class VariantProtoToCim(val networkService: VariantService) : BaseProtoToCim() {

    /**
     * Add a converted copy of the protobuf [PBAnnotatedProjectDependency] to the [VariantService].
     *
     * @param pb The [PBAnnotatedProjectDependency] to convert.
     * @return The converted [AnnotatedProjectDependency]
     */
    fun addFromPb(pb: PBAnnotatedProjectDependency): AnnotatedProjectDependency? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBNetworkModelProjectStage] to the [VariantService].
     *
     * @param pb The [PBNetworkModelProjectStage] to convert.
     * @return The converted [NetworkModelProjectStage]
     */
    fun addFromPb(pb: PBNetworkModelProjectStage): NetworkModelProjectStage? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBChangeSet] to the [VariantService].
     *
     * @param pb The [PBChangeSet] to convert.
     * @return The converted [ChangeSet]
     */
    fun addFromPb(pb: PBChangeSet): ChangeSet? = networkService.addFromPb(pb)

//    /**
//     * Add a converted copy of the protobuf [PBObjectCreation] to the [NetworkModelProjectService].
//     *
//     * @param pb The [PBObjectCreation] to convert.
//     * @return The converted [ObjectCreation]
//     */
//    fun addFromPb(pb: PBObjectCreation): ObjectCreation? = networkService.addFromPb(pb)
//
//    /**
//     * Add a converted copy of the protobuf [PBObjectDeletion] to the [NetworkModelProjectService].
//     *
//     * @param pb The [PBObjectDeletion] to convert.
//     * @return The converted [ObjectDeletion]
//     */
//    fun addFromPb(pb: PBObjectDeletion): ObjectDeletion? = networkService.addFromPb(pb)
//
//    /**
//     * Add a converted copy of the protobuf [PBObjectModification] to the [NetworkModelProjectService].
//     *
//     * @param pb The [PBObjectModification] to convert.
//     * @return The converted [ObjectModification]
//     */
//    fun addFromPb(pb: PBObjectModification): ObjectModification? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBNetworkModelProject] to the [VariantService].
     *
     * @param pb The [PBNetworkModelProject] to convert.
     * @return The converted [NetworkModelProject]
     */
    fun addFromPb(pb: PBNetworkModelProject): NetworkModelProject? = networkService.addFromPb(pb)

}
