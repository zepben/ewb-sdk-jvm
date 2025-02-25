/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.operators

import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep

/**
 * State aware operations relating to network conducting equipment connectivity.
 */
interface ConnectivityStateOperators {

    /**
     * Provides the next "paths" (from terminal to terminal) from a current path. This provides 'connected' terminals in a fashion
     * that is suitable for tracing and not just terminals that are connected via connectivity nodes. For example:
     * - Finding connected terminals on an AcLineSegment when the current path is at a clamp.
     * - Stepping to and from a BusbarSection terminal.
     */
    fun nextPaths(path: NetworkTraceStep.Path): Sequence<NetworkTraceStep.Path>

}
