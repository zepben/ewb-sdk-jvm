/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.cim.iec61970.base.wires.RegulatingControl
import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TablePowerSystemResources
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `RegulatingControl` columns required for the database table.
 *
 * @property DISCRETE The regulation is performed in a discrete mode. This applies to equipment with discrete controls, e.g. tap changers and shunt compensators.
 * @property MODE The regulating control mode presently available. This specification allows for determining the kind of regulation without need for obtaining
 * the units from a schedule.
 * @property MONITORED_PHASE Phase voltage controlling this regulator, measured at regulator location.
 * @property TARGET_DEADBAND This is a deadband used with discrete control to avoid excessive update of controls like tap changers and shunt compensator banks
 * while regulating. The units are the base units appropriate for the mode. The attribute shall be a positive value or zero. If [RegulatingControl.discrete] is
 * set to "false", the RegulatingControl.targetDeadband is to be ignored. Note that for instance, if the targetValue is 100 kV and the targetDeadband is 2 kV
 * the range is from 99 to 101 kV.
 * @property TARGET_VALUE The target value specified for case input. This value can be used for the target value without the use of schedules. The value has the
 * units appropriate to the mode attribute.
 * @property ENABLED The flag tells if regulation is enabled.
 * @property MAX_ALLOWED_TARGET_VALUE Maximum allowed target value (RegulatingControl.targetValue).
 * @property MIN_ALLOWED_TARGET_VALUE Minimum allowed target value (RegulatingControl.targetValue).
 * @property RATED_CURRENT The rated current of associated CT in amps for this RegulatingControl. Forms the base used to convert Line Drop Compensation
 * settings from ohms to voltage.
 * @property TERMINAL_MRID The terminal associated with this regulating control. The terminal is associated instead of a node, since the terminal could connect into
 * either a topological node or a connectivity node. Sometimes it is useful to model regulation at a terminal of a bus bar object.
 * @property CT_PRIMARY Current rating of the CT, expressed in terms of the current (in Amperes) that flows in the Primary where the 'Primary' is the conductor
 * being monitored. It ensures proper operation of the regulating equipment by providing the necessary current references for control actions. An important side
 * effect of this current value is that it also defines the current value at which the full LDC R and X voltages are applied by the controller, where enabled.
 * @property MIN_TARGET_DEADBAND This is the minimum allowable range for discrete control in regulating devices, used to prevent frequent control actions and
 * promote operational stability. This attribute sets a baseline range within which no adjustments are made, applicable across various devices like voltage
 * regulators, shunt compensators, or battery units.
 */
@Suppress("PropertyName")
abstract class TableRegulatingControls : TablePowerSystemResources() {

    val DISCRETE: Column = Column(++columnIndex, "discrete", Column.Type.BOOLEAN, NULL)
    val MODE: Column = Column(++columnIndex, "mode", Column.Type.STRING, NOT_NULL)
    val MONITORED_PHASE: Column = Column(++columnIndex, "monitored_phase", Column.Type.STRING, NOT_NULL)
    val TARGET_DEADBAND: Column = Column(++columnIndex, "target_deadband", Column.Type.DOUBLE, NULL)
    val TARGET_VALUE: Column = Column(++columnIndex, "target_value", Column.Type.DOUBLE, NULL)
    val ENABLED: Column = Column(++columnIndex, "enabled", Column.Type.BOOLEAN, NULL)
    val MAX_ALLOWED_TARGET_VALUE: Column = Column(++columnIndex, "max_allowed_target_value", Column.Type.DOUBLE, NULL)
    val MIN_ALLOWED_TARGET_VALUE: Column = Column(++columnIndex, "min_allowed_target_value", Column.Type.DOUBLE, NULL)
    val RATED_CURRENT: Column = Column(++columnIndex, "rated_current", Column.Type.DOUBLE, NULL)
    val TERMINAL_MRID: Column = Column(++columnIndex, "terminal_mrid", Column.Type.STRING, NULL)
    val CT_PRIMARY: Column = Column(++columnIndex, "ct_primary", Column.Type.DOUBLE, NULL)
    val MIN_TARGET_DEADBAND: Column = Column(++columnIndex, "min_target_deadband", Column.Type.DOUBLE, NULL)

}
