/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.customers

import com.zepben.ewb.cim.iec61968.common.Agreement
import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.validateReference
import com.zepben.ewb.testing.MRIDListWrapper

/**
 * Agreement between the customer and the service supplier to pay for service at a specific service location. It
 * records certain billing information about the type of service provided at the service location and is used
 * during charge creation to determine the type of service.
 *
 * @property customer Customer for this agreement.
 */
class CustomerAgreement @JvmOverloads constructor(mRID: String = "") : Agreement(mRID) {

    var customer: Customer? = null
        set(value) {
            field =
                if (field == null || field === value) value else throw IllegalStateException("customer has already been set to $field. Cannot set this field again")
        }

    private var _pricingStructures: MutableList<PricingStructure>? = null

    /**
     * All pricing structures applicable to this customer agreement. The returned collection is read only.
     */
    val pricingStructures: MRIDListWrapper<PricingStructure>
        get() = MRIDListWrapper(
            getter = { _pricingStructures },
            setter = { _pricingStructures = it })

    @Deprecated("BOILERPLATE: Use pricingStructures.size instead")
    fun numPricingStructures(): Int = pricingStructures.size

    @Deprecated("BOILERPLATE: Use agreements.getByMRID(mRID) instead")
    fun getPricingStructure(mRID: String): PricingStructure? = pricingStructures.getByMRID(mRID)

    fun addPricingStructure(pricingStructure: PricingStructure): CustomerAgreement {
        if (validateReference(pricingStructure, ::getPricingStructure, "A PricingStructure"))
            return this

        _pricingStructures = _pricingStructures ?: mutableListOf()
        _pricingStructures!!.add(pricingStructure)

        return this
    }

    fun removePricingStructure(pricingStructure: PricingStructure): Boolean {
        val ret = _pricingStructures?.remove(pricingStructure) == true
        if (_pricingStructures.isNullOrEmpty()) _pricingStructures = null
        return ret
    }

    fun clearPricingStructures(): CustomerAgreement {
        _pricingStructures = null
        return this
    }
}
