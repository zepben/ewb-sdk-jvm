/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment

/**
 * A point where the system is grounded used for connecting conducting equipment to ground. The power system model can have any number of grounds.
 */
class Ground @JvmOverloads constructor(mRID: String = "") : ConductingEquipment(mRID)
