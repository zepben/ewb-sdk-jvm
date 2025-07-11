/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.extensions.iec61970.base.protection.ProtectionRelayFunction

/**
 * An overcurrent protective device with a circuit opening fusible part that is heated and severed by the passage of
 * overcurrent through it. A fuse is considered a switching device because it breaks current.
 *
 * @property function The function implemented by this Fuse
 */
class Fuse @JvmOverloads constructor(mRID: String = "") : Switch(mRID) {

    var function: ProtectionRelayFunction? = null

}
