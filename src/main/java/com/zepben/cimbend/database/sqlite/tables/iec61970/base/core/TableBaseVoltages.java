/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.core;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

@EverythingIsNonnullByDefault
public class TableBaseVoltages extends TableIdentifiedObjects {

    public final Column NOMINAL_VOLTAGE = new Column(++columnIndex, "base_voltage", "INTEGER", NOT_NULL);

    @Override
    public String name() {
        return "base_voltages";
    }

    @Override
    protected Class<?> getTableClass() {
        return TableBaseVoltages.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
