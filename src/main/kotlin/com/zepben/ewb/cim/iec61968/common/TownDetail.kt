/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.common

/**
 * Town details, in the context of address.
 *
 * @property name Town name.
 * @property stateOrProvince Name of the state or province.
 */
data class TownDetail(
    val name: String? = null,
    val stateOrProvince: String? = null
) {

    /**
     * Check to see if all fields of this [TownDetail] are null or empty
     *
     * @return true if all fields are null or empty, otherwise false
     */
    fun allFieldsNullOrEmpty(): Boolean = name.isNullOrEmpty() && stateOrProvince.isNullOrEmpty()

}
