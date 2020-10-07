/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61968.common

/**
 * General purpose street and postal address information.
 *
 * @property postalCode Postal code for the address.
 * @property townDetail Town detail.
 */
data class StreetAddress(val postalCode: String, val townDetail: TownDetail?)

