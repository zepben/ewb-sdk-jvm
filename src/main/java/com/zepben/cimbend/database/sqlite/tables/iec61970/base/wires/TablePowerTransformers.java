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
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.TableConductingEquipment;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

/**
 * Represents the power transformers table.
 */
@EverythingIsNonnullByDefault
public class TablePowerTransformers extends TableConductingEquipment {

    public final Column VECTOR_GROUP = new Column(++columnIndex, "vector_group", "TEXT", NOT_NULL);

    @Override
    public String name() {
        return "power_transformers";
    }

    @Override
    protected Class<?> getTableClass() {
        return TablePowerTransformers.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
