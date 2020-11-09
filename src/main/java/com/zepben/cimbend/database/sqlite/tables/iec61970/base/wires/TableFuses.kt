/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.wires;

import com.zepben.annotations.EverythingIsNonnullByDefault;

/**
 * Represents the fuses table.
 */
@EverythingIsNonnullByDefault
public class TableFuses extends TableSwitches {

    @Override
    public String name() {
        return "fuses";
    }

    @Override
    protected Class<?> getTableClass() {
        return TableFuses.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
