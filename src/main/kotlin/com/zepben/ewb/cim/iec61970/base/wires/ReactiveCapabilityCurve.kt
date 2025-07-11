/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61970.base.core.Curve


/**
 * Reactive power rating envelope versus the synchronous machine's active power, in both the generating and motoring modes. For each active power value there
 * is a corresponding high and low reactive power limit value. Typically, there will be a separate curve for each coolant condition, such as hydrogen pressure.
 * The Y1 axis values represent reactive minimum and the Y2 axis values represent reactive maximum.
 */
class ReactiveCapabilityCurve @JvmOverloads constructor(mRID: String = "") : Curve(mRID)
