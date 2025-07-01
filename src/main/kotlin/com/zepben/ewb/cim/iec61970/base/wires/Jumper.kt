/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

/**
 * A short section of conductor with negligible impedance which can be manually removed and replaced if the circuit is de-energized.
 * Note that zero-impedance branches can potentially be modeled by other equipment types.
 */
class Jumper @JvmOverloads constructor(mRID: String = "") : Switch(mRID)
