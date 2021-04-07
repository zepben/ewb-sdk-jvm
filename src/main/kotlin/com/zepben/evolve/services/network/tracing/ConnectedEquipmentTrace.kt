/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.Tracing.createBasicBreadthTrace
import com.zepben.evolve.services.network.tracing.Tracing.createBasicDepthTrace
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal

/**
 * A class that creates commonly used connectivity based traces. These ignores phases, they are purely to trace equipment that
 * are connected in any way. You can add custom step actions and stop conditions to the returned traversal.
 */
object ConnectedEquipmentTrace {

    /**
     * @return a traversal that traces equipment that are connected, ignoring open status.
     */
    fun newConnectedEquipmentTrace(): BasicTraversal<ConductingEquipment> = createBasicDepthTrace(queueNext(OpenTest.IGNORE_OPEN))

    /**
     * @return a traversal that traces equipment that are connected, ignoring open status.
     */
    fun newConnectedEquipmentBreadthTrace(): BasicTraversal<ConductingEquipment> = createBasicBreadthTrace(queueNext(OpenTest.IGNORE_OPEN))

    /**
     * @return a traversal that traces equipment that are connected stopping at normally open points.
     */
    fun newNormalConnectedEquipmentTrace(): BasicTraversal<ConductingEquipment> = createBasicDepthTrace(queueNext(OpenTest.NORMALLY_OPEN))

    /**
     * @return a traversal that traces equipment that are connected stopping at currently open points.
     */
    fun newCurrentConnectedEquipmentTrace(): BasicTraversal<ConductingEquipment> = createBasicDepthTrace(queueNext(OpenTest.CURRENTLY_OPEN))

    private fun queueNext(openTest: OpenTest): BasicTraversal.QueueNext<ConductingEquipment> = BasicTraversal.QueueNext { conductingEquipment, traversal ->
        if (!openTest.isOpen(conductingEquipment, null)) {
            NetworkService.connectedEquipment(conductingEquipment)
                .mapNotNull { it.to }
                .forEach { traversal.queue.add(it) }
        }
    }

}
