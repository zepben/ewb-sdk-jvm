/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject

/**
 * A scheme that a group of relay functions implement. For example, typically schemes are primary and secondary, or main and failsafe.
 */
class ProtectionRelayScheme(mRID: String = "") : IdentifiedObject(mRID)
