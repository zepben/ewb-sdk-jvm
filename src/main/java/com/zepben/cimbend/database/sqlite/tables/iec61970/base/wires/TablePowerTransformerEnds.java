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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

/**
 * Represents the power transformer ends table.
 */
@EverythingIsNonnullByDefault
public class TablePowerTransformerEnds extends TableTransformerEnds {

    public final Column POWER_TRANSFORMER_MRID = new Column(++columnIndex, "power_transformer_mrid", "TEXT", NOT_NULL);
    public final Column CONNECTION_KIND = new Column(++columnIndex, "connection_kind", "TEXT", NOT_NULL);
    public final Column PHASE_ANGLE_CLOCK = new Column(++columnIndex, "phase_angle_clock", "INTEGER", NOT_NULL);
    public final Column B = new Column(++columnIndex, "b", "NUMBER", NOT_NULL);
    public final Column B0 = new Column(++columnIndex, "b0", "NUMBER", NOT_NULL);
    public final Column G = new Column(++columnIndex, "g", "NUMBER", NOT_NULL);
    public final Column G0 = new Column(++columnIndex, "g0", "NUMBER", NOT_NULL);
    public final Column R = new Column(++columnIndex, "R", "NUMBER", NOT_NULL);
    public final Column R0 = new Column(++columnIndex, "R0", "NUMBER", NOT_NULL);
    public final Column RATED_S = new Column(++columnIndex, "rated_s", "INTEGER", NOT_NULL);
    public final Column RATED_U = new Column(++columnIndex, "rated_u", "INTEGER", NOT_NULL);
    public final Column X = new Column(++columnIndex, "X", "NUMBER", NOT_NULL);
    public final Column X0 = new Column(++columnIndex, "X0", "NUMBER", NOT_NULL);

    @Override
    public String name() {
        return "power_transformer_ends";
    }

    @Override
    public List<List<Column>> uniqueIndexColumns() {
        List<List<Column>> cols = super.uniqueIndexColumns();
        cols.add(Arrays.asList(POWER_TRANSFORMER_MRID, END_NUMBER));
        return cols;
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(POWER_TRANSFORMER_MRID));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TablePowerTransformerEnds.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
