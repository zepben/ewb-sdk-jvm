/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.customer.testdata

import com.zepben.ewb.cim.iec61968.common.Agreement
import com.zepben.ewb.cim.iec61968.common.Document
import com.zepben.ewb.cim.iec61968.common.OrganisationRole
import com.zepben.ewb.cim.iec61968.customers.*
import com.zepben.ewb.cim.iec61970.base.domain.DateTimeInterval
import com.zepben.ewb.services.common.testdata.fillFieldsCommon
import com.zepben.ewb.services.customer.CustomerService
import java.time.Instant

// ###################
// # IEC61968 Common #
// ###################

fun Agreement.fillFields(service: CustomerService, includeRuntime: Boolean = true): Agreement {
    (this as Document).fillFieldsCommon(service, includeRuntime)

    validityInterval = DateTimeInterval(start = Instant.ofEpochSecond(1), end = Instant.ofEpochSecond(2))

    return this
}

// ######################
// # IEC61968 Customers #
// ######################

fun Customer.fillFields(service: CustomerService, includeRuntime: Boolean = true): Customer {
    (this as OrganisationRole).fillFieldsCommon(service, includeRuntime)

    kind = CustomerKind.enterprise
    numEndDevices = 1
    specialNeed = "my need"

    for (i in 0..1) {
        addAgreement(CustomerAgreement().also {
            it.customer = this
            service.add(it)
        })
    }

    return this
}

fun CustomerAgreement.fillFields(service: CustomerService, includeRuntime: Boolean = true): CustomerAgreement {
    (this as Agreement).fillFields(service, includeRuntime)

    customer = Customer().also {
        it.addAgreement(this)
        service.add(it)
    }

    for (i in 0..1)
        addPricingStructure(PricingStructure().also { service.add(it) })

    validityInterval = DateTimeInterval(Instant.ofEpochSecond(1), Instant.ofEpochSecond(2))

    return this
}

fun PricingStructure.fillFields(service: CustomerService, includeRuntime: Boolean = true): PricingStructure {
    (this as Document).fillFieldsCommon(service, includeRuntime)

    for (i in 0..1)
        addTariff(Tariff().also { service.add(it) })

    return this
}

fun Tariff.fillFields(service: CustomerService, includeRuntime: Boolean = true): Tariff {
    (this as Document).fillFieldsCommon(service, includeRuntime)
    return this
}
