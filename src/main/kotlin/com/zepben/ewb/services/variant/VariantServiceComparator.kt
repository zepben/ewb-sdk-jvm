/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.variant

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.*
import com.zepben.ewb.services.common.BaseServiceComparator
import com.zepben.ewb.services.common.ObjectDifference

/**
 * A class for comparing the contents of a [VariantService].
 *
 * NOTE: Unused functions have been suppressed for this class as they are accessed by reflection rather than directly. This
 *       means they are always flagged as unused. By suppressing the warning it also means you might not be testing every
 *       function, so make sure you check the code coverage
 */
@Suppress("unused")
class VariantServiceComparator : BaseServiceComparator() {

    private fun ObjectDifference<out NetworkModelProjectComponent>.compareNetworkModelProjectComponent(): ObjectDifference<out NetworkModelProjectComponent> =
        ObjectDifference(source, target).apply {
            compareIdentifiedObject()
            compareValues(
                NetworkModelProjectComponent::created,
                NetworkModelProjectComponent::updated,
                NetworkModelProjectComponent::closed,
                NetworkModelProjectComponent::parent
            )
        }

    fun compareNetworkModelProject(source: NetworkModelProject, target: NetworkModelProject): ObjectDifference<NetworkModelProject> =
        ObjectDifference(source, target).apply {
            compareNetworkModelProjectComponent()
            compareValues(
                NetworkModelProject::externalStatus,
                NetworkModelProject::forecastCommissionDate,
                NetworkModelProject::externalDriver,
            )
            compareIdReferenceCollections(
                NetworkModelProject::children
            )

        }

    fun compareNetworkModelProjectStage(source: NetworkModelProjectStage, target: NetworkModelProjectStage): ObjectDifference<NetworkModelProjectStage> =
        ObjectDifference(source, target).apply {
            compareNetworkModelProjectComponent()
            compareValues(
                NetworkModelProjectStage::plannedCommissionedDate,
                NetworkModelProjectStage::commissionedDate,
                NetworkModelProjectStage::confidenceLevel,
                NetworkModelProjectStage::baseModelVersion,
                NetworkModelProjectStage::lastConflictCheckedAt,
                NetworkModelProjectStage::userComments,
                NetworkModelProjectStage::changeSet,
            )
            compareIndexedValueCollections(NetworkModelProjectStage::equipmentContainerMRIDs)
            compareIdReferenceCollections(
                NetworkModelProjectStage::dependentOnStage,
                NetworkModelProjectStage::dependingStage,
            )

        }

    fun compareAnnotatedProjectDependencies(source: AnnotatedProjectDependency, target: AnnotatedProjectDependency): ObjectDifference<AnnotatedProjectDependency> =
        ObjectDifference(source, target).apply {
            compareIdentifiedObject()
            compareValues(
                AnnotatedProjectDependency::dependencyType,
            )
            compareIdReferences(
                AnnotatedProjectDependency::dependencyDependentOnStage,
                AnnotatedProjectDependency::dependencyDependingStage,
            )

        }

    private fun ObjectDifference<out DataSet>.compareDataSet(): ObjectDifference<out DataSet> =
        ObjectDifference(source, target).apply {
            compareIdentifiable()
            compareValues(DataSet::name, DataSet::description)
        }

    fun compareChangeSets(source: ChangeSet, target: ChangeSet): ObjectDifference<ChangeSet> =
        ObjectDifference(source, target).apply {
            compareDataSet()
            compareIdReferenceCollections(ChangeSet::changeSetMembers)
            compareIdReferences(ChangeSet::networkModelProjectStage)
        }

    private fun ObjectDifference<out ChangeSetMember>.compareChangeSetMember(): ObjectDifference<out ChangeSetMember> =
        ObjectDifference(source, target).apply {
            compareIdentifiable()
            compareValues(ChangeSetMember::targetObjectMRID)
        }

    fun compareObjectCreation(source: ObjectCreation, target: ObjectCreation): ObjectDifference<ObjectCreation> =
        ObjectDifference(source, target).apply {
            compareChangeSetMember()
            compareValues(ObjectCreation::targetObjectMRID)

            compareIdReferences(ObjectCreation::changeSet)
        }

    fun compareObjectDeletion(source: ObjectDeletion, target: ObjectDeletion): ObjectDifference<ObjectDeletion> =
        ObjectDifference(source, target).apply {
            compareChangeSetMember()
            compareValues(ObjectDeletion::targetObjectMRID)

            compareIdReferences(ObjectDeletion::changeSet)
        }

    fun compareObjectModification(source: ObjectModification, target: ObjectModification): ObjectDifference<ObjectModification> =
        ObjectDifference(source, target).apply {
            compareChangeSetMember()
            compareValues(ObjectModification::targetObjectMRID)

            // Reverse modifications are not part of the VariantService, so always compared from a modification.
            compareObjectReverseModification(source.objectReverseModification, target.objectReverseModification)

            compareIdReferences(ObjectModification::changeSet)
        }

    private fun compareObjectReverseModification(source: ObjectReverseModification, target: ObjectReverseModification): ObjectDifference<ObjectReverseModification> =
        ObjectDifference(source, target).apply {
            compareChangeSetMember()
            compareValues(ObjectReverseModification::targetObjectMRID)

            compareIdReferences(ObjectReverseModification::changeSet)
            compareIdReferences(ObjectReverseModification::objectModification)
        }


}
