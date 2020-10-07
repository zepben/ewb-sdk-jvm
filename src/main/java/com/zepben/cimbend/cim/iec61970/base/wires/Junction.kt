/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61970.base.wires

/**
 * A point where one or more conducting equipments are connected with zero resistance.
 */
class Junction @JvmOverloads constructor(mRID: String = "") : Connector(mRID)
