/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.network

import com.google.common.collect.Interners
import com.zepben.cimbend.cim.iec61968.common.PositionPoint
import com.zepben.cimbend.cim.iec61968.common.StreetAddress
import com.zepben.cimbend.cim.iec61968.common.TownDetail

@Suppress("UnstableApiUsage")
class NetworkServiceInstanceCache {

    private val townDetails = Interners.newStrongInterner<TownDetail>()
    private val streetAddresses = Interners.newStrongInterner<StreetAddress>()
    private val positionPoints = Interners.newStrongInterner<PositionPoint>()

    fun intern(item: TownDetail): TownDetail = townDetails.intern(item)
    fun intern(item: StreetAddress): StreetAddress = streetAddresses.intern(item)
    fun intern(item: PositionPoint): PositionPoint = positionPoints.intern(item)
}
