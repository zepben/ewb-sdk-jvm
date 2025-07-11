/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core.TableCurves

/**
 * A class representing the ReactiveCapabilityCurve columns required for the database table.
 */
class TableReactiveCapabilityCurves : TableCurves() {

    override val name: String = "reactive_capability_curves"

}
