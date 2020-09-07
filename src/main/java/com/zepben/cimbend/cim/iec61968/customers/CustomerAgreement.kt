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

import com.zepben.cimbend.cim.iec61968.common.Agreement
import com.zepben.cimbend.common.extensions.asUnmodifiable
import com.zepben.cimbend.common.extensions.getByMRID
import com.zepben.cimbend.common.extensions.validateReference

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
    val pricingStructures: Collection<PricingStructure> get() = _pricingStructures.asUnmodifiable()

    /**
     * Get the number of entries in the [PricingStructure] collection.
     */
    fun numPricingStructures() = _pricingStructures?.size ?: 0

    /**
     * All pricing structures applicable to this customer agreement.
     *
     * @param mRID the mRID of the required [PricingStructure]
     * @return The [PricingStructure] with the specified [mRID] if it exists, otherwise null
     */
    fun getPricingStructure(mRID: String) = _pricingStructures?.getByMRID(mRID)

    fun addPricingStructure(pricingStructure: PricingStructure): CustomerAgreement {
        if (validateReference(pricingStructure, ::getPricingStructure, "A PricingStructure"))
            return this

        _pricingStructures = _pricingStructures ?: mutableListOf()
        _pricingStructures!!.add(pricingStructure)

        return this
    }

    fun removePricingStructure(pricingStructure: PricingStructure?): Boolean {
        val ret = _pricingStructures?.remove(pricingStructure) == true
        if (_pricingStructures.isNullOrEmpty()) _pricingStructures = null
        return ret
    }

    fun clearPricingStructures(): CustomerAgreement {
        _pricingStructures = null
        return this
    }
}
