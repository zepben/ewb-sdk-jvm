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
 * An abstract class for RotatingMachine Tables.
 *
 * @property RATED_POWER_FACTOR Power factor (nameplate data). It is primarily used for short circuit data exchange according to IEC 60909. The attribute cannot be a negative value.
 * @property RATED_S Nameplate apparent power rating for the unit in volt-amperes (VA). The attribute shall have a positive value.
 * @property RATED_U Rated voltage in volts (nameplate data, Ur in IEC 60909-0). It is primarily used for short circuit data exchange according to IEC 60909. The attribute shall be a positive value.
 * @property P Active power injection in watts. Load sign convention is used, i.e. positive sign means flow out from a node. Starting value for a steady state solution.
 * @property Q Reactive power injection in VAr. Load sign convention is used, i.e. positive sign means flow out from a node. Starting value for a steady state solution.
 */

@Suppress("PropertyName")
abstract class TableRotatingMachines : TableRegulatingCondEq() {

    val RATED_POWER_FACTOR: Column = Column(++columnIndex, "rated_power_factor", "NUMBER", NULL)
    val RATED_S: Column = Column(++columnIndex, "rated_s", "INTEGER", NULL)
    val RATED_U: Column = Column(++columnIndex, "rated_u", "INTEGER", NULL)
    val P: Column = Column(++columnIndex, "p", "NUMBER", NULL)
    val Q: Column = Column(++columnIndex, "q", "NUMBER", NULL)

}
