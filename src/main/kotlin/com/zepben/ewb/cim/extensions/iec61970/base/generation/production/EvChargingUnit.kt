/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.generation.production

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.generation.production.PowerElectronicsUnit

/**
 * [ZBEX]
 * An electric vehicle charging station.
 */
@ZBEX
class EvChargingUnit @JvmOverloads constructor(mRID: String = "") : PowerElectronicsUnit(mRID)
