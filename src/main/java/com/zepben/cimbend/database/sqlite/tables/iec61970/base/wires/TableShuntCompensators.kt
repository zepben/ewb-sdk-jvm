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
 * Represents the shunt compensators table.
 */
@EverythingIsNonnullByDefault
public abstract class TableShuntCompensators extends TableRegulatingCondEq {

    public final Column GROUNDED = new Column(++columnIndex, "grounded", "BOOLEAN", NOT_NULL);
    public final Column NOM_U = new Column(++columnIndex, "nom_u", "INTEGER", NOT_NULL);
    public final Column PHASE_CONNECTION = new Column(++columnIndex, "phase_connection", "TEXT", NOT_NULL);
    public final Column SECTIONS = new Column(++columnIndex, "sections", "NUMBER", NOT_NULL);

}
