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
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.services.common.BaseServiceComparator
import com.zepben.ewb.services.common.ObjectDifference

/**
 * A class for comparing the contents of a [VariantService].
 *
 * NOTE: Unused functions have been suppressed for this class as they are access by reflection rather than directly. This
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

    private fun compareNetworkModelProject(source: NetworkModelProject, target: NetworkModelProject): ObjectDifference<NetworkModelProject> =
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

    private fun compareNetworkModelProjectStage(source: NetworkModelProjectStage, target: NetworkModelProjectStage): ObjectDifference<NetworkModelProjectStage> =
        ObjectDifference(source, target).apply {
            compareNetworkModelProjectComponent()
            compareValues(
                NetworkModelProjectStage::plannedCommissionedDate,
                NetworkModelProjectStage::commissionedDate,
                NetworkModelProjectStage::confidenceLevel,
                NetworkModelProjectStage::baseModelVersion,
                NetworkModelProjectStage::lastConflictCheckedAt,
                NetworkModelProjectStage::userComments,
                NetworkModelProjectStage::changeSet
            )
            compareIdReferenceCollections(
                NetworkModelProjectStage::dependentOnStage,
                NetworkModelProjectStage::dependingStage,
                //NetworkModelProjectStage::equipmentContainers, // FIXME: not resolving via addFromPb
            )

        }

    private fun compareAnnotatedProjectDependencies(source: AnnotatedProjectDependency, target: AnnotatedProjectDependency): ObjectDifference<AnnotatedProjectDependency> =
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

    private fun compareChangeSets(source: ChangeSet, target: ChangeSet): ObjectDifference<ChangeSet> =
        ObjectDifference(source, target).apply {
            compareIndexedValueCollections(
                ChangeSet::changeSetMembers,
            )
        }

}
