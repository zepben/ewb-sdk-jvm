/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

object DirectionValidator {

    fun Terminal.validateDirections(expectedNormalDirection: FeederDirection, expectedCurrentDirection: FeederDirection = expectedNormalDirection) {
        assertThat(normalFeederDirection, equalTo(expectedNormalDirection))
        assertThat(currentFeederDirection, equalTo(expectedCurrentDirection))
    }

}
