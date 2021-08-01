/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.network.tracing.traversals.Tracker

class ConnectivityTracker : Tracker<ConnectivityResult> {

    private val visited = mutableSetOf<ConductingEquipment>()

    override fun hasVisited(item: ConnectivityResult): Boolean = item.to?.let { visited.contains(it) } ?: false

    override fun visit(item: ConnectivityResult): Boolean = item.to?.let { visited.add(it) } ?: false

    override fun clear() {
        visited.clear()
    }

}
