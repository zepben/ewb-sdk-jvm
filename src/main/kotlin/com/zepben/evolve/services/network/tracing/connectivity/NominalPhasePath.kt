/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind

/**
 * Defines how a nominal phase is wired through a connectivity node between two terminals
 *
 * @property from The nominal phase where the path comes from.
 * @property to The nominal phase where the path goes to.
 */
data class NominalPhasePath(val from: SinglePhaseKind, val to: SinglePhaseKind)
