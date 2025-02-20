/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.operators

import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep

interface ConnectivityStateOperators {

    fun nextPaths(path: NetworkTraceStep.Path): Sequence<NetworkTraceStep.Path>

}
