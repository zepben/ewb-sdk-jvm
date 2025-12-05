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

/**
 * Agreement between the customer and the service supplier to pay for service at a specific service location. It
 * records certain billing information about the type of service provided at the service location and is used
 * during charge creation to determine the type of service.
 *
 * @property customer Customer for this agreement.
 */
class CustomerAgreement(mRID: String) : Agreement(mRID) {

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
    fun numPricingStructures(): Int = _pricingStructures?.size ?: 0

    /**
     * All pricing structures applicable to this customer agreement.
     *
     * @param mRID the mRID of the required [PricingStructure]
     * @return The [PricingStructure] with the specified [mRID] if it exists, otherwise null
     */
    fun getPricingStructure(mRID: String): PricingStructure? = _pricingStructures?.getByMRID(mRID)

    /**
     * Add a [PricingStructure] to this [CustomerAgreement].
     *
     * @param pricingStructure The [PricingStructure] to add.
     * @return This [CustomerAgreement] for fluent use.
     */
    fun addPricingStructure(pricingStructure: PricingStructure): CustomerAgreement {
        if (validateReference(pricingStructure, ::getPricingStructure, "A PricingStructure"))
            return this

        _pricingStructures = _pricingStructures ?: mutableListOf()
        _pricingStructures!!.add(pricingStructure)

        return this
    }

    /**
     * Remove a [PricingStructure] from this [CustomerAgreement].
     *
     * @param pricingStructure The [PricingStructure] to remove.
     * @return true if [pricingStructure] is removed from the collection.
     */
    fun removePricingStructure(pricingStructure: PricingStructure): Boolean {
        val ret = _pricingStructures?.remove(pricingStructure) == true
        if (_pricingStructures.isNullOrEmpty()) _pricingStructures = null
        return ret
    }

    /**
     * Clear all [PricingStructure]'s from this [CustomerAgreement].
     *
     * @return This [CustomerAgreement] for fluent use.
     */
    fun clearPricingStructures(): CustomerAgreement {
        _pricingStructures = null
        return this
    }

}
