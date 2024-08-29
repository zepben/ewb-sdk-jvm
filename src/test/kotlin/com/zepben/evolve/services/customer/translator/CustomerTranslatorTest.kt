/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.customer.translator

import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.customers.Customer
import com.zepben.evolve.cim.iec61968.customers.CustomerAgreement
import com.zepben.evolve.cim.iec61968.customers.PricingStructure
import com.zepben.evolve.cim.iec61968.customers.Tariff
import com.zepben.evolve.database.sqlite.cim.customer.CustomerDatabaseTables
import com.zepben.evolve.database.sqlite.cim.tables.associations.TableCustomerAgreementsPricingStructures
import com.zepben.evolve.database.sqlite.cim.tables.associations.TablePricingStructuresTariffs
import com.zepben.evolve.services.common.testdata.fillFieldsCommon
import com.zepben.evolve.services.common.translator.TranslatorTestBase
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.customer.CustomerServiceComparator
import com.zepben.evolve.services.customer.testdata.fillFields

internal class CustomerTranslatorTest : TranslatorTestBase<CustomerService>(
    ::CustomerService,
    CustomerServiceComparator(),
    CustomerDatabaseTables(),
    CustomerService::addFromPb
) {

    private val csToPb = CustomerCimToProto()

    override val validationInfo = listOf(
        /************ IEC61968 COMMON ************/
        ValidationInfo(Organisation(), { fillFieldsCommon(it) }, { addFromPb(csToPb.toPb(it)) }),

        /************ IEC61968 CUSTOMERS ************/
        ValidationInfo(Customer(), { fillFields(it) }, { addFromPb(csToPb.toPb(it)) }),
        ValidationInfo(CustomerAgreement(), { fillFields(it) }, { addFromPb(csToPb.toPb(it)) }),
        ValidationInfo(PricingStructure(), { fillFields(it) }, { addFromPb(csToPb.toPb(it)) }),
        ValidationInfo(Tariff(), { fillFields(it) }, { addFromPb(csToPb.toPb(it)) })
    )

    override val excludedTables =
        super.excludedTables + setOf(
            // Excluded associations
            TableCustomerAgreementsPricingStructures::class,
            TablePricingStructuresTariffs::class,
        )

}
