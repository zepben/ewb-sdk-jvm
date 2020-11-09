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
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;
import static com.zepben.cimbend.database.Column.Nullable.NULL;

@EverythingIsNonnullByDefault
public abstract class TableTransformerEnds extends TableIdentifiedObjects {

    public final Column END_NUMBER = new Column(++columnIndex, "end_number", "INTEGER", NOT_NULL);
    public final Column TERMINAL_MRID = new Column(++columnIndex, "terminal_mrid", "TEXT", NULL);
    public final Column BASE_VOLTAGE_MRID = new Column(++columnIndex, "base_voltage_mrid", "TEXT", NULL);
    public final Column GROUNDED = new Column(++columnIndex, "grounded", "BOOLEAN", NOT_NULL);
    public final Column R_GROUND = new Column(++columnIndex, "r_ground", "NUMBER", NOT_NULL);
    public final Column X_GROUND = new Column(++columnIndex, "x_ground", "NUMBER", NOT_NULL);

}
