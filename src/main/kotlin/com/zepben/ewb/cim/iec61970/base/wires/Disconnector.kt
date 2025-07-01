/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

/**
 * A manually operated or motor operated mechanical switching device used for changing the connections in a circuit, or for isolating
 * a circuit or equipment from a source of power. It is required to open or close circuits when negligible current is broken or made.
 */
class Disconnector @JvmOverloads constructor(mRID: String = "") : Switch(mRID)
