/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.common

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class TelephoneNumberTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.Companion.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(TelephoneNumber(), notNullValue())
    }

    @Test
    internal fun accessorCoverage() {
        val number = TelephoneNumber(
            areaCode = "areaCode",
            cityCode = "cityCode",
            countryCode = "countryCode",
            dialOut = "dialOut",
            extension = "extension",
            internationalPrefix = "internationalPrefix",
            localNumber = "localNumber",
            isPrimary = true,
            description = "description",
        )

        assertThat(number.areaCode, equalTo("areaCode"))
        assertThat(number.cityCode, equalTo("cityCode"))
        assertThat(number.countryCode, equalTo("countryCode"))
        assertThat(number.dialOut, equalTo("dialOut"))
        assertThat(number.extension, equalTo("extension"))
        assertThat(number.internationalPrefix, equalTo("internationalPrefix"))
        assertThat(number.localNumber, equalTo("localNumber"))
        assertThat(number.isPrimary, equalTo(true))
        assertThat(number.description, equalTo("description"))
    }

    @Test
    internal fun `formats ITU number`() {
        val number = TelephoneNumber("1", "2", "3", "4", "5", "6", "7", true, "9")

        assertThat(number.ituPhone, equalTo("3127"))
    }

    @Test
    internal fun `ITU number strips leading zeros from area code`() {
        val number = TelephoneNumber(areaCode = "0001", cityCode = "2", countryCode = "3", localNumber = "7")

        assertThat(number.ituPhone, equalTo("3127"))
    }

    @Test
    internal fun `ITU number requires a country code`() {
        val number = TelephoneNumber(areaCode = "1", cityCode = "2", localNumber = "7")

        assertThat(number.ituPhone, nullValue())
    }

    @Test
    internal fun `ITU number has a maximum of 15 digits`() {
        val number1 = TelephoneNumber(areaCode = "100", cityCode = "200", countryCode = "300", localNumber = "700000")
        val number2 = TelephoneNumber(areaCode = "100", cityCode = "200", countryCode = "300", localNumber = "7000000")

        assertThat(number1.ituPhone, equalTo("300100200700000"))
        assertThat(number2.ituPhone, nullValue())
    }

    @Test
    internal fun `Stripped zeros don't count towards the length limit`() {
        val number = TelephoneNumber(areaCode = "000100", cityCode = "200", countryCode = "300", localNumber = "700000")

        assertThat(number.ituPhone, equalTo("300100200700000"))
    }

    @Test
    internal fun `ITU number has optional area, city and local numbers`() {
        val number1 = TelephoneNumber(cityCode = "2", countryCode = "3", localNumber = "7")
        val number2 = TelephoneNumber(areaCode = "1", countryCode = "3", localNumber = "7")
        val number3 = TelephoneNumber(areaCode = "1", cityCode = "2", countryCode = "3")
        val number4 = TelephoneNumber(countryCode = "3")

        assertThat(number1.ituPhone, equalTo("327"))
        assertThat(number2.ituPhone, equalTo("317"))
        assertThat(number3.ituPhone, equalTo("312"))
        assertThat(number4.ituPhone, equalTo("3"))
    }

    @Test
    internal fun `partial ITU gives the available parts of invalid ITU numbers`() {
        val number1 = TelephoneNumber(areaCode = "1", cityCode = "2", localNumber = "7")
        val number2 = TelephoneNumber(areaCode = "100", cityCode = "200", countryCode = "300", localNumber = "7000000")

        assertThat(number1.partialItuPhone, equalTo("127"))
        assertThat(number2.partialItuPhone, equalTo("3001002007000000"))
    }

    @Test
    internal fun `formats the number nicely in toString`() {
        val number = TelephoneNumber(
            areaCode = "areaCode",
            cityCode = "cityCode",
            countryCode = "countryCode",
            dialOut = "dialOut",
            extension = "extension",
            internationalPrefix = "internationalPrefix",
            localNumber = "localNumber",
            isPrimary = true,
            description = "description",
        )

        // Would have ituPhone instead of partialItuPhone if the itu was valid (more than 15 digits here)
        assertThat(number.toString(), equalTo("description: dialOut internationalPrefix${number.partialItuPhone} ext extension [primary: true]"))
    }

    @Test
    internal fun `formats the number nicely in toString without optional`() {
        val number = TelephoneNumber(
            areaCode = "areaCode",
            cityCode = "cityCode",
            countryCode = "countryCode",
            localNumber = "localNumber",
            isPrimary = true,
            description = "description",
        )

        // Would have ituPhone instead of partialItuPhone if the itu was valid (more than 15 digits here)
        assertThat(number.toString(), equalTo("description: ${number.partialItuPhone} [primary: true]"))
    }

}
