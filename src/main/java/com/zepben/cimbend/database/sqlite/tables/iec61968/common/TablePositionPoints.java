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
