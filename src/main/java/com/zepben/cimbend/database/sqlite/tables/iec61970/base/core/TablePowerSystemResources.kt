/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.core;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;
import static com.zepben.cimbend.database.Column.Nullable.NULL;

@EverythingIsNonnullByDefault
public abstract class TablePowerSystemResources extends TableIdentifiedObjects {

    public final Column LOCATION_MRID = new Column(++columnIndex, "location_mrid", "TEXT", NULL);
    public final Column NUM_CONTROLS = new Column(++columnIndex, "num_controls", "INTEGER", NOT_NULL);

}
