/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61968.common

/**
 * Town details, in the context of address.
 *
 * @property name Town name.
 * @property stateOrProvince Name of the state or province.
 */
data class TownDetail(val name: String, val stateOrProvince: String)

