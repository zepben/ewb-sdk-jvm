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
