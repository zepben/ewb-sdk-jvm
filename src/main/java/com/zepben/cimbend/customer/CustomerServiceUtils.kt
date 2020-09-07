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


@file:JvmName("CustomerServiceUtils")

package com.zepben.cimbend.customer

import com.zepben.cimbend.cim.iec61968.common.Organisation
import com.zepben.cimbend.cim.iec61968.customers.Customer
import com.zepben.cimbend.cim.iec61968.customers.CustomerAgreement
import com.zepben.cimbend.cim.iec61968.customers.PricingStructure
import com.zepben.cimbend.cim.iec61968.customers.Tariff
import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject

/**
 * A function that provides an exhaustive `when` style statement for all [IdentifiedObject] leaf types supported by
 * the [CustomerService]. If the provided [identifiedObject] is not supported by the service the [isOther] handler
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
 * @param identifiedObject The identified object to handle.
 * @param isCustomer Handler when the [identifiedObject] is a [Customer]
 * @param isCustomerAgreement Handler when the [identifiedObject] is a [CustomerAgreement]
 * @param isOrganisation Handler when the [identifiedObject] is a [Organisation]
 * @param isPricingStructure Handler when the [identifiedObject] is a [PricingStructure]
 * @param isTariff Handler when the [identifiedObject] is a [Tariff]
 * @param isOther Handler when the [identifiedObject] is not supported by the [CustomerService].
 */
@JvmOverloads
inline fun <R> whenCustomerServiceObject(
    identifiedObject: IdentifiedObject,
    crossinline isCustomer: (Customer) -> R,
    crossinline isCustomerAgreement: (CustomerAgreement) -> R,
    crossinline isOrganisation: (Organisation) -> R,
    crossinline isPricingStructure: (PricingStructure) -> R,
    crossinline isTariff: (Tariff) -> R,
    crossinline isOther: (IdentifiedObject) -> R = { idObj: IdentifiedObject ->
        throw IllegalArgumentException("Identified object type ${idObj::class} is not supported by the customer service")
    }
): R = when (identifiedObject) {
    is Customer -> isCustomer(identifiedObject)
    is CustomerAgreement -> isCustomerAgreement(identifiedObject)
    is Organisation -> isOrganisation(identifiedObject)
    is PricingStructure -> isPricingStructure(identifiedObject)
    is Tariff -> isTariff(identifiedObject)
    else -> isOther(identifiedObject)
}
