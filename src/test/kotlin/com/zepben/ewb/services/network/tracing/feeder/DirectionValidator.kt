/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.feeder

import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

object DirectionValidator {

    fun Terminal.validateDirection(feederDirection: FeederDirection, stateOperators: NetworkStateOperators) {
        assertThat(stateOperators.getDirection(this), equalTo(feederDirection))
    }

}
