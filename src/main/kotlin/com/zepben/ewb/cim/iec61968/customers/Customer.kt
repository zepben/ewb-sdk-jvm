/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.customers

import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.cim.iec61968.common.OrganisationRole
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.validateReference

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
    val agreements: MridCollection<CustomerAgreement> get() = LazyMridList(
        getter = { _customerAgreements },
        setter = { _customerAgreements = it },
        owner = this,
        elementDescription = "A CustomerAgreement"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region agreements boilerplate

    /**
     * Get the number of entries in the [CustomerAgreement] collection.
     */
    @Deprecated(
        message = "Use agreements.size instead.",
        replaceWith = ReplaceWith("agreements.size")
    )
    fun numAgreements(): Int = _customerAgreements?.size ?: 0

    /**
     * All agreements of this customer.
     *
     * @param mRID the mRID of the required [CustomerAgreement]
     * @return The [CustomerAgreement] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use agreements.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("agreements.getByMRID(mRID)")
    )
    fun getAgreement(mRID: String): CustomerAgreement? = _customerAgreements?.getByMRID(mRID)

    /**
     *  Add a [CustomerAgreement] to this [Customer].
     *
     * @param customerAgreement The [CustomerAgreement] to add.
     * @return this [Customer].
     */
    @Deprecated(
        message = "Use agreements.add(customerAgreement) instead.",
        replaceWith = ReplaceWith("also { it.agreements.add(customerAgreement) }")
    )
    fun addAgreement(customerAgreement: CustomerAgreement): Customer {
        if (validateReference(customerAgreement, ::getAgreement, "A CustomerAgreement"))
            return this

        _customerAgreements = _customerAgreements ?: mutableListOf()
        _customerAgreements!!.add(customerAgreement)

        return this
    }

    /**
     * Remove a customerAgreement from this [Customer].
     *
     * @param customerAgreement The [CustomerAgreement] to remove.
     * @return true if [customerAgreement] is removed from the collection.
     */
    @Deprecated(
        message = "Use agreements.remove(customerAgreement) instead.",
        replaceWith = ReplaceWith("agreements.remove(customerAgreement)")
    )
    fun removeAgreement(customerAgreement: CustomerAgreement): Boolean {
        val ret = _customerAgreements?.remove(customerAgreement) == true
        if (_customerAgreements.isNullOrEmpty()) _customerAgreements = null
        return ret
    }

    /**
     * Clear all [CustomerAgreement]'s from this [Customer].
     * @return this [Customer].
     */
    @Deprecated(
        message = "Use agreements.clear() instead.",
        replaceWith = ReplaceWith("agreements.clear()")
    )
    fun clearAgreements(): Customer {
        _customerAgreements = null
        return this
    }

    // endregion

    // endregion
}
