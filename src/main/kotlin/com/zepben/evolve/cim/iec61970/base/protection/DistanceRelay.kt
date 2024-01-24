/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

/**
 * A protective device used in power systems that measures the impedance of a transmission line to determine the distance to a fault, and initiates circuit
 * breaker tripping to isolate the faulty section and safeguard the power system.
 *
 * @property backwardBlind The reverse blind impedance (in ohms) that defines the area to be blinded in the opposite direction of the power flow.
 * @property backwardReach The reverse reach impedance (in ohms) that determines the maximum distance along the transmission line in the opposite direction of
 *                         power flow for which the relay will provide protection.
 * @property backwardReactance The reverse reactance (in ohms) that determines the maximum distance along the transmission line in the opposite direction of
 *                             power flow for which the relay will provide protection.
 * @property forwardBlind The forward blind impedance (in ohms) that defines the area to be blinded in the opposite direction of the power flow.
 * @property forwardReach The forward reach impedance (in ohms) that determines the maximum distance along the transmission line in the opposite direction of
 *                         power flow for which the relay will provide protection.
 * @property forwardReactance The forward reactance (in ohms) that determines the maximum distance along the transmission line in the opposite direction of
 *                             power flow for which the relay will provide protection.
 * @property operationPhaseAngle1 The phase angle (in degrees) between voltage and current during normal operating conditions for zone 1 relay.
 * @property operationPhaseAngle2 The phase angle (in degrees) between voltage and current during normal operating conditions for zone 2 relay.
 * @property operationPhaseAngle3 The phase angle (in degrees) between voltage and current during normal operating conditions for zone 3 relay.
 */
class DistanceRelay(mRID: String = "") : ProtectionRelayFunction(mRID) {

    var backwardBlind: Double? = null
    var backwardReach: Double? = null
    var backwardReactance : Double? = null

    /**
     * The forward blind impedance (in ohms) that defines the area to be blinded in the direction of the power flow.
     */
    var forwardBlind : Double? = null

    /**
     * The forward reach impedance (in ohms) that determines the maximum distance along the transmission line in the
     * direction of power flow for which the relay will provide protection.
     */
    var forwardReach : Double? = null

    /**
     * The forward reactance (in ohms) that determines the maximum distance along the transmission line in the direction
     * of power flow for which the relay will provide protection.
     */
    var forwardReactance : Double? = null

    /**
     * The phase angle (in degrees) between voltage and current during normal operating conditions for zone 1 relay.
     */
    var operationPhaseAngle1 : Double? = null

    /**
     * The phase angle (in degrees) between voltage and current during normal operating conditions for zone 2 relay.
     */
    var operationPhaseAngle2 : Double? = null

    /**
     * The phase angle (in degrees) between voltage and current during normal operating conditions for zone 3 relay.
     */
    var operationPhaseAngle3: Double? = null

}
