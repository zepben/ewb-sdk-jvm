/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.customer

import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.customers.Customer
import com.zepben.evolve.cim.iec61968.customers.CustomerAgreement
import com.zepben.evolve.cim.iec61968.customers.PricingStructure
import com.zepben.evolve.cim.iec61968.customers.Tariff
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.services.common.InvokeChecker
import com.zepben.evolve.services.common.InvokedChecker
import com.zepben.evolve.services.common.NeverInvokedChecker
import com.zepben.evolve.services.common.verifyWhenServiceObjectFunctionSupportsAllServiceObjectTypes
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CustomerServiceUtilsTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    // Function references to functions with generics are not yet supported.
    // So, we take a copy of the function that has a concrete type and pass through.
    // If you get failed tests about missing IdentifiedObject types, first update the proxied function,
    // then update this one to match and then update the tests.
    private fun whenCustomerServiceObjectProxy(
        identifiedObject: IdentifiedObject,
        isCustomer: (Customer) -> String,
        isCustomerAgreement: (CustomerAgreement) -> String,
        isOrganisation: (Organisation) -> String,
        isPricingStructure: (PricingStructure) -> String,
        isTariff: (Tariff) -> String,
        isOther: (IdentifiedObject) -> String
    ): String = whenCustomerServiceObject(
        identifiedObject,
        isCustomer = isCustomer,
        isCustomerAgreement = isCustomerAgreement,
        isOrganisation = isOrganisation,
        isPricingStructure = isPricingStructure,
        isTariff = isTariff,
        isOther = isOther
    )

    private fun whenCustomerServiceObjectTester(
        identifiedObject: IdentifiedObject,
        isCustomer: InvokeChecker<Customer> = NeverInvokedChecker(),
        isCustomerAgreement: InvokeChecker<CustomerAgreement> = NeverInvokedChecker(),
        isOrganisation: InvokeChecker<Organisation> = NeverInvokedChecker(),
        isPricingStructure: InvokeChecker<PricingStructure> = NeverInvokedChecker(),
        isTariff: InvokeChecker<Tariff> = NeverInvokedChecker(),
        isOther: InvokeChecker<IdentifiedObject> = NeverInvokedChecker()
    ) {
        val returnValue = whenCustomerServiceObjectProxy(
            identifiedObject,
            isCustomer = isCustomer,
            isCustomerAgreement = isCustomerAgreement,
            isOrganisation = isOrganisation,
            isPricingStructure = isPricingStructure,
            isTariff = isTariff,
            isOther = isOther
        )

        assertThat(returnValue, equalTo(identifiedObject.toString()))
        isCustomer.verifyInvoke()
        isCustomerAgreement.verifyInvoke()
        isOrganisation.verifyInvoke()
        isPricingStructure.verifyInvoke()
        isTariff.verifyInvoke()
        isOther.verifyInvoke()
    }

    @Test
    fun `supports all customer service types`() {
        verifyWhenServiceObjectFunctionSupportsAllServiceObjectTypes(CustomerService().supportedKClasses, ::whenCustomerServiceObjectProxy)
    }

    @Test
    internal fun `invokes correct function`() {
        Customer().also { whenCustomerServiceObjectTester(it, isCustomer = InvokedChecker(it)) }
        CustomerAgreement().also { whenCustomerServiceObjectTester(it, isCustomerAgreement = InvokedChecker(it)) }
        Organisation().also { whenCustomerServiceObjectTester(it, isOrganisation = InvokedChecker(it)) }
        PricingStructure().also { whenCustomerServiceObjectTester(it, isPricingStructure = InvokedChecker(it)) }
        Tariff().also { whenCustomerServiceObjectTester(it, isTariff = InvokedChecker(it)) }
        object : IdentifiedObject() {}.also { whenCustomerServiceObjectTester(it, isOther = InvokedChecker(it)) }
    }
}

