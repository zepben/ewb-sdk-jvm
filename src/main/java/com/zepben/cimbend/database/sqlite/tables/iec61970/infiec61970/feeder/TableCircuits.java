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
package com.zepben.cimbend.database.sqlite.tables.iec61970.infiec61970.feeder;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.wires.TableLines;

import java.util.Collections;
import java.util.List;

import static com.zepben.cimbend.database.Column.Nullable.NULL;

/**
 * Represents the circuits table.
 */
@EverythingIsNonnullByDefault
public class TableCircuits extends TableLines {

    public final Column LOOP_MRID = new Column(++columnIndex, "loop_mrid", "TEXT", NULL);

    @Override
    public String name() {
        return "circuits";
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(LOOP_MRID));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TableCircuits.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
