/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.infiec61970.protection.ProtectionKind

/**
 * An electrical device designed to respond to input conditions in a prescribed manner and after specified conditions are met to cause contact operation or
 * similar abrupt change in associated electric control circuits, or simply to display the detected condition. Protection equipment is associated with
 * conducting equipment and usually operate circuit breakers.
 *
 * @property relayDelayTime The time delay from detection of abnormal conditions to relay operation in seconds.
 * @property protectionKind The kind of protection being provided by this protection equipment.
 */
abstract class ProtectionEquipment(mRID: String = "") : Equipment(mRID) {

    var relayDelayTime: Double? = null
    var protectionKind: ProtectionKind? = null

}
