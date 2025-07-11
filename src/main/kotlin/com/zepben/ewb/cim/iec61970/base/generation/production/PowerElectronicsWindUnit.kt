/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.generation.production

/**
 * A wind generating unit that connects to the AC network with power electronics rather than rotating machines or an aggregation of such units.
 */
class PowerElectronicsWindUnit @JvmOverloads constructor(mRID: String = "") : PowerElectronicsUnit(mRID)
