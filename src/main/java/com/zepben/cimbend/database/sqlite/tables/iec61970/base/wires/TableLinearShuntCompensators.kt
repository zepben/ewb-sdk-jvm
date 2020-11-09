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
