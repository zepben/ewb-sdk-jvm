/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.common

/**
 * Street details, in the context of address.
 *
 * @property buildingName (if applicable) In certain cases the physical location of the place of interest does not have a direct point of entry from the street,
 *                        but may be located inside a larger structure such as a building, complex, office block, apartment, etc.
 * @property floorIdentification The identification by name or number, expressed as text, of the floor in the building as part of this address.
 * @property name Name of the street.
 * @property number Designator of the specific location on the street.
 * @property suiteNumber Number of the apartment or suite.
 * @property type Type of street. Examples include: street, circle, boulevard, avenue, road, drive, etc.
 * @property displayAddress The address as it should be displayed to a user.
 */
data class StreetDetail(
    val buildingName: String? = null,
    val floorIdentification: String? = null,
    val name: String? = null,
    val number: String? = null,
    val suiteNumber: String? = null,
    val type: String? = null,
    val displayAddress: String? = null
) {

    /**
     * Check to see if all fields of this [StreetDetail] are empty
     *
     * @return true if all fields are empty, otherwise false
     */
    fun allFieldsEmpty(): Boolean = (
        buildingName.isEmpty() &&
            floorIdentification.isEmpty() &&
            name.isEmpty() &&
            number.isEmpty() &&
            suiteNumber.isEmpty() &&
            type.isEmpty() &&
            displayAddress.isEmpty()
        )

}
