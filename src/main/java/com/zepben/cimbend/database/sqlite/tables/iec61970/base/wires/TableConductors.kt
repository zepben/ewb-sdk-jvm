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

import static com.zepben.cimbend.database.Column.Nullable.NULL;

/**
 * Represents the conductors table
 */
@EverythingIsNonnullByDefault
public abstract class TableConductors extends TableConductingEquipment {

    public final Column LENGTH = new Column(++columnIndex, "length", "NUMBER");
    public final Column WIRE_INFO_MRID = new Column(++columnIndex, "wire_info_mrid", "TEXT", NULL);

}
