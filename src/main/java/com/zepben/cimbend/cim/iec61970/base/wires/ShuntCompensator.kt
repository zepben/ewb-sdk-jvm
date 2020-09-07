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
package com.zepben.cimbend.cim.iec61970.base.wires

/**
 * A shunt capacitor or reactor or switchable bank of shunt capacitors or reactors. A section of a shunt compensator is an individual
 * capacitor or reactor.  A negative value for reactivePerSection indicates that the compensator is a reactor. ShuntCompensator is a
 * single terminal device.  Ground is implied.
 *
 * @property grounded Used for Yn and Zn connections. True if the neutral is solidly grounded.
 * @property nomU The voltage at which the nominal reactive power may be calculated. This should normally be within 10% of the voltage at which the capacitor is connected to the network.
 * @property phaseConnection The type of phase connection, such as wye or delta.
 * @property sections Shunt compensator sections in use.
 *                    Starting value for steady state solution. Non integer values are allowed to support continuous variables.
 *                    The reasons for continuous value are to support study cases where no discrete shunt compensator's has yet been
 *                    designed, a solutions where a narrow voltage band force the sections to oscillate or accommodate for a continuous
 *                    solution as input.
 *
 *                    For [LinearShuntCompensator] the value shall be between zero and [ShuntCompensator.maximumSections]. At value zero the
 *                    shunt compensator conductance and admittance is zero. Linear interpolation of conductance and admittance between the
 *                    previous and next integer section is applied in case of non-integer values.
 *
 *                    For [NonlinearShuntCompensator]-s shall only be set to one of the NonlinearShuntCompensatorPoint.sectionNumber.
 *                    There is no interpolation between NonlinearShuntCompensatorPoint-s.
 */
abstract class ShuntCompensator(mRID: String = "") : RegulatingCondEq(mRID) {

    var grounded: Boolean = false
    var nomU: Int = 0
    var phaseConnection: PhaseShuntConnectionKind = PhaseShuntConnectionKind.UNKNOWN
    var sections: Double = 0.0
}
