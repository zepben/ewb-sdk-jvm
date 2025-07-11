/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject

data class UnresolvedReference<T : IdentifiedObject, R : IdentifiedObject>(
    val from: T,
    val toMrid: String,
    val resolver: ReferenceResolver<T, R>,
    val reverseResolver: ReferenceResolver<R, T>? = null
)
