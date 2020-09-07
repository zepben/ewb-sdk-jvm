/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.customer

import com.zepben.cimbend.cim.iec61968.common.Organisation
import com.zepben.cimbend.cim.iec61968.customers.Customer
import com.zepben.cimbend.cim.iec61968.customers.CustomerAgreement
import com.zepben.cimbend.cim.iec61968.customers.PricingStructure
import com.zepben.cimbend.cim.iec61968.customers.Tariff
import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.common.InvokeChecker
import com.zepben.cimbend.common.InvokedChecker
import com.zepben.cimbend.common.NeverInvokedChecker
import com.zepben.cimbend.common.verifyWhenServiceObjectFunctionSupportsAllServiceObjectTypes
import com.zepben.test.util.junit.SystemLogExtension
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

