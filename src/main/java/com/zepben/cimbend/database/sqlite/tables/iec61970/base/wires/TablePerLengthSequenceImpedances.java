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

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

/**
 * Represents the per length sequence impedances table
 */
@EverythingIsNonnullByDefault
public class TablePerLengthSequenceImpedances extends TablePerLengthImpedances {

    public final Column R = new Column(++columnIndex, "r", "NUMBER", NOT_NULL);
    public final Column X = new Column(++columnIndex, "x", "NUMBER", NOT_NULL);
    public final Column R0 = new Column(++columnIndex, "r0", "NUMBER", NOT_NULL);
    public final Column X0 = new Column(++columnIndex, "x0", "NUMBER", NOT_NULL);
    public final Column BCH = new Column(++columnIndex, "bch", "NUMBER", NOT_NULL);
    public final Column GCH = new Column(++columnIndex, "gch", "NUMBER", NOT_NULL);
    public final Column B0CH = new Column(++columnIndex, "b0ch", "NUMBER", NOT_NULL);
    public final Column G0CH = new Column(++columnIndex, "g0ch", "NUMBER", NOT_NULL);

    @Override
    public String name() {
        return "per_length_sequence_impedances";
    }

    @Override
    protected Class<?> getTableClass() {
        return TablePerLengthSequenceImpedances.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
