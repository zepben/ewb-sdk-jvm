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

import static com.zepben.cimbend.database.Column.Nullable.NULL;

/**
 * Represents the AC line segments table
 */
@EverythingIsNonnullByDefault
public class TableAcLineSegments extends TableConductors {

    public final Column PER_LENGTH_SEQUENCE_IMPEDANCE_MRID = new Column(++columnIndex, "per_length_sequence_impedance_mrid", "TEXT", NULL);

    @Override
    public String name() {
        return "ac_line_segments";
    }

    @Override
    protected Class<?> getTableClass() {
        return TableAcLineSegments.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
