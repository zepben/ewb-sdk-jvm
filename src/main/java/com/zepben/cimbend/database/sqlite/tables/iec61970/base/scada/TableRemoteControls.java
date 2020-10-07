/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.scada;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.database.Column;

import static com.zepben.cimbend.database.Column.Nullable.NULL;

@EverythingIsNonnullByDefault
public class TableRemoteControls extends TableRemotePoints {

    public final Column CONTROL_MRID = new Column(++columnIndex, "power_system_resource_mrid", "TEXT", NULL);

    @Override
    public String name() {
        return "remote_controls";
    }

    @Override
    protected Class<?> getTableClass() {
        return TableRemoteControls.class;
    }

    @Override
    protected Object getTableClassInstance() {
        return this;
    }

}
