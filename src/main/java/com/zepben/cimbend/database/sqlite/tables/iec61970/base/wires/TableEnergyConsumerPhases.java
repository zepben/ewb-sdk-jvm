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
