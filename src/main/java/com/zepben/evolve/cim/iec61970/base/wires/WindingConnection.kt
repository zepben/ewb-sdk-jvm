/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

/**
 * Winding connection type.
 *
 * @property UNKNOWN_WINDING Default
 * @property D Delta
 * @property Y Wye
 * @property Z ZigZag
 * @property Yn Wye, with neutral brought out for grounding
 * @property Zn ZigZag, with neutral brought out for grounding
 * @property A Auto-transformer common winding
 * @property I Independent winding, for single-phase connections
 */
enum class WindingConnection {

    UNKNOWN_WINDING,
    D,
    Y,
    Z,
    Yn,
    Zn,
    A,
    I
}
