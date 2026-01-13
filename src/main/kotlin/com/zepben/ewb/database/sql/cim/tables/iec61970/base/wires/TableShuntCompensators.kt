/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.wires

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `ShuntCompensator` columns required for the database table.
 *
 * @property SHUNT_COMPENSATOR_INFO_MRID The asset information for this ShuntCompensator.
 * @property GROUNDED Used for Yn and Zn connections. True if the neutral is solidly grounded.
 * @property NOM_U The voltage at which the nominal reactive power may be calculated. This should normally be within 10% of the voltage at which the capacitor is connected to the network.
 * @property PHASE_CONNECTION The type of phase connection, such as wye or delta.
 * @property SECTIONS Shunt compensator sections in use.
 * Starting value for steady state solution. Non integer values are allowed to support continuous variables.
 * The reasons for continuous value are to support study cases where no discrete shunt compensator's has yet been
 * designed, a solutions where a narrow voltage band force the sections to oscillate or accommodate for a continuous
 * solution as input.
 *
 * For LinearShuntCompensator the value shall be between zero and ShuntCompensator.maximumSections. At value zero the
 * shunt compensator conductance and admittance is zero. Linear interpolation of conductance and admittance between the
 * previous and next integer section is applied in case of non-integer values.
 *
 * For NonlinearShuntCompensator-s shall only be set to one of the NonlinearShuntCompensatorPoint.sectionNumber.
 * There is no interpolation between NonlinearShuntCompensatorPoint-s.
 * @property GROUNDING_TERMINAL_MRID The terminal connecting to grounded network.
 */
@Suppress("PropertyName")
abstract class TableShuntCompensators : TableRegulatingCondEq() {

    val SHUNT_COMPENSATOR_INFO_MRID: Column = Column(++columnIndex, "shunt_compensator_info_mrid", Column.Type.STRING, NULL)
    val GROUNDED: Column = Column(++columnIndex, "grounded", Column.Type.BOOLEAN, NULL)
    val NOM_U: Column = Column(++columnIndex, "nom_u", Column.Type.INTEGER, NULL)
    val PHASE_CONNECTION: Column = Column(++columnIndex, "phase_connection", Column.Type.STRING, NOT_NULL)
    val SECTIONS: Column = Column(++columnIndex, "sections", Column.Type.DOUBLE, NULL)
    val GROUNDING_TERMINAL_MRID: Column = Column(++columnIndex, "grounding_terminal_mrid", Column.Type.STRING, NULL)

    init {
        addNonUniqueIndexes(
            listOf(GROUNDING_TERMINAL_MRID)
        )
    }

}
