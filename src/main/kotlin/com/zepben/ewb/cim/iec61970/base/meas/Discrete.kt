/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.meas

/**
 * Discrete represents a discrete Measurement, i.e. a Measurement representing discrete values, e.g. a Breaker position.
 */
class Discrete @JvmOverloads constructor(mRID: String = "") : Measurement(mRID)
