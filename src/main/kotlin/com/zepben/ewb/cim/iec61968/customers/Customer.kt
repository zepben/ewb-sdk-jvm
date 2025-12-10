/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.customers

import com.zepben.ewb.cim.iec61968.common.OrganisationRole
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.validateReference
import com.zepben.ewb.testing.MRIDListWrapper

/**
 * Organisation receiving services from service supplier.
 *
 * @property kind Kind of customer.
 * @property numEndDevices The number of end devices associated with this customer.
 * @property specialNeed A special service need such as life support, hospitals, etc.
 */
class Customer @JvmOverloads constructor(mRID: String = "") : OrganisationRole(mRID) {

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
    val agreements: MRIDListWrapper<CustomerAgreement>
        get() = MRIDListWrapper(
            getter = { _customerAgreements },
            setter = { _customerAgreements = it })

    @Deprecated("BOILERPLATE: Use agreements.size instead")
    fun numAgreements(): Int = agreements.size

    /**
     * All agreements of this customer.
     *
     * @param mRID the mRID of the required [CustomerAgreement]
     * @return The [CustomerAgreement] with the specified [mRID] if it exists, otherwise null
     */
    fun getAgreement(mRID: String): CustomerAgreement? = _customerAgreements?.getByMRID(mRID)

    /**
     *  Add a [CustomerAgreement] to this [Customer].
     *
     * @param customerAgreement The [CustomerAgreement] to add.
     * @return this [Customer].
     */
    fun addAgreement(customerAgreement: CustomerAgreement): Customer {
        if (validateReference(customerAgreement, ::getAgreement, "A CustomerAgreement"))
            return this

        _customerAgreements = _customerAgreements ?: mutableListOf()
        _customerAgreements!!.add(customerAgreement)

        return this
    }

    @Deprecated("BOILERPLATE: Use customerAgreements.remove(customerAgreement) instead")
    fun removeAgreement(customerAgreement: CustomerAgreement): Boolean = agreements.remove(customerAgreement)

    /**
     * Clear all [CustomerAgreement]'s from this [Customer].
     * @return this [Customer].
     */
    fun clearAgreements(): Customer {
        _customerAgreements = null
        return this
    }
}
