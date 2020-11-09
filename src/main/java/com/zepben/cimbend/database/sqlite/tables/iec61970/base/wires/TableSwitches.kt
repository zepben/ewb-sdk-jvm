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
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.TableConductingEquipment;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

/**
 * Represents the switches table.
 */
@EverythingIsNonnullByDefault
public abstract class TableSwitches extends TableConductingEquipment {

    public final Column NORMAL_OPEN = new Column(++columnIndex, "normal_open", "INTEGER", NOT_NULL);
    public final Column OPEN = new Column(++columnIndex, "open", "INTEGER", NOT_NULL);

}
