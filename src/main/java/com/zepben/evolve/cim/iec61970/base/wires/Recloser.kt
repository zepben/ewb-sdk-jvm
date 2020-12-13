/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

/**
 * Pole-mounted fault interrupter with built-in phase and ground relays, current transformer (CT), and supplemental controls.
 */
class Recloser @JvmOverloads constructor(mRID: String = "") : ProtectedSwitch(mRID)
