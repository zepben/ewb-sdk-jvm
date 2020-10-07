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
