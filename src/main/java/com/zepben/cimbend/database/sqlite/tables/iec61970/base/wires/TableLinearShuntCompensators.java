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
 * Represents the linear shunt compensators table.
 */
@EverythingIsNonnullByDefault
public class TableLinearShuntCompensators extends TableShuntCompensators {

    public final Column B0_PER_SECTION = new Column(++columnIndex, "b0_per_section", "NUMBER", NOT_NULL);
    public final Column B_PER_SECTION = new Column(++columnIndex, "b_per_section", "NUMBER", NOT_NULL);
    public final Column G0_PER_SECTION = new Column(++columnIndex, "g0_per_section", "NUMBER", NOT_NULL);
    public final Column G_PER_SECTION = new Column(++columnIndex, "g_per_section", "NUMBER", NOT_NULL);

    @Override
    public String name() {
        return "linear_shunt_compensators";
    }

    @Override
    protected Class<?> getTableClass() {
        return TableLinearShuntCompensators.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
