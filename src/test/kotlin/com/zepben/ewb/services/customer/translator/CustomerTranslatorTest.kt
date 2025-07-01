/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.customer.translator

import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61968.customers.Customer
import com.zepben.ewb.cim.iec61968.customers.CustomerAgreement
import com.zepben.ewb.cim.iec61968.customers.PricingStructure
import com.zepben.ewb.cim.iec61968.customers.Tariff
import com.zepben.ewb.database.sqlite.cim.customer.CustomerDatabaseTables
import com.zepben.ewb.database.sqlite.cim.tables.associations.TableCustomerAgreementsPricingStructures
import com.zepben.ewb.database.sqlite.cim.tables.associations.TablePricingStructuresTariffs
import com.zepben.ewb.services.common.testdata.fillFieldsCommon
import com.zepben.ewb.services.common.translator.TranslatorTestBase
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.ewb.services.customer.CustomerServiceComparator
import com.zepben.ewb.services.customer.testdata.fillFields

internal class CustomerTranslatorTest : TranslatorTestBase<CustomerService>(
    ::CustomerService,
    CustomerServiceComparator(),
    CustomerDatabaseTables(),
    CustomerService::addFromPb,
    ::customerIdentifiedObject
) {

    private val csToPb = CustomerCimToProto()

    override val validationInfo = listOf(
        // ###################
        // # IEC61968 Common #
        // ###################

        ValidationInfo(Organisation(), { fillFieldsCommon(it) }, { addFromPb(csToPb.toPb(it)) }),

        // ######################
        // # IEC61968 Customers #
        // ######################

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
