/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.wires

import com.zepben.ewb.cim.extensions.ZBEX

/**
 * [ZBEX]
 * Normal apparent power rating for a PowerTransformerEnd based on their cooling types.
 *
 * @property coolingType [ZBEX] The cooling type for this rating.
 * @property ratedS [ZBEX] The normal apparent power rating for this cooling type.
 */
@ZBEX
data class TransformerEndRatedS(
    @ZBEX val coolingType: TransformerCoolingType,
    @ZBEX val ratedS: Int
)
