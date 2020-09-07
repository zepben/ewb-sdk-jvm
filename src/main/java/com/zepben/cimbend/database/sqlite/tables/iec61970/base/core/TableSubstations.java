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
public class TableSubstations extends TableEquipmentContainers {

    public final Column SUB_GEOGRAPHICAL_REGION_MRID = new Column(++columnIndex, "sub_geographical_region_mrid", "TEXT", NULL);

    @Override
    public String name() {
        return "substations";
    }

    @Override
    public List<List<Column>> nonUniqueIndexColumns() {
        List<List<Column>> cols = super.nonUniqueIndexColumns();
        cols.add(Collections.singletonList(SUB_GEOGRAPHICAL_REGION_MRID));
        return cols;
    }

    @Override
    protected Class<?> getTableClass() {
        return TableSubstations.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
