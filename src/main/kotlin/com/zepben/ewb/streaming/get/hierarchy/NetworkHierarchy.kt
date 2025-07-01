/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get.hierarchy

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.GeographicalRegion
import com.zepben.ewb.cim.iec61970.base.core.SubGeographicalRegion
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit

/**
 * Container for simplified network hierarchy objects
 */
class NetworkHierarchy(
    val geographicalRegions: Map<String, GeographicalRegion>,
    val subGeographicalRegions: Map<String, SubGeographicalRegion>,
    val substations: Map<String, Substation>,
    val feeders: Map<String, Feeder>,
    val circuits: Map<String, Circuit>,
    val loops: Map<String, Loop>
)
