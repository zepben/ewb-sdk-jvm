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
 * @property ituPhone Phone number according to ITU E.164. Will return `null` if a valid
 * @property partialItuPhone As much of the phone number according to ITU E.164 that could be formatted based on the given fields.
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

    private val maybeItuFormattedPhone = buildString {
        countryCode?.also { append(it) }
        areaCode?.trimStart('0')?.also { append(it) }
        cityCode?.also { append(it) }
        localNumber?.also { append(it) }
    }

    val ituPhone: String? = maybeItuFormattedPhone.takeIf { (countryCode != null) && (it.length <= 15) }
    val partialItuPhone: String? = maybeItuFormattedPhone.takeIf { ituPhone == null }

    override fun toString(): String =
        "$description: ${
            listOfNotNull(
                dialOut,
                internationalPrefix?.let { "$it$maybeItuFormattedPhone" } ?: maybeItuFormattedPhone,
                extension?.let { "ext $it" }
            ).joinToString(separator = " ")
        } [primary: $isPrimary]"

}
