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
 * [ZBEX] Defines the type of polarizing quantity used by the directional relay. This informs how the relay
 * determines the reference voltage from the Voltage transformers associated with its parent ProtectionEquipment.
 */
@ZBEX
enum class PolarizingQuantityType {

    /**
     * [ZBEX] Type is unknown.
     */
    @ZBEX
    UNKNOWN,

    /**
     * [ZBEX] Uses the voltage of the same phase as the current element (e.g., Va for an Ia element).
     */
    @ZBEX
    SELF_PHASE_VOLTAGE,

    /**
     * [ZBEX] Uses a quadrature voltage (e.g., Vbc for an Ia element, specific convention applies).
     */
    @ZBEX
    QUADRATURE_VOLTAGE,

    /**
     * [ZBEX] Uses the zero sequence voltage (Vo), derived from three phase voltages.
     */
    @ZBEX
    ZERO_SEQUENCE_VOLTAGE,

    /**
     * [ZBEX] Uses the negative sequence voltage (V2), derived from three phase voltages.
     */
    @ZBEX
    NEGATIVE_SEQUENCE_VOLTAGE,

    /**
     * [ZBEX] Uses the positive sequence voltage (V1), derived from three phase voltages.
     */
    @ZBEX
    POSITIVE_SEQUENCE_VOLTAGE,

}
