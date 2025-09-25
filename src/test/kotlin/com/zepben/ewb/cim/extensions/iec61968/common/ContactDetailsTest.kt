/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61968.common

import com.zepben.ewb.cim.iec61968.common.*
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ContactDetailsTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.Companion.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(ContactDetails().id, not(equalTo("")))
        assertThat(ContactDetails("id").id, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val contactDetails = ContactDetails()

        assertThat(contactDetails.contactAddress, nullValue())
        assertThat(contactDetails.contactType, nullValue())
        assertThat(contactDetails.firstName, nullValue())
        assertThat(contactDetails.lastName, nullValue())
        assertThat(contactDetails.preferredContactMethod, equalTo(ContactMethodType.UNKNOWN))
        assertThat(contactDetails.isPrimary, nullValue())
        assertThat(contactDetails.businessName, nullValue())

        contactDetails.fillFields()

        assertThat(
            contactDetails.contactAddress, equalTo(
                StreetAddress(
                    postalCode = "1",
                    townDetail = TownDetail(name = "2", stateOrProvince = "3"),
                    poBox = "4",
                    streetDetail = StreetDetail(
                        buildingName = "5",
                        floorIdentification = "6",
                        name = "7",
                        number = "8",
                        suiteNumber = "9",
                        type = "10",
                        displayAddress = "11"
                    )
                )
            )
        )
        assertThat(contactDetails.contactType, equalTo("12"))
        assertThat(contactDetails.firstName, equalTo("13"))
        assertThat(contactDetails.lastName, equalTo("14"))
        assertThat(contactDetails.preferredContactMethod, equalTo(ContactMethodType.LETTER))
        assertThat(contactDetails.isPrimary, equalTo(true))
        assertThat(contactDetails.businessName, equalTo("15"))
    }

    @Test
    internal fun phoneNumbers() {
        PrivateCollectionValidator.validateUnordered(
            ::ContactDetails,
            { TelephoneNumber(localNumber = it.toString()) },
            ContactDetails::phoneNumbers,
            ContactDetails::numPhoneNumbers,
            { contact, id -> contact.phoneNumbers.firstOrNull { it.localNumber == id } },
            ContactDetails::addPhoneNumber,
            ContactDetails::removePhoneNumber,
            ContactDetails::clearPhoneNumbers,
            { it.localNumber!! },
            PrivateCollectionValidator.DuplicateBehaviour.SUPPORTED,
        )
    }

    @Test
    internal fun electronicAddresses() {
        PrivateCollectionValidator.validateUnordered(
            ::ContactDetails,
            { ElectronicAddress(email1 = it.toString()) },
            ContactDetails::electronicAddresses,
            ContactDetails::numElectronicAddresses,
            { contact, id -> contact.electronicAddresses.firstOrNull { it.email1 == id } },
            ContactDetails::addElectronicAddress,
            ContactDetails::removeElectronicAddress,
            ContactDetails::clearElectronicAddresses,
            { it.email1!! },
            PrivateCollectionValidator.DuplicateBehaviour.SUPPORTED,
        )
    }

}
