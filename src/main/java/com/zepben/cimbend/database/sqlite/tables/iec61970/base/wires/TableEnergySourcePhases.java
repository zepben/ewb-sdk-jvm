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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

/**
 * Represents the sources table
 */
@EverythingIsNonnullByDefault
public class TableEnergySourcePhases extends TablePowerSystemResources {

    public final Column ENERGY_SOURCE_MRID = new Column(++columnIndex, "energy_source_mrid", "TEXT", NOT_NULL);
    public final Column PHASE = new Column(++columnIndex, "phase", "TEXT", NOT_NULL);

    @Override
    public String name() {
        return "energy_source_phases";
    }

    @Override
    public List<List<Column>> uniqueIndexColumns() {
        List<List<Column>> cols = super.uniqueIndexColumns();
        cols.add(Arrays.asList(ENERGY_SOURCE_MRID, PHASE));
        return cols;
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(ENERGY_SOURCE_MRID));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TableEnergySourcePhases.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
