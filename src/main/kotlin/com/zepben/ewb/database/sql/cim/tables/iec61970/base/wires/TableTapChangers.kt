/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TablePowerSystemResources
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `TapChanger` columns required for the database table.
 *
 * @property CONTROL_ENABLED Specifies the regulation status of the equipment.  True is regulating, false is not regulating.
 * @property TAP_CHANGER_CONTROL_MRID The regulating control scheme in which this tap changer participates.
 * @property HIGH_STEP Highest possible tap step position, advance from neutral. The attribute shall be greater than lowStep. This tap position results in the
 *                    maximum voltage boost on secondary winding(s).
 * @property LOW_STEP Lowest possible tap step position, retard from neutral. This tap position results in the maximum voltage buck on secondary winding(s).
 * @property NEUTRAL_STEP The neutral tap step position for this winding.
 *                       The attribute shall be equal or greater than lowStep and equal or less than highStep.
 * @property NEUTRAL_U Voltage at which the winding operates at the neutral tap setting.
 * @property NORMAL_STEP The tap step position used in "normal" network operation for this winding. For a "Fixed" tap changer indicates the current physical tap setting.
 *                      The attribute shall be equal or greater than lowStep and equal or less than highStep.
 * @property STEP Tap changer position.
 *                Starting step for a steady state solution. Non integer values are allowed to support continuous tap variables.
 *                The reasons for continuous value are to support study cases where no discrete tap changers has yet been designed,
 *                a solutions where a narrow voltage band force the tap step to oscillate or accommodate for a continuous solution as input.
 *                The attribute shall be equal or greater than lowStep and equal or less than highStep.
 */
@Suppress("PropertyName")
abstract class TableTapChangers : TablePowerSystemResources() {

    val CONTROL_ENABLED: Column = Column(++columnIndex, "control_enabled", Column.Type.BOOLEAN, NULL)
    val HIGH_STEP: Column = Column(++columnIndex, "high_step", Column.Type.INTEGER, NULL)
    val LOW_STEP: Column = Column(++columnIndex, "low_step", Column.Type.INTEGER, NULL)
    val NEUTRAL_STEP: Column = Column(++columnIndex, "neutral_step", Column.Type.INTEGER, NULL)
    val NEUTRAL_U: Column = Column(++columnIndex, "neutral_u", Column.Type.INTEGER, NULL)
    val NORMAL_STEP: Column = Column(++columnIndex, "normal_step", Column.Type.INTEGER, NULL)
    val STEP: Column = Column(++columnIndex, "step", Column.Type.DOUBLE, NULL)
    val TAP_CHANGER_CONTROL_MRID: Column = Column(++columnIndex, "tap_changer_control_mrid", Column.Type.STRING, NULL)

}
