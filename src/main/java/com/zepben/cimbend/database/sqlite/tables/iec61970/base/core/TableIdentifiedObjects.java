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
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.core;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;
import com.zepben.cimbend.database.sqlite.tables.SqliteTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

/**
 * Represents the identified objects table
 */
@EverythingIsNonnullByDefault
public abstract class TableIdentifiedObjects extends SqliteTable {

    public final Column MRID = new Column(++columnIndex, "mrid", "TEXT", NOT_NULL);
    public final Column NAME = new Column(++columnIndex, "name", "TEXT", NOT_NULL);
    public final Column DESCRIPTION = new Column(++columnIndex, "description", "TEXT", NOT_NULL);
    public final Column NUM_DIAGRAM_OBJECTS = new Column(++columnIndex, "num_diagram_objects", "INTEGER", NOT_NULL);

    @Override
    public List<List<Column>> uniqueIndexColumns() {
        List<List<Column>> cols = new ArrayList<>();
        cols.add(Collections.singletonList(MRID));
        return cols;
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(NAME));
        return cols;
    }

}
