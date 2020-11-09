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
