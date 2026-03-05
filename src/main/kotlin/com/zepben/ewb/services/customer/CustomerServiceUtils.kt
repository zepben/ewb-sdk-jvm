/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


@file:JvmName("CustomerServiceUtils")

package com.zepben.ewb.services.customer

import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61968.customers.Customer
import com.zepben.ewb.cim.iec61968.customers.CustomerAgreement
import com.zepben.ewb.cim.iec61968.customers.PricingStructure
import com.zepben.ewb.cim.iec61968.customers.Tariff
import com.zepben.ewb.cim.iec61970.base.core.Identifiable
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject

/**
 * A function that provides an exhaustive `when` style statement for all [Identifiable] leaf types supported by
 * the [CustomerService]. If the provided [identifiable] is not supported by the service the [isOther] handler
 * is invoked which by default will throw an [IllegalArgumentException]
 *
 * By using this function, you acknowledge that if any new types are added to the customer service, and thus this
 * function, it will cause a compilation error when updating to the new version. This should reduce errors due to
 * missed handling of new types introduced to the model. As this is intended behaviour it generally will not be
 * considered a breaking change in terms of semantic versioning of this library.
 *
 * If it is not critical that all types within the service are always handled, it is recommended to use a typical
 * `when` statement (Kotlin) or if-else branch (Java) and update new cases as required without breaking your code.
 *
 * @param identifiable The identified object to handle.
 * @param isCustomer Handler when the [identifiable] is a [Customer]
 * @param isCustomerAgreement Handler when the [identifiable] is a [CustomerAgreement]
 * @param isOrganisation Handler when the [identifiable] is a [Organisation]
 * @param isPricingStructure Handler when the [identifiable] is a [PricingStructure]
 * @param isTariff Handler when the [identifiable] is a [Tariff]
 * @param isOther Handler when the [identifiable] is not supported by the [CustomerService].
 */
@JvmOverloads
inline fun <R> whenCustomerServiceObject(
    identifiable: Identifiable,
    isCustomer: (Customer) -> R,
    isCustomerAgreement: (CustomerAgreement) -> R,
    isOrganisation: (Organisation) -> R,
    isPricingStructure: (PricingStructure) -> R,
    isTariff: (Tariff) -> R,
    isOther: (Identifiable) -> R = { idObj: Identifiable ->
        throw IllegalArgumentException("Identified object type ${idObj::class} is not supported by the customer service")
    }
): R = when (identifiable) {
    is Customer -> isCustomer(identifiable)
    is CustomerAgreement -> isCustomerAgreement(identifiable)
    is Organisation -> isOrganisation(identifiable)
    is PricingStructure -> isPricingStructure(identifiable)
    is Tariff -> isTariff(identifiable)
    else -> isOther(identifiable)
}
