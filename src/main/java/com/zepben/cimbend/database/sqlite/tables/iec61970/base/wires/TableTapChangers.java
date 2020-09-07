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
