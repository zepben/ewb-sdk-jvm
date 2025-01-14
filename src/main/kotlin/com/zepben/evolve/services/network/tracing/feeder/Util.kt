/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.Terminal


internal fun Terminal.isFeederHeadTerminal(): Boolean =
    conductingEquipment?.let { ce ->
        ce.containers
            .asSequence()
            .filterIsInstance<Feeder>()
            .any { it.normalHeadTerminal == this }
    } == true
