/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61968.common

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject

/**
 * Organisation that might have roles as utility, contractor, supplier, manufacturer, customer, etc.
 */
class Organisation @JvmOverloads constructor(mRID: String = "") : IdentifiedObject(mRID)
