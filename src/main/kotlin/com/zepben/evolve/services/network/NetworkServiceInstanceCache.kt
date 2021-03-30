/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network

import com.google.common.collect.Interners
import com.zepben.evolve.cim.iec61968.common.PositionPoint
import com.zepben.evolve.cim.iec61968.common.StreetAddress
import com.zepben.evolve.cim.iec61968.common.TownDetail

@Suppress("UnstableApiUsage")
class NetworkServiceInstanceCache {

    private val townDetails = Interners.newStrongInterner<TownDetail>()
    private val streetAddresses = Interners.newStrongInterner<StreetAddress>()
    private val positionPoints = Interners.newStrongInterner<PositionPoint>()

    fun intern(item: TownDetail): TownDetail = townDetails.intern(item)
    fun intern(item: StreetAddress): StreetAddress = streetAddresses.intern(item)
    fun intern(item: PositionPoint): PositionPoint = positionPoints.intern(item)
}
