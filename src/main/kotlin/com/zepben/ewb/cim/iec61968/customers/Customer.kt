/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.customers

import com.zepben.ewb.boilerplate.LazyMridList
import com.zepben.ewb.cim.iec61968.common.OrganisationRole

/**
 * Organisation receiving services from service supplier.
 *
 * @property kind Kind of customer.
 * @property numEndDevices The number of end devices associated with this customer.
 * @property specialNeed A special service need such as life support, hospitals, etc.
 */
class Customer(mRID: String) : OrganisationRole(mRID) {

    var kind: CustomerKind = CustomerKind.UNKNOWN
    var numEndDevices: Int? = null
    var specialNeed: String? = null

    private var _customerAgreements: MutableList<CustomerAgreement>? = null

    /**
     * @return True if this [Customer] has at least 1 EndDevice associated with it, false otherwise.
     */
    fun hasEndDevices(): Boolean = numEndDevices?.let { it > 0 } ?: false

    /**
     * All agreements of this customer. The returned collection is read only.
     */
    val agreements: LazyMridList<CustomerAgreement> get() = LazyMridList(
        getter = { _customerAgreements },
        setter = { _customerAgreements = it },
        owner = { this },
        elementDescription = "A CustomerAgreement"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region agreements boilerplate

    @Deprecated(
        message = "Use agreements.size instead.",
        replaceWith = ReplaceWith("agreements.size")
    )
    fun numAgreements(): Int = agreements.size

    @Deprecated(
        message = "Use agreements.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("agreements.getByMRID(mRID)")
    )
    fun getAgreement(mRID: String): CustomerAgreement? = agreements.getByMrid(mRID)

    @Deprecated(
        message = "Use agreements.add(customerAgreement) instead.",
        replaceWith = ReplaceWith("also { it.agreements.add(customerAgreement) }")
    )
    fun addAgreement(customerAgreement: CustomerAgreement): Customer {
        agreements.add(customerAgreement)
        return this
    }

    @Deprecated(
        message = "Use agreements.remove(customerAgreement) instead.",
        replaceWith = ReplaceWith("agreements.remove(customerAgreement)")
    )
    fun removeAgreement(customerAgreement: CustomerAgreement): Boolean = agreements.remove(customerAgreement)

    @Deprecated(
        message = "Use agreements.clear() instead.",
        replaceWith = ReplaceWith("agreements.clear()")
    )
    fun clearAgreements(): Customer {
        agreements.clear()
        return this
    }

    // endregion

    // endregion
}
