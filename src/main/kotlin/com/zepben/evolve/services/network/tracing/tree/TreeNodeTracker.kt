/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.tree

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.network.tracing.traversals.Tracker

/**
 * Simple tracker for traversals that just tracks the items visited.
 */
class TreeNodeTracker : Tracker<TreeNode> {

    private val visited = mutableSetOf<ConductingEquipment>()

    override fun hasVisited(item: TreeNode): Boolean = visited.contains(item.conductingEquipment)

    override fun visit(item: TreeNode): Boolean = visited.add(item.conductingEquipment)

    override fun clear() {
        visited.clear()
    }

}
