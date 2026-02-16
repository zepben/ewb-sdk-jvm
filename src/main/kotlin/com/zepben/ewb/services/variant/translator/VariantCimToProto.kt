/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.variant.translator

import com.google.protobuf.NullValue
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent
import com.zepben.ewb.cim.iec61970.base.core.Identifiable
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSetMember
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.DataSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectCreation
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectDeletion
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectModification
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectReverseModification
import com.zepben.ewb.services.common.translator.*
import com.zepben.ewb.services.variant.whenVariantIdentifiedObject
import com.zepben.protobuf.vc.VariantObject
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
import com.zepben.protobuf.cim.iec61970.infiec61970.part303.genericdataset.ObjectReverseModification as PBObjectReverseModification

/**
 * Convert the [variantObject] to a [VariantObject] representation.
 */
fun variantObject(identified: Identifiable): VariantObject =
    VariantObject.newBuilder().apply {
        whenVariantIdentifiedObject(
            identified,
            isNetworkModelProject = { networkModelProject = it.toPb() },
            isNetworkModelProjectStage = { networkModelProjectStage = it.toPb() },
            isAnnotatedProjectDependency = { annotatedProjectDependency = it.toPb() },
            isChangeSet = { changeSet = it.toPb() },
            isObjectCreation = { objectCreation = it.toPb() },
            isObjectDeletion = { objectDeletion = it.toPb() },
            isObjectModification = { objectModification = it.toPb() },
        )
    }.build()

// ###################################################################
// # Extensions IEC61970 InfIEC61970 InfPart303 NetworkModelProjects #
// ###################################################################

/**
 * Convert the [NetworkModelProject] into its protobuf counterpart.
 *
 * @param cim The [NetworkModelProject] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: NetworkModelProject, pb: PBNetworkModelProject.Builder): PBNetworkModelProject.Builder =
    pb.apply {
        cim.externalStatus?.also { externalStatusSet = it } ?: run { externalStatusNull = NullValue.NULL_VALUE }
        cim.forecastCommissionDate?.also { forecastCommissionDateSet = it.toTimestamp() } ?: run { forecastCommissionDateNull = NullValue.NULL_VALUE }
        cim.externalDriver?.also { externalDriverSet = it } ?: run { externalDriverNull = NullValue.NULL_VALUE }
        cim.children.forEach {
            addChildrenMRIDs(it.mRID)
        }
    }.also {
        toPb(cim, pb.nmpcBuilder)
    }

/**
 * Convert the [NetworkModelProjectComponent] into its protobuf counterpart.
 *
 * @param cim The [NetworkModelProjectComponent] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: NetworkModelProjectComponent, pb: PBNetworkModelProjectComponent.Builder): PBNetworkModelProjectComponent.Builder =
    pb.apply {
        cim.created?.also { createdSet = it.toTimestamp() } ?: run { createdNull = NullValue.NULL_VALUE }
        cim.updated?.also { updatedSet = it.toTimestamp() } ?: run { updatedNull = NullValue.NULL_VALUE }
        cim.closed?.also { closedSet = it.toTimestamp() } ?: run { closedNull = NullValue.NULL_VALUE }
        cim.parent?.also { parentMRID = it.mRID } ?: clearParentMRID()

    }.also {
        toPb(cim, pb.ioBuilder)
    }

/**
 * An extension for converting any [NetworkModelProject] into its protobuf counterpart.
 */
fun NetworkModelProject.toPb(): PBNetworkModelProject = toPb(this, PBNetworkModelProject.newBuilder()).build()


// ########################################################
// # IEC61970 InfIEC61970 InfPart303 NetworkModelProjects #
// ########################################################

/**
 * Convert the [AnnotatedProjectDependency] into its protobuf counterpart.
 *
 * @param cim The [AnnotatedProjectDependency] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: AnnotatedProjectDependency, pb: PBAnnotatedProjectDependency.Builder): PBAnnotatedProjectDependency.Builder =
    pb.apply {
        cim.dependencyType.also { dependencyType = mapDependencyKind.toPb(it) }
        cim.dependencyDependingStage?.also { dependencyDependingStageMRID = it.mRID } ?: clearDependencyDependingStageMRID()
        cim.dependencyDependentOnStage?.also { dependencyDependentOnStageMRID = it.mRID } ?: clearDependencyDependentOnStageMRID()

        toPb(cim, ioBuilder)
    }


/**
 * Convert the [NetworkModelProjectStage] into its protobuf counterpart.
 *
 * @param cim The [NetworkModelProjectStage] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: NetworkModelProjectStage, pb: PBNetworkModelProjectStage.Builder): PBNetworkModelProjectStage.Builder =
    pb.apply {
        cim.plannedCommissionedDate?.also { plannedCommissionedDateSet = it.toTimestamp() } ?: run { plannedCommissionedDateNull = NullValue.NULL_VALUE }
        cim.commissionedDate?.also { commissionedDateSet = it.toTimestamp() } ?: run { commissionedDateNull = NullValue.NULL_VALUE }
        cim.confidenceLevel?.also { confidenceLevelSet = it } ?: run { confidenceLevelNull = NullValue.NULL_VALUE }
        cim.baseModelVersion?.also { baseModelVersionSet = it } ?: run { baseModelVersionNull = NullValue.NULL_VALUE }
        cim.lastConflictCheckedAt?.also { lastConflictCheckedAtSet = it.toTimestamp() } ?: run { lastConflictCheckedAtNull = NullValue.NULL_VALUE }
        cim.userComments?.also { userCommentsSet = it } ?: run { userCommentsNull = NullValue.NULL_VALUE }
        cim.changeSet?.also { changeSetMRIDSet = it.mRID } ?: run { changeSetMRIDNull = NullValue.NULL_VALUE }
        cim.dependentOnStage.forEach {
            addDependentOnStageMRID(it.mRID)
        }
        cim.dependingStage.forEach {
            addDependingStageMRID(it.mRID)
        }
        cim.equipmentContainerMRIDs.forEach {
            addEquipmentContainerMRIDs(it)
        }

        toPb(cim, pb.nmpcBuilder)
    }

/**
 * An extension for converting any [AnnotatedProjectDependency] into its protobuf counterpart.
 */
fun AnnotatedProjectDependency.toPb(): PBAnnotatedProjectDependency = toPb(this, PBAnnotatedProjectDependency.newBuilder()).build()

/**
 * An extension for converting any [NetworkModelProjectStage] into its protobuf counterpart.
 */
fun NetworkModelProjectStage.toPb(): PBNetworkModelProjectStage = toPb(this, PBNetworkModelProjectStage.newBuilder()).build()

// ###############################################
// # IEC61970 InfIEC61970 Part303 GenericDataSet #
// ###############################################

/**
 * Convert the [DataSet] into its protobuf counterpart.
 *
 * @param cim The [DataSet] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: DataSet, pb: PBDataSet.Builder): PBDataSet.Builder =
    pb.apply {
        mrid = cim.mRID
        cim.name?.also { nameSet = it } ?: run { nameNull = NullValue.NULL_VALUE }
        cim.description?.also { descriptionSet = it } ?: run { descriptionNull = NullValue.NULL_VALUE }
    }

/**
 * Convert the [ChangeSet] into its protobuf counterpart.
 *
 * @param cim The [ChangeSet] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ChangeSet, pb: PBChangeSet.Builder): PBChangeSet.Builder =
    pb.apply {
        // NOTE: changeSetMembers are sent separately
        cim.networkModelProjectStage?.also { networkModelProjectStageMRID = it.mRID } ?: clearNetworkModelProjectStageMRID()
        toPb(cim, datasetBuilder)
    }

/**
 * Convert the [ChangeSetMember] into its protobuf counterpart.
 *
 * @param cim The [ChangeSetMember] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ChangeSetMember, pb: PBChangeSetMember.Builder): PBChangeSetMember.Builder =
    pb.apply {
        changeSetMRID = cim.changeSet.mRID
        targetObjectMRID = cim.targetObjectMRID
    }

/**
 * Convert the [ObjectCreation] into its protobuf counterpart.
 *
 * @param cim The [ObjectCreation] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ObjectCreation, pb: PBObjectCreation.Builder): PBObjectCreation.Builder =
    pb.apply {
        toPb(cim, csmBuilder)
    }

/**
 * Convert the [ObjectDeletion] into its protobuf counterpart.
 *
 * @param cim The [ObjectDeletion] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ObjectDeletion, pb: PBObjectDeletion.Builder): PBObjectDeletion.Builder =
    pb.apply {
        toPb(cim, csmBuilder)
    }

/**
 * Convert the [ObjectModification] into its protobuf counterpart.
 *
 * @param cim The [ObjectModification] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ObjectModification, pb: PBObjectModification.Builder): PBObjectModification.Builder =
    pb.apply {
        pb.objectReverseModification = toPb(cim.objectReverseModification, objectReverseModificationBuilder).build()

        toPb(cim, csmBuilder)
    }

/**
 * Convert the [ObjectReverseModification] into its protobuf counterpart.
 *
 * @param cim The [ObjectReverseModification] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ObjectReverseModification, pb: PBObjectReverseModification.Builder): PBObjectReverseModification.Builder =
    pb.apply {
        toPb(cim, csmBuilder)
    }

/**
 * An extension for converting any [ChangeSet] into its protobuf counterpart.
 */
fun ChangeSet.toPb(): PBChangeSet = toPb(this, PBChangeSet.newBuilder()).build()

/**
 * An extension for converting any [ObjectCreation] into its protobuf counterpart.
 */
fun ObjectCreation.toPb(): PBObjectCreation = toPb(this, PBObjectCreation.newBuilder()).build()

/**
 * An extension for converting any [ObjectDeletion] into its protobuf counterpart.
 */
fun ObjectDeletion.toPb(): PBObjectDeletion = toPb(this, PBObjectDeletion.newBuilder()).build()

/**
 * An extension for converting any [ObjectModification] into its protobuf counterpart.
 */
fun ObjectModification.toPb(): PBObjectModification = toPb(this, PBObjectModification.newBuilder()).build()


// #################################
// # Class for Java friendly usage #
// #################################

/**
 * A helper class for Java friendly convertion from CIM objects to their protobuf counterparts.
 */
class NetworkModelProjectCimToProto : BaseCimToProto() {

    /**
     * Convert the [NetworkModelProject] into its protobuf counterpart.
     *
     * @param cim The [NetworkModelProject] to convert.
     * @return the protobuf form of [cim]
     */
    fun toPb(cim: NetworkModelProject): PBNetworkModelProject = cim.toPb()

    /**
     * Convert the [AnnotatedProjectDependency] into its protobuf counterpart.
     *
     * @param cim The [AnnotatedProjectDependency] to convert.
     * @return the protobuf form of [cim]
     */
    fun toPb(cim: AnnotatedProjectDependency): PBAnnotatedProjectDependency = cim.toPb()

    /**
     * Convert the [NetworkModelProjectStage] into its protobuf counterpart.
     *
     * @param cim The [NetworkModelProjectStage] to convert.
     * @return the protobuf form of [cim]
     */
    fun toPb(cim: NetworkModelProjectStage): PBNetworkModelProjectStage = cim.toPb()

    /**
     * Convert the [ChangeSet] into its protobuf counterpart.
     *
     * @param cim The [ChangeSet] to convert.
     * @return the protobuf form of [cim]
     */
    fun toPb(cim: ChangeSet): PBChangeSet = cim.toPb()

    /**
     * Convert the [ObjectCreation] into its protobuf counterpart.
     *
     * @param cim The [ObjectCreation] to convert.
     * @return the protobuf form of [cim]
     */
    fun toPb(cim: ObjectCreation): PBObjectCreation = cim.toPb()

    /**
     * Convert the [ObjectDeletion] into its protobuf counterpart.
     *
     * @param cim The [ObjectDeletion] to convert.
     * @return the protobuf form of [cim]
     */
    fun toPb(cim: ObjectDeletion): PBObjectDeletion = cim.toPb()

    /**
     * Convert the [ObjectModification] into its protobuf counterpart.
     *
     * @param cim The [ObjectModification] to convert.
     * @return the protobuf form of [cim]
     */
    fun toPb(cim: ObjectModification): PBObjectModification = cim.toPb()

}
