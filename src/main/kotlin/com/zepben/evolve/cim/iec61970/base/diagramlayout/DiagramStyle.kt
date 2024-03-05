/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.diagramlayout

/**
 * The diagram style refer to a style used by the originating system for a diagram.  A diagram style describes
 * information such as schematic, geographic, bus-branch etc.
 *
 * @property SCHEMATIC The diagram should be styled as a schematic view.
 * @property GEOGRAPHIC The diagram should be styled as a geographic view.
 */
enum class DiagramStyle {

    SCHEMATIC,
    GEOGRAPHIC
}
