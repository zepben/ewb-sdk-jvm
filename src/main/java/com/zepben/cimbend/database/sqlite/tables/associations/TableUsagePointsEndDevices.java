/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.associations;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;
import com.zepben.cimbend.database.sqlite.tables.SqliteTable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

@EverythingIsNonnullByDefault
public class TableUsagePointsEndDevices extends SqliteTable {

    public final Column USAGE_POINT_MRID = new Column(++columnIndex, "usage_point_mrid", "TEXT", NOT_NULL);
    public final Column END_DEVICE_MRID = new Column(++columnIndex, "end_device_mrid", "TEXT", NOT_NULL);

    @Override
    public String name() {
        return "usage_points_end_devices";
    }

    @Override
    public List<List<Column>> uniqueIndexColumns() {
        List<List<Column>> cols = super.uniqueIndexColumns();
        cols.add(Arrays.asList(USAGE_POINT_MRID, END_DEVICE_MRID));
        return cols;
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(USAGE_POINT_MRID));
        cols.add(Collections.singletonList(END_DEVICE_MRID));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TableUsagePointsEndDevices.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
