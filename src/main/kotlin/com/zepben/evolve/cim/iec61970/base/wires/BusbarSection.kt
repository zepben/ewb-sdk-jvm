/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

/**
 * A conductor, or group of conductors, with negligible impedance, that serve to connect other conducting equipment within a single substation.
 *
 * Voltage measurements are typically obtained from voltage transformers that are connected to busbar sections. A bus bar section may have many
 * physical terminals but for analysis is modelled with exactly one logical terminal.
 */
class BusbarSection @JvmOverloads constructor(mRID: String = "") : Connector(mRID)
