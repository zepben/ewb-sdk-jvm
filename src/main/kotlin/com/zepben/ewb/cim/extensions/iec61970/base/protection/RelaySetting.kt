/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.protection

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.domain.UnitSymbol

/**
 * [ZBEX] This extension is in-line with the CIM working group for replacing the `protection` package, can be replaced when the working
 * group outcome is merged into the CIM model.
 *
 * The threshold settings for a given relay.
 *
 * @property unitSymbol [ZBEX] The unit of the value.
 * @property value [ZBEX] The value of the setting, e.g voltage, current, etc.
 * @property name [ZBEX] The name of the setting.
 */
@ZBEX
data class RelaySetting @JvmOverloads constructor(
    @ZBEX val unitSymbol: UnitSymbol,
    @ZBEX val value: Double,
    @ZBEX val name: String? = null
)
