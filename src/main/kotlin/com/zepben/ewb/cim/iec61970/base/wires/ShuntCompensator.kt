/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61968.assetinfo.ShuntCompensatorInfo

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

    override var assetInfo: ShuntCompensatorInfo? = null

    var grounded: Boolean = false
    var nomU: Int? = null
    var phaseConnection: PhaseShuntConnectionKind = PhaseShuntConnectionKind.UNKNOWN
    var sections: Double? = null
}
