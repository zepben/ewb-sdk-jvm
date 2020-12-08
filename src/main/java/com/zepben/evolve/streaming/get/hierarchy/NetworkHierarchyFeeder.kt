/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get.hierarchy

/**
 * A simplified representation of a feeder for requesting the network hierarchy.
 */
class NetworkHierarchyFeeder(
    mRID: String,
    name: String,
    var substation: NetworkHierarchySubstation? = null
) : NetworkHierarchyIdentifiedObject(mRID, name)
