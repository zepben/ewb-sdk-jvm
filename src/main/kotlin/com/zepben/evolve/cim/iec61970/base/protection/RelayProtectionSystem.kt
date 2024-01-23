/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.infiec61970.protection.ProtectionKind

/**
 * A relay system for controlling ProtectedSwitches.
 *
 * @property protectionKind The kind of protection being provided by this protection equipment.
 */
class RelayProtectionSystem(mRID: String) : Equipment(mRID) {

    var protectionKind: ProtectionKind = ProtectionKind.UNKNOWN

}
