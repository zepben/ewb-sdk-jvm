/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
