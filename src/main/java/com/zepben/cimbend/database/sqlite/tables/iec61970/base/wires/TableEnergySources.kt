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
 * Represents the energy sources table.
 */
@EverythingIsNonnullByDefault
public class TableEnergySources extends TableEnergyConnections {

    public final Column ACTIVE_POWER = new Column(++columnIndex, "active_power", "NUMBER", NOT_NULL);
    public final Column REACTIVE_POWER = new Column(++columnIndex, "reactive_power", "NUMBER", NOT_NULL);
    public final Column VOLTAGE_ANGLE = new Column(++columnIndex, "voltage_angle", "NUMBER", NOT_NULL);
    public final Column VOLTAGE_MAGNITUDE = new Column(++columnIndex, "voltage_magnitude", "NUMBER", NOT_NULL);
    public final Column P_MAX = new Column(++columnIndex, "p_max", "NUMBER", NOT_NULL);
    public final Column P_MIN = new Column(++columnIndex, "p_min", "NUMBER", NOT_NULL);
    public final Column R = new Column(++columnIndex, "r", "NUMBER", NOT_NULL);
    public final Column R0 = new Column(++columnIndex, "r0", "NUMBER", NOT_NULL);
    public final Column RN = new Column(++columnIndex, "rn", "NUMBER", NOT_NULL);
    public final Column X = new Column(++columnIndex, "x", "NUMBER", NOT_NULL);
    public final Column X0 = new Column(++columnIndex, "x0", "NUMBER", NOT_NULL);
    public final Column XN = new Column(++columnIndex, "xn", "NUMBER", NOT_NULL);

    @Override
    public String name() {
        return "energy_sources";
    }

    @Override
    protected Class<?> getTableClass() {
        return TableEnergySources.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
