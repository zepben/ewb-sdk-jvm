/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61968.common

/**
 * General purpose street and postal address information.
 *
 * @property postalCode Postal code for the address.
 * @property townDetail Town detail.
 * @property poBox Post office box.
 * @property streetDetail Street detail.
 */
data class StreetAddress(
    val postalCode: String = "1234",
    val townDetail: TownDetail? = TownDetail("name", "stateOrProvince"),
    val poBox: String = "5678",
    val streetDetail: StreetDetail? = StreetDetail("buildingName", "floorIdentification", "name", "number", "suiteNumber", "type", "displayAddress")
)

