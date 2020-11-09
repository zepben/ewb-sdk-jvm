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
