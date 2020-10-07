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
 * Represents the circuit to terminal associations table.
 */
@EverythingIsNonnullByDefault
public class TableCircuitsTerminals extends SqliteTable {

    public final Column CIRCUIT_MRID = new Column(++columnIndex, "circuit_mrid", "TEXT", NOT_NULL);
    public final Column TERMINAL_MRID = new Column(++columnIndex, "terminal_mrid", "TEXT", NOT_NULL);

    @Override
    public String name() {
        return "circuits_terminals";
    }

    @Override
    public List<List<Column>> uniqueIndexColumns() {
        List<List<Column>> cols = super.uniqueIndexColumns();
        cols.add(Arrays.asList(CIRCUIT_MRID, TERMINAL_MRID));
        return cols;
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(CIRCUIT_MRID));
        cols.add(Collections.singletonList(TERMINAL_MRID));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TableCircuitsTerminals.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
