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
 * A conductor, or group of conductors, with negligible impedance, that serve to connect other conducting equipment
 * within a single substation and are modelled with a single logical terminal.
 */
abstract class Connector(mRID: String = "") : ConductingEquipment(mRID)
