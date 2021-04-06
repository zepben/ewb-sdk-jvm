/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get.hierarchy

/**
 * A simplified representation of a sub geographical region for requesting the network hierarchy.
 */
class NetworkHierarchySubGeographicalRegion(
    mRID: String,
    name: String,
    val substations: Map<String, NetworkHierarchySubstation>,
    var geographicalRegion: NetworkHierarchyGeographicalRegion? = null
) : NetworkHierarchyIdentifiedObject(mRID, name)
