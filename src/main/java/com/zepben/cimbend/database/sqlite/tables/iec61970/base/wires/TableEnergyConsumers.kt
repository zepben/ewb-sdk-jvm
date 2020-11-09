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
 * Represents the energy consumers table.
 */
@EverythingIsNonnullByDefault
public class TableEnergyConsumers extends TableEnergyConnections {

    public final Column CUSTOMER_COUNT = new Column(++columnIndex, "customer_count", "INTEGER", NOT_NULL);
    public final Column GROUNDED = new Column(++columnIndex, "grounded", "BOOLEAN", NOT_NULL);
    public final Column P = new Column(++columnIndex, "p", "NUMBER", NOT_NULL);
    public final Column Q = new Column(++columnIndex, "q", "NUMBER", NOT_NULL);
    public final Column P_FIXED = new Column(++columnIndex, "p_fixed", "NUMBER", NOT_NULL);
    public final Column Q_FIXED = new Column(++columnIndex, "q_fixed", "NUMBER", NOT_NULL);
    public final Column PHASE_CONNECTION = new Column(++columnIndex, "phase_connection", "TEXT", NOT_NULL);

    @Override
    public String name() {
        return "energy_consumers";
    }

    @Override
    protected Class<?> getTableClass() {
        return TableEnergyConsumers.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
