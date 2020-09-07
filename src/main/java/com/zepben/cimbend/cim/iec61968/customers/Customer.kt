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
package com.zepben.cimbend.cim.iec61968.customers

import com.zepben.cimbend.cim.iec61968.common.OrganisationRole
import com.zepben.cimbend.common.extensions.asUnmodifiable
import com.zepben.cimbend.common.extensions.getByMRID
import com.zepben.cimbend.common.extensions.validateReference

/**
 * Organisation receiving services from service supplier.
 *
 * @property kind Kind of customer.
 * @property numEndDevices The number of end devices associated with this customer.
 */
class Customer @JvmOverloads constructor(mRID: String = "") : OrganisationRole(mRID) {

    var kind: CustomerKind = CustomerKind.UNKNOWN
    private var _customerAgreements: MutableList<CustomerAgreement>? = null

    var numEndDevices: Int = 0

    /**
     * @return True if this [Customer] has at least 1 EndDevice associated with it, false otherwise.
     */
    fun hasEndDevices() = numEndDevices > 0

    /**
     * All agreements of this customer. The returned collection is read only.
     */
    val agreements: Collection<CustomerAgreement> get() = _customerAgreements.asUnmodifiable()

    /**
     * Get the number of entries in the [CustomerAgreement] collection.
     */
    fun numAgreements() = _customerAgreements?.size ?: 0

    /**
     * All agreements of this customer.
     *
     * @param mRID the mRID of the required [CustomerAgreement]
     * @return The [CustomerAgreement] with the specified [mRID] if it exists, otherwise null
     */
    fun getAgreement(mRID: String) = _customerAgreements?.getByMRID(mRID)

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
