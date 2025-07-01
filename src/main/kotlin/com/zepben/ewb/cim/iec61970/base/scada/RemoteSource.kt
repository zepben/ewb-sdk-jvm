/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.scada

import com.zepben.ewb.cim.iec61970.base.meas.Measurement

/**
 * Remote sources are state variables that are telemetered or calculated within the remote unit.
 *
 * @property measurement The [Measurement] for the [RemoteSource] point.
 */
class RemoteSource @JvmOverloads constructor(mRID: String = "") : RemotePoint(mRID) {

    var measurement: Measurement? = null
}
