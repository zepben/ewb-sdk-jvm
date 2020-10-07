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

import static com.zepben.cimbend.database.Column.Nullable.NULL;

/**
 * Created by robertocomp on 7/02/2017.
 * SQL Table that holds the information contained in conducting equipment
 */
@EverythingIsNonnullByDefault
public abstract class TableConductingEquipment extends TableEquipment {

    public final Column BASE_VOLTAGE_MRID = new Column(++columnIndex, "base_voltage_mrid", "NUMBER", NULL);

}
