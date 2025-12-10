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

    @Deprecated("BOILERPLATE: Use agreements.getByMRID(mRID) instead")
    fun getAgreement(mRID: String): CustomerAgreement? = agreements.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use customerAgreements.add(customerAgreement) instead")
    fun addAgreement(customerAgreement: CustomerAgreement): Customer {
        agreements.add(customerAgreement)
        return this
    }

    @Deprecated("BOILERPLATE: Use customerAgreements.remove(customerAgreement) instead")
    fun removeAgreement(customerAgreement: CustomerAgreement): Boolean = agreements.remove(customerAgreement)

    @Deprecated("BOILERPLATE: Use customerAgreements.clear() instead")
    fun clearAgreements(): Customer {
        agreements.clear()
        return this
    }
}
