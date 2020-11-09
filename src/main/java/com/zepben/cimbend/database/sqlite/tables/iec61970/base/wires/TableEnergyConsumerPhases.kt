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
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.TablePowerSystemResources;

import java.util.Collections;
import java.util.List;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

/**
 * Represents the energy consumers table.
 */
@EverythingIsNonnullByDefault
public class TableEnergyConsumerPhases extends TablePowerSystemResources {

    public final Column ENERGY_CONSUMER_MRID = new Column(++columnIndex, "ENERGY_CONSUMER_MRID", "TEXT", NOT_NULL);
    public final Column PHASE = new Column(++columnIndex, "phase", "TEXT", NOT_NULL);
    public final Column P = new Column(++columnIndex, "p", "NUMBER", NOT_NULL);
    public final Column Q = new Column(++columnIndex, "q", "NUMBER", NOT_NULL);
    public final Column P_FIXED = new Column(++columnIndex, "p_fixed", "NUMBER", NOT_NULL);
    public final Column Q_FIXED = new Column(++columnIndex, "q_fixed", "NUMBER", NOT_NULL);

    @Override
    public String name() {
        return "energy_consumer_phases";
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(ENERGY_CONSUMER_MRID));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TableEnergyConsumerPhases.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
