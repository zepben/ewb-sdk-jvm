/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.cim.tables.Column
import com.zepben.evolve.database.sqlite.cim.tables.Column.Nullable.NULL

/**
 * A class representing the PetersenCoil columns required for the database table.
 *
 * @property X_GROUND_NOMINAL A column storing the nominal reactance in ohms. This is the operating point (normally over compensation) that is defined based
 * on the resonance point in the healthy network condition. The impedance is calculated based on nominal voltage divided by position current.
 */
@Suppress("PropertyName")
class TablePetersenCoils : TableEarthFaultCompensators() {

    val X_GROUND_NOMINAL: Column = Column(++columnIndex, "x_ground_nominal", "NUMBER", NULL)

    override val name: String = "petersen_coils"

}
