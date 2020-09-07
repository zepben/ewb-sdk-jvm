/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
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
