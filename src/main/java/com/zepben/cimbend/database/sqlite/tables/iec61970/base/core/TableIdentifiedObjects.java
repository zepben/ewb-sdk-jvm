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
