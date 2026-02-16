/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.variant.translator

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent
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
import com.zepben.ewb.services.common.translator.*
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.variant.VariantService
import com.zepben.protobuf.vc.VariantChangeSetMember
import com.zepben.protobuf.vc.VariantChangeSetMember.ChangeSetMemberCase.*
import com.zepben.protobuf.vc.VariantObject
import com.zepben.protobuf.vc.VariantObject.ObjectCase.*
import com.zepben.protobuf.vc.VariantObject.ObjectCase.OBJECTCREATION
import com.zepben.protobuf.vc.VariantObject.ObjectCase.OBJECTDELETION
import com.zepben.protobuf.vc.VariantObject.ObjectCase.OBJECTMODIFICATION
import com.zepben.protobuf.vc.VariantObject.ObjectCase.OTHER
import com.zepben.protobuf.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject as PBNetworkModelProject
import com.zepben.protobuf.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent as PBNetworkModelProjectComponent
import com.zepben.protobuf.cim.iec61970.base.core.NameType as PBNameType
import com.zepben.protobuf.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency as PBAnnotatedProjectDependency
import com.zepben.protobuf.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage as PBNetworkModelProjectStage
import com.zepben.protobuf.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet as PBChangeSet
import com.zepben.protobuf.cim.iec61970.infiec61970.part303.genericdataset.ChangeSetMember as PBChangeSetMember
import com.zepben.protobuf.cim.iec61970.infiec61970.part303.genericdataset.DataSet as PBDataSet
import com.zepben.protobuf.cim.iec61970.infiec61970.part303.genericdataset.ObjectCreation as PBObjectCreation
import com.zepben.protobuf.cim.iec61970.infiec61970.part303.genericdataset.ObjectDeletion as PBObjectDeletion
import com.zepben.protobuf.cim.iec61970.infiec61970.part303.genericdataset.ObjectModification as PBObjectModification

fun VariantService.addFromPb(pb: VariantObject): AddFromPbResult =
    when (pb.objectCase) {
        NETWORKMODELPROJECT -> getOrAddFromPb(pb.networkModelProject.mRID()) { addFromPb(pb.networkModelProject )}
        NETWORKMODELPROJECTSTAGE -> getOrAddFromPb(pb.networkModelProjectStage.mRID()) { addFromPb(pb.networkModelProjectStage )}
        ANNOTATEDPROJECTDEPENDENCY -> getOrAddFromPb(pb.annotatedProjectDependency.mRID()) { addFromPb(pb.annotatedProjectDependency) }
        CHANGESET -> getOrAddFromPb(pb.changeSet.mRID()) { addFromPb(pb.changeSet) }
        OBJECTCREATION -> addFromPb(pb.objectCreation)
        OBJECTDELETION -> addFromPb(pb.objectDeletion)
        OBJECTMODIFICATION -> addFromPb(pb.objectModification)
        OTHER, OBJECT_NOT_SET, null -> throw UnsupportedOperationException(
            "Object type ${pb.objectCase} is not supported by the variant service"
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
        pb.childrenMRIDsList.forEach { child ->
            networkService.resolveOrDeferReference(Resolvers.networkModelProjectComponents(this), child)
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
        pb.changeSetMRIDSet.takeUnless {pb.hasChangeSetMRIDNull() }?.let {
            networkService.resolveOrDeferReference(Resolvers.changeSet(this), it)
        }

        pb.dependingStageMRIDList.forEach {
            networkService.resolveOrDeferReference(Resolvers.dependingStage(this), it)
        }
        pb.dependentOnStageMRIDList.forEach {
            networkService.resolveOrDeferReference(Resolvers.dependentOnStage(this), it)
        }
        pb.equipmentContainerMRIDsList.forEach { ec ->
            addContainer(ec)
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
        networkService.resolveOrDeferReference(Resolvers.dependentOnStage(this), pb.dependencyDependentOnStageMRID)
        networkService.resolveOrDeferReference(Resolvers.dependingStage(this), pb.dependencyDependingStageMRID)
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
fun toCim(pb: PBDataSet, cim: DataSet): DataSet =
    cim.apply {
        description = pb.descriptionSet.takeUnless { pb.hasDescriptionNull() }
        name = pb.nameSet.takeUnless { pb.hasNameNull() }
    }

/**
 * An extension to add a converted copy of the protobuf [PBNameType] to the [NetworkService].
 */
fun VariantService.addFromPb(pb: PBNameType): NameType = toCim(pb, this) // Special case

/**
 * Convert the protobuf [PBChangeSet] into its CIM counterpart.
 *
 * @param pb The protobuf [PBChangeSet] to convert.
 * @param networkService The [VariantService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ChangeSet].
 */
fun toCim(pb: PBChangeSet, networkService: VariantService): ChangeSet =
    ChangeSet(pb.mRID()).apply {
        // Note: changeSetMembers are always resolved in the reverse direction, and thus the ChangeSet must be sent before any ChangeSetMember
        networkService.resolveOrDeferReference(Resolvers.stage(this), pb.networkModelProjectStageMRID)

        toCim(pb.dataset, this)
    }

/**
 * Convert the protobuf [PBChangeSetMember] into its CIM counterpart.
 *
 * @param pb The protobuf [PBChangeSetMember] to convert.
 * @param service The [VariantService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ChangeSetMember].
 */
inline fun <reified T : ChangeSetMember> toCim(pb: PBChangeSetMember, service: VariantService, creator: (ChangeSet, String) -> T): T =
    creator(service.getOrThrow(pb.changeSetMRID, "${pb::class} ${pb.changeSetMRID}"), pb.targetObjectMRID).apply {
        changeSet.addMember(this)
    }

/**
 * Convert the protobuf [PBObjectCreation] into its CIM counterpart.
 *
 * @param pb The protobuf [PBObjectCreation] to convert.
 * @param networkService The [VariantService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ObjectCreation].
 */
fun toCim(pb: PBObjectCreation, networkService: VariantService): ObjectCreation =
    toCim(pb.csm, networkService) { cs, target ->
        ObjectCreation(cs, target)
    }

/**
 * Convert the protobuf [PBObjectDeletion] into its CIM counterpart.
 *
 * @param pb The protobuf [PBObjectDeletion] to convert.
 * @param networkService The [VariantService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ObjectDeletion].
 */
fun toCim(pb: PBObjectDeletion, networkService: VariantService): ObjectDeletion =
    toCim(pb.csm, networkService) { cs, target ->
        ObjectDeletion(cs, target)
    }

/**
 * Convert the protobuf [PBObjectModification] into its CIM counterpart.
 *
 * @param pb The protobuf [PBObjectModification] to convert.
 * @param networkService The [VariantService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ObjectModification].
 */
fun toCim(pb: PBObjectModification, networkService: VariantService): ObjectModification =
    toCim(pb.csm, networkService) { cs, target ->
        ObjectModification(cs, target).also {
            cs.addMember(it.objectReverseModification)
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
fun VariantService.addFromPb(pb: PBChangeSet): ChangeSet? = tryAddOrNull(toCim(pb, this))


/**
 * An extension to add a converted copy of the protobuf [PBObjectCreation] to the [VariantService].
 */
fun VariantService.addFromPb(pb: PBObjectCreation): AddFromPbResult =
    toCim(pb, this).let { AddFromPbResult("${it.changeSet.mRID}-${it.targetObjectMRID}", null, false) }


/**
 * An extension to add a converted copy of the protobuf [PBObjectDeletion] to the [VariantService].
 */
fun VariantService.addFromPb(pb: PBObjectDeletion): AddFromPbResult =
    toCim(pb, this).let { AddFromPbResult("${it.changeSet.mRID}-${it.targetObjectMRID}", null, false) }

/**
 * An extension to add a converted copy of the protobuf [PBObjectModification] to the [VariantService].
 */
fun VariantService.addFromPb(pb: PBObjectModification): AddFromPbResult =
    toCim(pb, this).let { AddFromPbResult("${it.changeSet.mRID}-${it.targetObjectMRID}", null, false) }

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
