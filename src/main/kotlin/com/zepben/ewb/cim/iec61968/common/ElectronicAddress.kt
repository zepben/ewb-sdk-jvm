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
 * @property email1 Primary email address.
 * @property isPrimary [ZBEX] Whether this email is the primary email address of the contact.
 * @property description [ZBEX] A description for this email, e.g: work, personal.
 */
data class ElectronicAddress(
    val email1: String? = null,
    @ZBEX val isPrimary: Boolean? = null,
    @ZBEX val description: String? = null,
)
