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

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

/**
 * Represents the conductor graphics table
 */
@EverythingIsNonnullByDefault
public class TableDiagrams extends TableIdentifiedObjects {

    public final Column DIAGRAM_STYLE = new Column(++columnIndex, "diagram_style", "TEXT", NOT_NULL);
    public final Column ORIENTATION_KIND = new Column(++columnIndex, "orientation_kind", "TEXT", NOT_NULL);

    @Override
    public String name() {
        return "diagrams";
    }

    @Override
    protected Class<?> getTableClass() {
        return TableDiagrams.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
