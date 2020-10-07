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
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects;

import java.util.Collections;
import java.util.List;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;
import static com.zepben.cimbend.database.Column.Nullable.NULL;

/**
 * Represents the conductor graphics table
 */
@EverythingIsNonnullByDefault
public class TableDiagramObjects extends TableIdentifiedObjects {

    public final Column IDENTIFIED_OBJECT_MRID = new Column(++columnIndex, "identified_object_mrid", "TEXT", NULL);
    public final Column DIAGRAM_MRID = new Column(++columnIndex, "diagram_mrid", "TEXT", NULL);
    public final Column STYLE = new Column(++columnIndex, "style", "TEXT", NOT_NULL);
    public final Column ROTATION = new Column(++columnIndex, "rotation", "NUMBER", NOT_NULL);

    @Override
    public String name() {
        return "diagram_objects";
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(IDENTIFIED_OBJECT_MRID));
        cols.add(Collections.singletonList(DIAGRAM_MRID));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TableDiagramObjects.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
