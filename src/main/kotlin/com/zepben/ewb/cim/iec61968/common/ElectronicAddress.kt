/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.common

import com.zepben.ewb.cim.extensions.ZBEX

/**
 * Electronic address information.
 *
 * @property email1 (if applicable) Area or region code.
 * @property isPrimary [ZBEX] Is this phone number the primary number?
 * @property description [ZBEX] Description for phone number, e.g: home, work, mobile.
 */
data class ElectronicAddress (

    var email1: String? = null,

    @ZBEX
    var isPrimary: Boolean? = null,

    @ZBEX
    var description: String? = null

)
