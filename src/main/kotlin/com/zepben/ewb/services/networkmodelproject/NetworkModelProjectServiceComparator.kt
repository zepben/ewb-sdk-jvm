/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.networkmodelproject

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.services.common.BaseServiceComparator
import com.zepben.ewb.services.common.ObjectDifference

/**
 * A class for comparing the contents of a [NetworkModelProjectService].
 *
 * NOTE: Unused functions have been suppressed for this class as they are access by reflection rather than directly. This
 *       means they are always flagged as unused. By suppressing the warning it also means you might not be testing every
 *       function, so make sure you check the code coverage
 */
@Suppress("unused")
class NetworkModelProjectServiceComparator : BaseServiceComparator() {

    private fun ObjectDifference<out NetworkModelProjectComponent>.compareNetworkModelProjectComponent(): ObjectDifference<out NetworkModelProjectComponent> =
        apply {
            compareIdentifiedObject()
            compareValues(
                NetworkModelProjectComponent::created,
                NetworkModelProjectComponent::updated,
                NetworkModelProjectComponent::closed,
                NetworkModelProjectComponent::parent
            )
        }

    private fun ObjectDifference<out NetworkModelProject>.compareNetworkModelProject(): ObjectDifference<out NetworkModelProject> =
        apply {
            compareNetworkModelProjectComponent()
            compareValues(
                NetworkModelProject::externalStatus,
                NetworkModelProject::forecastCommissionDate,
                NetworkModelProject::externalDriver,
                NetworkModelProject::children,
            )
        }

    private fun ObjectDifference<out NetworkModelProjectStage>.compareNetworkModelProjectStage(): ObjectDifference<out NetworkModelProjectStage> =
        apply {
            compareNetworkModelProjectComponent()
            compareValues(
                NetworkModelProjectStage::plannedCommissionedDate,
                NetworkModelProjectStage::commissionedDate,
                NetworkModelProjectStage::confidenceLevel,
                NetworkModelProjectStage::baseModelVersion,
                NetworkModelProjectStage::lastConflictCheckedAt,
                NetworkModelProjectStage::userComments,
                NetworkModelProjectStage::changeSet,
                NetworkModelProjectStage::dependentOnStage,
                NetworkModelProjectStage::dependingStage,
                NetworkModelProjectStage::equipmentContainers,
            )
        }

}
