/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.protection

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode

/**
 * [ZBEX] A Directional Current Relay is a type of protective relay used in electrical power systems to detect the direction of current flow and operate only
 * when the current exceeds a certain threshold in a specified direction.
 *
 * @property directionalCharacteristicAngle [ZBEX] The characteristic angle (in degrees) that defines the boundary between the operate and restrain regions of
 * the directional element, relative to the polarizing quantity. Often referred to as Maximum Torque Angle (MTA) or Relay Characteristic Angle (RCA).
 * @property polarizingQuantityType [ZBEX] Specifies the type of voltage to be used for polarization. This guides the selection/derivation of voltage from the
 * VTs.
 * @property relayElementPhase [ZBEX] The phase associated with this directional relay element. This helps in selecting the correct 'self-phase' or other
 * phase-derived.
 * @property minimumPickupCurrent [ZBEX] The minimum current magnitude required for the directional element to operate reliably and determine direction. This
 * might be different from the main pickupCurrent for the overcurrent function.
 * @property currentLimit1 [ZBEX] Current limit number 1 for inverse time pickup in amperes.
 * @property inverseTimeFlag [ZBEX] Set true if the current relay has inverse time characteristic.
 * @property timeDelay1 [ZBEX] Inverse time delay number 1 for current limit number 1 in seconds.
 */
@ZBEX
class DirectionalCurrentRelay(mRID: String) : ProtectionRelayFunction(mRID) {

    @ZBEX
    var directionalCharacteristicAngle: Double? = null

    @ZBEX
    var polarizingQuantityType: PolarizingQuantityType = PolarizingQuantityType.UNKNOWN

    @ZBEX
    var relayElementPhase: PhaseCode = PhaseCode.NONE

    @ZBEX
    var minimumPickupCurrent: Double? = null

    @ZBEX
    var currentLimit1: Double? = null

    @ZBEX
    var inverseTimeFlag: Boolean? = null

    @ZBEX
    var timeDelay1: Double? = null

}
