/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.auxiliaryequipment

import com.zepben.ewb.cim.iec61970.base.core.Equipment
import com.zepben.ewb.cim.iec61970.base.core.Terminal

/**
 * [AuxiliaryEquipment] describe equipment that is not performing any primary functions but support for the equipment performing the primary function.
 *
 * [AuxiliaryEquipment] is attached to primary equipment via an association with [Terminal].
 *
 * @property terminal The [Terminal] at the equipment where the [AuxiliaryEquipment] is attached.
 */
abstract class AuxiliaryEquipment(mRID: String) : Equipment(mRID) {

    var terminal: Terminal? = null
}
