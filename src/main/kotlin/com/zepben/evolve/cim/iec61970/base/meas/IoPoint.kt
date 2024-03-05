/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.meas

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject

/**
 * This class describes a measurement or control value.
 * The purpose is to enable having attributes and associations common for measurement and control.
 */
abstract class IoPoint(mRID: String = "") : IdentifiedObject(mRID)
