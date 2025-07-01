/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.core

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.EquipmentContainer

/**
 * [ZBEX]
 * A collection of equipment for organizational purposes, used for grouping distribution resources located at a site.
 */
@ZBEX
class Site @JvmOverloads constructor(mRID: String = "") : EquipmentContainer(mRID)
