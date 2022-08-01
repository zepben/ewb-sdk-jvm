/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal
import com.zepben.evolve.services.network.tracing.traversals.Tracker
import com.zepben.evolve.services.network.tracing.traversals.TraversalQueue

/**
 * Traversal of [ConductingEquipmentStep] which wraps [BasicTraversal] for the purposes of starting directly from [ConductingEquipment].
 */
class ConnectedEquipmentTraversal(
    queueNext: QueueNext<ConductingEquipmentStep>,
    queue: TraversalQueue<ConductingEquipmentStep>,
    tracker: Tracker<ConductingEquipmentStep>
) : BasicTraversal<ConductingEquipmentStep>(queueNext, queue, tracker) {

    /**
     * Helper function to start the traversal from a [ConductingEquipment] without needing to explicitly creating the [ConductingEquipmentStep].
     *
     * @param conductingEquipment THe [ConductingEquipment] to start from.
     */
    fun run(conductingEquipment: ConductingEquipment) = run(ConductingEquipmentStep(conductingEquipment))

}
