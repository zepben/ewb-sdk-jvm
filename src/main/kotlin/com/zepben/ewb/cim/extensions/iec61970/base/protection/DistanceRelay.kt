/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.protection

import com.zepben.ewb.cim.extensions.ZBEX

/**
 * [ZBEX] This extension is in-line with the CIM working group for replacing the `protection` package, can be replaced when the working
 * group outcome is merged into the CIM model.
 *
 * A protective device used in power systems that measures the impedance of a transmission line to determine the distance to a fault, and initiates circuit
 * breaker tripping to isolate the faulty section and safeguard the power system.
 *
 * @property backwardBlind [ZBEX] The reverse blind impedance (in ohms) that defines the area to be blinded in the opposite direction of the power flow.
 * @property backwardReach [ZBEX] The reverse reach impedance (in ohms) that determines the maximum distance along the transmission line in the opposite
 * direction of power flow for which the relay will provide protection.
 * @property backwardReactance [ZBEX] The reverse reactance (in ohms) that determines the maximum distance along the transmission line in the opposite direction
 * of power flow for which the relay will provide protection.
 * @property forwardBlind [ZBEX] The forward blind impedance (in ohms) that defines the area to be blinded in the opposite direction of the power flow.
 * @property forwardReach [ZBEX] The forward reach impedance (in ohms) that determines the maximum distance along the transmission line in the opposite
 * direction of power flow for which the relay will provide protection.
 * @property forwardReactance [ZBEX] The forward reactance (in ohms) that determines the maximum distance along the transmission line in the opposite direction
 * of power flow for which the relay will provide protection.
 * @property operationPhaseAngle1 [ZBEX] The phase angle (in degrees) between voltage and current during normal operating conditions for zone 1 relay.
 * @property operationPhaseAngle2 [ZBEX] The phase angle (in degrees) between voltage and current during normal operating conditions for zone 2 relay.
 * @property operationPhaseAngle3 [ZBEX] The phase angle (in degrees) between voltage and current during normal operating conditions for zone 3 relay.
 */
@ZBEX
class DistanceRelay(mRID: String) : ProtectionRelayFunction(mRID) {

    @ZBEX
    var backwardBlind: Double? = null

    @ZBEX
    var backwardReach: Double? = null

    @ZBEX
    var backwardReactance: Double? = null

    @ZBEX
    var forwardBlind: Double? = null

    @ZBEX
    var forwardReach: Double? = null

    @ZBEX
    var forwardReactance: Double? = null

    @ZBEX
    var operationPhaseAngle1: Double? = null

    @ZBEX
    var operationPhaseAngle2: Double? = null

    @ZBEX
    var operationPhaseAngle3: Double? = null

}
