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
 * Represents the circuit to substation associations table.
 */
@EverythingIsNonnullByDefault
public class TableCircuitsSubstations extends SqliteTable {

    public final Column CIRCUIT_MRID = new Column(++columnIndex, "circuit_mrid", "TEXT", NOT_NULL);
    public final Column SUBSTATION_MRID = new Column(++columnIndex, "substation_mrid", "TEXT", NOT_NULL);

    @Override
    public String name() {
        return "circuits_substations";
    }

    @Override
    public List<List<Column>> uniqueIndexColumns() {
        List<List<Column>> cols = super.uniqueIndexColumns();
        cols.add(Arrays.asList(CIRCUIT_MRID, SUBSTATION_MRID));
        return cols;
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(CIRCUIT_MRID));
        cols.add(Collections.singletonList(SUBSTATION_MRID));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TableCircuitsSubstations.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
