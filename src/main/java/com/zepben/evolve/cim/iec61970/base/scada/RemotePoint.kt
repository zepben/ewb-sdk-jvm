/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.scada

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject

/**
 * For a RTU remote points correspond to telemetered values or control outputs. Other units (e.g. control centers) usually
 * also contain calculated values.
 */
abstract class RemotePoint(mRID: String = "") : IdentifiedObject(mRID)
