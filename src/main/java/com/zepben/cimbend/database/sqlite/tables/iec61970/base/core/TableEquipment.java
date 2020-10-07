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

@EverythingIsNonnullByDefault
public abstract class TableEquipment extends TablePowerSystemResources {

    public final Column NORMALLY_IN_SERVICE = new Column(++columnIndex, "normally_in_service", "BOOLEAN");
    public final Column IN_SERVICE = new Column(++columnIndex, "in_service", "BOOLEAN");

}
