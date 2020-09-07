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

/**
 * Represents the loop to substation associations table.
 */
@EverythingIsNonnullByDefault
public class TableLoopsSubstations extends SqliteTable {

    public final Column LOOP_MRID = new Column(++columnIndex, "loop_mrid", "TEXT", NOT_NULL);
    public final Column SUBSTATION_MRID = new Column(++columnIndex, "substation_mrid", "TEXT", NOT_NULL);
    public final Column RELATIONSHIP = new Column(++columnIndex, "relationship", "TEXT", NOT_NULL);

    @Override
    public String name() {
        return "loops_substations";
    }

    @Override
    public List<List<Column>> uniqueIndexColumns() {
        List<List<Column>> cols = super.uniqueIndexColumns();
        cols.add(Arrays.asList(LOOP_MRID, SUBSTATION_MRID));
        return cols;
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(LOOP_MRID));
        cols.add(Collections.singletonList(SUBSTATION_MRID));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TableLoopsSubstations.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
