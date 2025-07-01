/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.diagramlayout

/**
 * The diagram style refer to a style used by the originating system for a diagram.  A diagram style describes
 * information such as schematic, geographic, bus-branch etc.
 */
enum class DiagramStyle {

    /**
     * The diagram should be styled as a schematic view.
     */
    SCHEMATIC,

    /**
     * The diagram should be styled as a geographic view.
     */
    GEOGRAPHIC

}
