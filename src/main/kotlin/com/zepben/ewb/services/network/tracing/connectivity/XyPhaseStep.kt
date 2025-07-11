/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.connectivity

import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.cim.iec61970.base.core.Terminal

/***
 * @param terminal the incoming terminal
 * @param phaseCode the phases used to get to this step (should only be, XY, X or Y)
 */
data class XyPhaseStep(
    val terminal: Terminal,
    val phaseCode: PhaseCode
)
