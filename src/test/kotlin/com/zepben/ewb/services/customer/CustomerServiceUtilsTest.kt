/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.customer

import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61968.customers.Customer
import com.zepben.ewb.cim.iec61968.customers.CustomerAgreement
import com.zepben.ewb.cim.iec61968.customers.PricingStructure
import com.zepben.ewb.cim.iec61968.customers.Tariff
import com.zepben.ewb.cim.iec61970.base.core.Identifiable
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.services.common.verifyWhenServiceFunctionSupportsAllServiceTypes
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CustomerServiceUtilsTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun `supports all customer service types`() {
        verifyWhenServiceFunctionSupportsAllServiceTypes(CustomerService().supportedKClasses, ::whenCustomerServiceObjectProxy)
    }

    // Function references to functions with generics are not yet supported, so we take a copy of the function that has a concrete type and pass through.
    // If you get failed tests about missing IdentifiedObject types, first update the proxied function, then update this one to match.
    internal fun whenCustomerServiceObjectProxy(
        identifiedObject: Identifiable,
        isCustomer: (Customer) -> String,
        isCustomerAgreement: (CustomerAgreement) -> String,
        isOrganisation: (Organisation) -> String,
        isPricingStructure: (PricingStructure) -> String,
        isTariff: (Tariff) -> String,
        isOther: (Identifiable) -> String
    ): String = whenCustomerServiceObject(
        identifiedObject,
        isCustomer = isCustomer,
        isCustomerAgreement = isCustomerAgreement,
        isOrganisation = isOrganisation,
        isPricingStructure = isPricingStructure,
        isTariff = isTariff,
        isOther = isOther
    )

}
