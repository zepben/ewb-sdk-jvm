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
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.TablePowerSystemResources;

import static com.zepben.cimbend.database.Column.Nullable.NOT_NULL;

/**
 * Represents the reclosers table.
 */
@EverythingIsNonnullByDefault
public abstract class TableTapChangers extends TablePowerSystemResources {

    public final Column CONTROL_ENABLED = new Column(++columnIndex, "control_enabled", "BOOLEAN", NOT_NULL);
    public final Column HIGH_STEP = new Column(++columnIndex, "high_step", "INTEGER", NOT_NULL);
    public final Column LOW_STEP = new Column(++columnIndex, "low_step", "INTEGER", NOT_NULL);
    public final Column NEUTRAL_STEP = new Column(++columnIndex, "neutral_step", "INTEGER", NOT_NULL);
    public final Column NEUTRAL_U = new Column(++columnIndex, "neutral_u", "INTEGER", NOT_NULL);
    public final Column NORMAL_STEP = new Column(++columnIndex, "normal_step", "INTEGER", NOT_NULL);
    public final Column STEP = new Column(++columnIndex, "step", "NUMBER", NOT_NULL);

}
