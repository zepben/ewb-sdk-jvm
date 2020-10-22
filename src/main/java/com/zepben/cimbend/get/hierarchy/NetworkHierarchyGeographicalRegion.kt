/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.get.hierarchy

/**
 * A simplified representation of a geographical region for requesting the network hierarchy.
 */
class NetworkHierarchyGeographicalRegion(
    mRID: String,
    name: String,
    val subGeographicalRegions: Map<String, NetworkHierarchySubGeographicalRegion>
) : NetworkHierarchyIdentifiedObject(mRID, name)
