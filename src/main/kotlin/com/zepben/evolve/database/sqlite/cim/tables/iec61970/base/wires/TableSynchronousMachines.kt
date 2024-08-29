/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.cim.tables.Column
import com.zepben.evolve.database.sqlite.cim.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.cim.tables.Column.Nullable.NULL

/**
 * A class representing the SynchronousMachine columns required for the Datdatabaseabase table.
 *
 * @property BASE_Q Default base reactive power value in VAr. This value represents the initial reactive power that can be used by any application function.
 * @property CONDENSER_P Active power consumed (watts) when in condenser mode operation.
 * @property EARTHING Indicates whether the generator is earthed. Used for short circuit data exchange according to IEC 60909.
 * @property EARTHING_STAR_POINT_R Generator star point earthing resistance in Ohms (Re). Used for short circuit data exchange according to IEC 60909.
 * @property EARTHING_STAR_POINT_X Generator star point earthing reactance in Ohms (Xe). Used for short circuit data exchange according to IEC 60909.
 * @property IKK Steady-state short-circuit current (in A for the profile) of generator with compound excitation during 3-phase short circuit.
 *      - Ikk=0: Generator with no compound excitation. - Ikk<>0: Generator with compound excitation.
 *      Ikk is used to calculate the minimum steady-state short-circuit current for generators with compound excitation. (4.6.1.2 in IEC 60909-0:2001).
 *      Used only for single fed short circuit on a generator. (4.3.4.2. in IEC 60909-0:2001).
 * @property MAX_Q Maximum reactive power limit in VAr. This is the maximum (nameplate) limit for the unit.
 * @property MAX_U Maximum voltage limit for the unit in volts.
 * @property MIN_Q Minimum reactive power limit for the unit in VAr.
 * @property MIN_U Minimum voltage limit for the unit in volts.
 * @property MU Factor to calculate the breaking current (Section 4.5.2.1 in IEC 60909-0). Used only for single fed short circuit on a generator (Section 4.3.4.2. in IEC 60909-0).
 * @property R Equivalent resistance (RG) of generator as a percentage. RG is considered for the calculation of all currents,
 *      except for the calculation of the peak current ip. Used for short circuit data exchange according to IEC 60909.
 * @property R0 Zero sequence resistance of the synchronous machine as a percentage.
 * @property R2 Negative sequence resistance as a percentage.
 * @property SAT_DIRECT_SUBTRANS_X Direct-axis subtransient reactance saturated as a percentage, also known as Xd"sat.
 * @property SAT_DIRECT_SYNC_X Direct-axes saturated synchronous reactance (xdsat); reciprocal of short-circuit ration, as a percentage.
 *      Used for short circuit data exchange, only for single fed short circuit on a generator. (4.3.4.2. in IEC 60909-0:2001).
 * @property SAT_DIRECT_TRANS_X Saturated Direct-axis transient reactance as a percentage. The attribute is primarily used for short circuit calculations according to ANSI.
 * @property X0 Zero sequence reactance of the synchronous machine as a percentage.
 * @property X2 Negative sequence reactance as a percentage.
 * @property TYPE Modes that this synchronous machine can operate in.
 * @property OPERATING_MODE Current mode of operation.
 */
@Suppress("PropertyName")
class TableSynchronousMachines : TableRotatingMachines() {

    val BASE_Q: Column = Column(++columnIndex, "base_q", "NUMBER", NULL)
    val CONDENSER_P: Column = Column(++columnIndex, "condenser_p", "INTEGER", NULL)
    val EARTHING: Column = Column(++columnIndex, "earthing", "BOOLEAN", NOT_NULL)
    val EARTHING_STAR_POINT_R: Column = Column(++columnIndex, "earthing_star_point_r", "NUMBER", NULL)
    val EARTHING_STAR_POINT_X: Column = Column(++columnIndex, "earthing_star_point_x", "NUMBER", NULL)
    val IKK: Column = Column(++columnIndex, "ikk", "NUMBER", NULL)
    val MAX_Q: Column = Column(++columnIndex, "max_q", "NUMBER", NULL)
    val MAX_U: Column = Column(++columnIndex, "max_u", "INTEGER", NULL)
    val MIN_Q: Column = Column(++columnIndex, "min_q", "NUMBER", NULL)
    val MIN_U: Column = Column(++columnIndex, "min_u", "INTEGER", NULL)
    val MU: Column = Column(++columnIndex, "mu", "NUMBER", NULL)
    val R: Column = Column(++columnIndex, "r", "NUMBER", NULL)
    val R0: Column = Column(++columnIndex, "r0", "NUMBER", NULL)
    val R2: Column = Column(++columnIndex, "r2", "NUMBER", NULL)
    val SAT_DIRECT_SUBTRANS_X: Column = Column(++columnIndex, "sat_direct_subtrans_x", "NUMBER", NULL)
    val SAT_DIRECT_SYNC_X: Column = Column(++columnIndex, "sat_direct_sync_x", "NUMBER", NULL)
    val SAT_DIRECT_TRANS_X: Column = Column(++columnIndex, "sat_direct_trans_x", "NUMBER", NULL)
    val X0: Column = Column(++columnIndex, "x0", "NUMBER", NULL)
    val X2: Column = Column(++columnIndex, "x2", "NUMBER", NULL)
    val TYPE: Column = Column(++columnIndex, "type", "TEXT", NOT_NULL)
    val OPERATING_MODE: Column = Column(++columnIndex, "operating_mode", "TEXT", NOT_NULL)

    override val name: String = "synchronous_machines"

}
