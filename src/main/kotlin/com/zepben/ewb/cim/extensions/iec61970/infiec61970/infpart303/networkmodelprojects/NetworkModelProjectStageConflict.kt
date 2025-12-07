/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import java.time.Instant

/**
 * [ZBEX] A conflict between the network model project state and the base network.
 */
class NetworkModelProjectStageConflict @JvmOverloads constructor(mRID: String = "") {

    private var _stage: NetworkModelProjectStage? = null
    /**
     * [ZBEX] The date expected for this stage to be commissioned.
     */
    var plannedCommissionedDate: Instant? = null

    /**
     * [ZBEX] The stage the conflicts belong too.
     */
    val stage: NetworkModelProjectStage? get() = _stage
}