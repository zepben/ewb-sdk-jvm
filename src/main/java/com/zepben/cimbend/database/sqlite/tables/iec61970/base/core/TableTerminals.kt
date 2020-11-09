/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.core;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;
import static com.zepben.cimbend.database.Column.Nullable.NULL;

/**
 * Represents the terminals table
 */
@EverythingIsNonnullByDefault
public class TableTerminals extends TableAcDcTerminals {

    public final Column CONDUCTING_EQUIPMENT_MRID = new Column(++columnIndex, "conducting_equipment_mrid", "TEXT", NULL);
    public final Column SEQUENCE_NUMBER = new Column(++columnIndex, "sequence_number", "INTEGER", NOT_NULL);
    public final Column CONNECTIVITY_NODE_MRID = new Column(++columnIndex, "connectivity_node_mrid", "TEXT", NULL);
    public final Column PHASES = new Column(++columnIndex, "phases", "TEXT", NOT_NULL);

    @Override
    public String name() {
        return "terminals";
    }

    @Override
    public List<List<Column>> uniqueIndexColumns() {
        List<List<Column>> cols = super.uniqueIndexColumns();
        cols.add(Arrays.asList(CONDUCTING_EQUIPMENT_MRID, SEQUENCE_NUMBER));
        return cols;
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(CONNECTIVITY_NODE_MRID));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TableTerminals.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
