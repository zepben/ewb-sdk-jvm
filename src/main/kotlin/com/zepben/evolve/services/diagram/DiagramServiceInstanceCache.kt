/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.diagram

import com.google.common.collect.Interners
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObjectPoint

@Suppress("UnstableApiUsage")
class DiagramServiceInstanceCache {

    private val diagramObjectPoints = Interners.newStrongInterner<DiagramObjectPoint>()

    fun intern(item: DiagramObjectPoint): DiagramObjectPoint = diagramObjectPoints.intern(item)
}
