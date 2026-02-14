/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject

/**
 * Represents the relationship between two network model project stages.
 *
 * @param dependencyType Describes the dependency relationship between the two classes.
 * @param dependencyDependentOnStage NetworkModelProjectStage required by this stage.
 * @param dependencyDependingStage NetworkModelProjectStages that cannot be applied alongside this stage.
 */
class AnnotatedProjectDependency(mRID: String) : IdentifiedObject(mRID) {

    var dependencyType: DependencyKind = DependencyKind.UNKNOWN

    var dependencyDependentOnStage: NetworkModelProjectStage? = null
    var dependencyDependingStage: NetworkModelProjectStage? = null

}
