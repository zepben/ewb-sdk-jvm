/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.feeder.DirectionSelector

interface NetworkStateOperators {
    // TODO [Review]: Should OpenTest become OpenOperators and DirectionSelector become DirectionOperators?
    val openTest: OpenTest
    val directionSelector: DirectionSelector

    companion object {
        val NORMAL = object : NetworkStateOperators {
            override val openTest: OpenTest get() = OpenTest.NORMALLY_OPEN
            override val directionSelector: DirectionSelector get() = DirectionSelector.NORMAL_DIRECTION
        }

        val CURRENT = object : NetworkStateOperators {
            override val openTest: OpenTest get() = OpenTest.CURRENTLY_OPEN
            override val directionSelector: DirectionSelector get() = DirectionSelector.CURRENT_DIRECTION
        }
    }
}
