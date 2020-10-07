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
