/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61968.common;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;
import com.zepben.cimbend.database.sqlite.tables.SqliteTable;

import java.util.Arrays;
import java.util.List;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

@EverythingIsNonnullByDefault
public class TablePositionPoints extends SqliteTable {

    public final Column LOCATION_MRID = new Column(++columnIndex, "location_mrid", "TEXT", NOT_NULL);
    public final Column SEQUENCE_NUMBER = new Column(++columnIndex, "sequence_number", "INTEGER", NOT_NULL);
    public final Column X_POSITION = new Column(++columnIndex, "x_position", "NUMBER", NOT_NULL);
    public final Column Y_POSITION = new Column(++columnIndex, "y_position", "NUMBER", NOT_NULL);

    @Override
    public String name() {
        return "position_points";
    }

    @Override
    public List<List<Column>> uniqueIndexColumns() {
        List<List<Column>> cols = super.uniqueIndexColumns();
        cols.add(Arrays.asList(LOCATION_MRID, SEQUENCE_NUMBER));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TablePositionPoints.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
