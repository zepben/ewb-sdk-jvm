/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.diagramlayout;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;
import com.zepben.cimbend.database.sqlite.tables.SqliteTable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;
import static com.zepben.cimbend.database.Column.Nullable.NULL;

/**
 * Represents the diagram object points table
 */
@EverythingIsNonnullByDefault
public class TableDiagramObjectPoints extends SqliteTable {

    public final Column DIAGRAM_OBJECT_MRID = new Column(++columnIndex, "diagram_object_mrid", "TEXT", NOT_NULL);
    public final Column SEQUENCE_NUMBER = new Column(++columnIndex, "sequence_number", "TEXT", NOT_NULL);
    public final Column X_POSITION = new Column(++columnIndex, "x_position", "TEXT", NULL);
    public final Column Y_POSITION = new Column(++columnIndex, "y_position", "TEXT", NULL);

    @Override
    public String name() {
        return "diagram_object_points";
    }

    @Override
    public List<List<Column>> uniqueIndexColumns() {
        List<List<Column>> cols = super.uniqueIndexColumns();
        cols.add(Arrays.asList(DIAGRAM_OBJECT_MRID, SEQUENCE_NUMBER));
        return cols;
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(DIAGRAM_OBJECT_MRID));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TableDiagramObjectPoints.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
