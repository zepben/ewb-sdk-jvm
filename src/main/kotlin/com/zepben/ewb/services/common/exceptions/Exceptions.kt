/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common.exceptions

import com.zepben.ewb.cim.iec61970.base.core.Identifiable

/**
 * An exception thrown when the [Identifiable] isn't supported by an operation.
 *
 * @param message The message detailing the unsupported operation.
 */
class UnsupportedIdentifiableException(message: String) : RuntimeException(message)
