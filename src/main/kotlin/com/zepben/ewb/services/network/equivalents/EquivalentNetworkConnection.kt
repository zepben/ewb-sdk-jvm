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
 * Data class that holds references to the edge [ConductingEquipment] and [ConnectivityNode] to which an 'equivalent network' was attached.
 * Additionally, is holds references to each [EquivalentBranch] and their [ConductingEquipment] that make up the 'equivalent network'.
 *
 * @property edgeEquipment The [ConductingEquipment] at the edge of the included network.
 * @property edgeNode The [ConnectivityNode] extending beyond the included network. This will connect the [edgeEquipment] to each created [EquivalentBranch].
 * @property branchToEquipment A collection of the created [EquivalentBranch] instances, and the [ConductingEquipment] that was created on each branch.
 */
data class EquivalentNetworkConnection(
    val edgeEquipment: ConductingEquipment,
    val edgeNode: ConnectivityNode,
    val branchToEquipment: Map<EquivalentBranch, Set<ConductingEquipment>>
)
