/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.common

import com.zepben.ewb.cim.extensions.ZBEX

/**
 * Telephone number.
 *
 * @property areaCode (if applicable) Area or region code.
 * @property cityCode (if applicable) City code.
 * @property countryCode Country code.
 * @property dialOut (if applicable) Dial out code, for instance to call outside an enterprise.
 * @property extension (if applicable) Extension for this telephone number.
 * @property internationalPrefix (if applicable) Prefix used when calling an international number.
 * @property localNumber Main (local) part of this telephone number.
 * @property isPrimary [ZBEX] Is this phone number the primary number?
 * @property description [ZBEX] Description for phone number, e.g: home, work, mobile.
 */
data class TelephoneNumber(
    val areaCode: String? = null,
    val cityCode: String? = null,
    val countryCode: String? = null,
    val dialOut: String? = null,
    val extension: String? = null,
    val internationalPrefix: String? = null,
    val localNumber: String? = null,
    @ZBEX val isPrimary: Boolean? = null,
    @ZBEX val description: String? = null,
) {

    //todo irn?

}
