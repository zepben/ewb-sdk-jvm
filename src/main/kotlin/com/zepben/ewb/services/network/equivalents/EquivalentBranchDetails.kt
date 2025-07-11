/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.equivalents

import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.cim.iec61970.base.core.ConnectivityNode
import com.zepben.ewb.cim.iec61970.base.equivalents.EquivalentBranch

/**
 * Data class that holds instances used to create an [EquivalentBranch].
 *
 * @property edgeEquipment The [ConductingEquipment] at the edge of the included network.
 * @property edgeNode The [ConnectivityNode] extending beyond the included network.
 */
data class EquivalentBranchDetails(
    val edgeEquipment: ConductingEquipment,
    val edgeNode: ConnectivityNode
)
