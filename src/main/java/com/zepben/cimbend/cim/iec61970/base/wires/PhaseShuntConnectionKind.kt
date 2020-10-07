/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61970.base.wires

/**
 * The configuration of phase connections for a single terminal device such as a load or capacitor.
 *
 * @property UNKNOWN
 * @property D Delta Connection
 * @property Y Wye connection
 * @property Yn Wye, with neutral brought out for grounding.
 * @property I Independent winding, for single-phase connections.
 * @property G Ground connection; use when explicit connection to ground needs to be expressed in combination with the phase * code, such as for electrical wire/cable or for meters.
 */
enum class PhaseShuntConnectionKind {

    UNKNOWN,
    D,
    Y,
    Yn,
    I,
    G,
}
