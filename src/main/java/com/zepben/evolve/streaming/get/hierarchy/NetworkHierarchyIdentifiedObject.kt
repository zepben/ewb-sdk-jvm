/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get.hierarchy

/**
 * A simplified representation of an identified object for requesting the network hierarchy.
 */
abstract class NetworkHierarchyIdentifiedObject(
    val mRID: String,
    val name: String
)
