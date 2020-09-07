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
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.wires;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;

import static com.zepben.cimbend.database.Column.Nullable.NULL;

/**
 * Represents the AC line segments table
 */
@EverythingIsNonnullByDefault
public class TableAcLineSegments extends TableConductors {

    public final Column PER_LENGTH_SEQUENCE_IMPEDANCE_MRID = new Column(++columnIndex, "per_length_sequence_impedance_mrid", "TEXT", NULL);

    @Override
    public String name() {
        return "ac_line_segments";
    }

    @Override
    protected Class<?> getTableClass() {
        return TableAcLineSegments.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
