/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61968.customers

import com.zepben.evolve.cim.iec61968.common.OrganisationRole
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.getByMRID
import com.zepben.evolve.services.common.extensions.validateReference

/**
 * Organisation receiving services from service supplier.
 *
 * @property kind Kind of customer.
 * @property numEndDevices The number of end devices associated with this customer.
 */
class Customer @JvmOverloads constructor(mRID: String = "") : OrganisationRole(mRID) {

    var kind: CustomerKind = CustomerKind.UNKNOWN
    private var _customerAgreements: MutableList<CustomerAgreement>? = null

    var numEndDevices: Int? = null

    /**
     * @return True if this [Customer] has at least 1 EndDevice associated with it, false otherwise.
     */
    fun hasEndDevices(): Boolean = numEndDevices?.let { it > 0 } ?: false

    /**
     * All agreements of this customer. The returned collection is read only.
     */
    val agreements: Collection<CustomerAgreement> get() = _customerAgreements.asUnmodifiable()

    /**
     * Get the number of entries in the [CustomerAgreement] collection.
     */
    fun numAgreements(): Int = _customerAgreements?.size ?: 0

    /**
     * All agreements of this customer.
     *
     * @param mRID the mRID of the required [CustomerAgreement]
     * @return The [CustomerAgreement] with the specified [mRID] if it exists, otherwise null
     */
    fun getAgreement(mRID: String): CustomerAgreement? = _customerAgreements?.getByMRID(mRID)

    fun addAgreement(customerAgreement: CustomerAgreement): Customer {
        if (validateReference(customerAgreement, ::getAgreement, "A CustomerAgreement"))
            return this

        _customerAgreements = _customerAgreements ?: mutableListOf()
        _customerAgreements!!.add(customerAgreement)

        return this
    }

    fun removeAgreement(customerAgreement: CustomerAgreement?): Boolean {
        val ret = _customerAgreements?.remove(customerAgreement) == true
        if (_customerAgreements.isNullOrEmpty()) _customerAgreements = null
        return ret
    }

    fun clearAgreements(): Customer {
        _customerAgreements = null
        return this
    }
}
