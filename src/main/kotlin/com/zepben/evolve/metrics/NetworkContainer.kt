/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.metrics

import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.GeographicalRegion
import com.zepben.evolve.cim.iec61970.base.core.SubGeographicalRegion
import com.zepben.evolve.cim.iec61970.base.core.Substation
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder

/**
 * A union type of TotalNetworkContainer (all network processed in a run) and PartialNetworkContainer (network within a equipment container)
 */
sealed interface NetworkContainer

data class PartialNetworkContainer(
    val level: NetworkLevel,
    val mRID: String,
    val name: String
) : NetworkContainer

data object TotalNetworkContainer : NetworkContainer

enum class NetworkLevel {
    GeographicalRegion,
    SubGeographicalRegion,
    Substation,
    SubstationTotal,
    Feeder,
    FeederTotal,
    LvFeeder
}

fun GeographicalRegion.toNetworkContainer(): PartialNetworkContainer =
    PartialNetworkContainer(NetworkLevel.GeographicalRegion, mRID, name)
fun SubGeographicalRegion.toNetworkContainer(): PartialNetworkContainer =
    PartialNetworkContainer(NetworkLevel.SubGeographicalRegion, mRID, name)
fun Substation.toNetworkContainer(includeDownstream: Boolean = false): PartialNetworkContainer =
    PartialNetworkContainer(if (includeDownstream) NetworkLevel.SubstationTotal else NetworkLevel.Substation, mRID, name)
fun Feeder.toNetworkContainer(includeDownstream: Boolean = false): PartialNetworkContainer =
    PartialNetworkContainer(if (includeDownstream) NetworkLevel.FeederTotal else NetworkLevel.Feeder, mRID, name)
fun LvFeeder.toNetworkContainer(): PartialNetworkContainer =
    PartialNetworkContainer(NetworkLevel.LvFeeder, mRID, name)

// Java interop
fun networkContainer(geographicalRegion: GeographicalRegion): PartialNetworkContainer = geographicalRegion.toNetworkContainer()

fun networkContainer(subGeographicalRegion: SubGeographicalRegion): PartialNetworkContainer = subGeographicalRegion.toNetworkContainer()

@JvmOverloads
fun networkContainer(substation: Substation, includeDownstream: Boolean = false): PartialNetworkContainer = substation.toNetworkContainer(includeDownstream)

@JvmOverloads
fun networkContainer(feeder: Feeder, includeDownstream: Boolean = false): PartialNetworkContainer = feeder.toNetworkContainer(includeDownstream)

fun networkContainer(lvFeeder: LvFeeder): PartialNetworkContainer = lvFeeder.toNetworkContainer()
