/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

/**
 * Winding connection type.
 */
enum class WindingConnection {

    /**
     * Default.
     */
    UNKNOWN,

    /**
     * Delta.
     */
    D,

    /**
     * Wye.
     */
    Y,

    /**
     * ZigZag.
     */
    Z,

    /**
     * Wye, with neutral brought out for grounding.
     */
    Yn,

    /**
     * ZigZag, with neutral brought out for grounding.
     */
    Zn,

    /**
     * Auto-transformer common winding.
     */
    A,

    /**
     * Independent winding, for single-phase connections.
     */
    I

}
