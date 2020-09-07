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
