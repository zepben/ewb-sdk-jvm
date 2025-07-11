/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.common

import com.zepben.ewb.cim.iec61970.base.domain.DateTimeInterval

/**
 * Formal agreement between two parties defining the terms and conditions for a set of services. The specifics of
 * the services are, in turn, defined via one or more service agreements.
 *
 * @property validityInterval Date and time interval this agreement is valid (from going into effect to termination).
 */
abstract class Agreement(mRID: String = "") : Document(mRID) {

    val validityInterval: DateTimeInterval? = null

}
