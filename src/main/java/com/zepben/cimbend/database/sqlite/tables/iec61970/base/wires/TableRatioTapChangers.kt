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

import java.util.Collections;
import java.util.List;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;
import static com.zepben.cimbend.database.Column.Nullable.NULL;

/**
 * Represents the ratio tap changers table.
 */
@EverythingIsNonnullByDefault
public class TableRatioTapChangers extends TableTapChangers {

    public final Column TRANSFORMER_END_MRID = new Column(++columnIndex, "transformer_end_mrid", "TEXT", NULL);
    public final Column STEP_VOLTAGE_INCREMENT = new Column(++columnIndex, "step_voltage_increment", "NUMBER", NOT_NULL);

    @Override
    public String name() {
        return "ratio_tap_changers";
    }

    @Override
    protected Class<?> getTableClass() {
        return TableRatioTapChangers.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

    @Override
    public List<List<Column>> uniqueIndexColumns() {
        List<List<Column>> cols = super.uniqueIndexColumns();
        cols.add(Collections.singletonList(TRANSFORMER_END_MRID));
        return cols;
    }

}
