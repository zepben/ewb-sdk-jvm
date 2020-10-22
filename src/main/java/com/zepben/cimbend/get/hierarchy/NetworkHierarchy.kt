/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.get.hierarchy

/**
 * Container for simplified network hierarchy objects
 */
class NetworkHierarchy(
    val geographicalRegions: Map<String, NetworkHierarchyGeographicalRegion>,
    val subGeographicalRegions: Map<String, NetworkHierarchySubGeographicalRegion>,
    val substations: Map<String, NetworkHierarchySubstation>,
    val feeders: Map<String, NetworkHierarchyFeeder>
)
