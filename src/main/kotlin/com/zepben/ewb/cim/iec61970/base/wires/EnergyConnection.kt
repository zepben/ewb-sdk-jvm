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
 * A connection of energy generation or consumption on the power system model.
 */
abstract class EnergyConnection(mRID: String) : ConductingEquipment(mRID)
