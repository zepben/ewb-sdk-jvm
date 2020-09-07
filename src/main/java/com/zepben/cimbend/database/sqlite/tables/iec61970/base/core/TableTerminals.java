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
