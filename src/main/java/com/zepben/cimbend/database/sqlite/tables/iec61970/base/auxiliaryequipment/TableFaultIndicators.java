/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.auxiliaryequipment;

import com.zepben.annotations.EverythingIsNonnullByDefault;

/**
 * Represents the fault indicators table.
 */
@EverythingIsNonnullByDefault
public class TableFaultIndicators extends TableAuxiliaryEquipment {

    @Override
    public String name() {
        return "fault_indicators";
    }

    @Override
    protected Class<?> getTableClass() {
        return TableFaultIndicators.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
