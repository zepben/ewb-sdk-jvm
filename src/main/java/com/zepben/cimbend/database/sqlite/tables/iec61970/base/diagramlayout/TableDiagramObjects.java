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
