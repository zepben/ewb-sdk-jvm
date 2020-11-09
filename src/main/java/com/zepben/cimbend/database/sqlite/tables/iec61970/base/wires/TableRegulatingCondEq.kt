/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.wires;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;

/**
 * Represents the energy consumers table.
 */
@EverythingIsNonnullByDefault
public abstract class TableRegulatingCondEq extends TableEnergyConnections {

    public final Column CONTROL_ENABLED = new Column(++columnIndex, "control_enabled", "BOOLEAN");

}
