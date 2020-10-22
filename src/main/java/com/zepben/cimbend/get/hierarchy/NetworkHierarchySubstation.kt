/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.get.hierarchy

/**
 * A simplified representation of a substation for requesting the network hierarchy.
 */
class NetworkHierarchySubstation(
    mRID: String,
    name: String,
    val feeders: Map<String, NetworkHierarchyFeeder>,
    var subGeographicalRegion: NetworkHierarchySubGeographicalRegion? = null
) : NetworkHierarchyIdentifiedObject(mRID, name)
