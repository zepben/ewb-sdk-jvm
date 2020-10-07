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
