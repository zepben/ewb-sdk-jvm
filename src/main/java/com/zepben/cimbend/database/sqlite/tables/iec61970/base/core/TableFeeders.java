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
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.core;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;

import java.util.Collections;
import java.util.List;

import static com.zepben.cimbend.database.Column.Nullable.NULL;

@EverythingIsNonnullByDefault
public class TableFeeders extends TableEquipmentContainers {

    public final Column NORMAL_HEAD_TERMINAL_MRID = new Column(++columnIndex, "normal_head_terminal_mrid", "TEXT", NULL);
    public final Column NORMAL_ENERGIZING_SUBSTATION_MRID = new Column(++columnIndex, "normal_energizing_substation_mrid", "TEXT", NULL);

    @Override
    public String name() {
        return "feeders";
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(NORMAL_ENERGIZING_SUBSTATION_MRID));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TableFeeders.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
