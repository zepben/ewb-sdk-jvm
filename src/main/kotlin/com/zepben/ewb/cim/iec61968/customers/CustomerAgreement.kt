/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.customers

import com.zepben.ewb.boilerplate.LazyMridList
import com.zepben.ewb.cim.iec61968.common.Agreement

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
    val pricingStructures: LazyMridList<PricingStructure> get() = LazyMridList(
        getter = { _pricingStructures },
        setter = { _pricingStructures = it },
        owner = { this },
        elementDescription = "A PricingStructure"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region pricingStructures boilerplate

    @Deprecated(
        message = "Use pricingStructures.size instead.",
        replaceWith = ReplaceWith("pricingStructures.size")
    )
    fun numPricingStructures(): Int = pricingStructures.size

    @Deprecated(
        message = "Use pricingStructures.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("pricingStructures.getByMRID(mRID)")
    )
    fun getPricingStructure(mRID: String): PricingStructure? = pricingStructures.getByMrid(mRID)

    @Deprecated(
        message = "Use pricingStructures.add(pricingStructure) instead.",
        replaceWith = ReplaceWith("also { it.pricingStructures.add(pricingStructure) }")
    )
    fun addPricingStructure(pricingStructure: PricingStructure): CustomerAgreement {
        pricingStructures.add(pricingStructure)
        return this
    }

    @Deprecated(
        message = "Use pricingStructures.remove(pricingStructure) instead.",
        replaceWith = ReplaceWith("pricingStructures.remove(pricingStructure)")
    )
    fun removePricingStructure(pricingStructure: PricingStructure): Boolean = pricingStructures.remove(pricingStructure)

    @Deprecated(
        message = "Use pricingStructures.clear() instead.",
        replaceWith = ReplaceWith("pricingStructures.clear()")
    )
    fun clearPricingStructures(): CustomerAgreement {
        pricingStructures.clear()
        return this
    }

    // endregion

    // endregion
}
