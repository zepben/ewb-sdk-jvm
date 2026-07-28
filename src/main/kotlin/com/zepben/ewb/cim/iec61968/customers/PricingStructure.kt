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
import com.zepben.ewb.cim.iec61968.common.Document

/**
 * Grouping of pricing components and prices used in the creation of customer charges and the eligibility criteria under which these terms may be offered to a customer. The reasons for grouping include state, customer classification, site characteristics, classification (i.e. fee price structure, deposit price structure, electric service price structure, etc.) and accounting requirements.
 *
 * @property code Unique user-allocated key for this pricing structure, used by company representatives to identify the correct price structure for allocating to a customer. For rate schedules it is often prefixed by a state code.
 * @property tariffs All tariffs used by this pricing structure.
 */
class PricingStructure(mRID: String) : Document(mRID) {

    private var _tariffs: MutableList<Tariff>? = null

    var code: String? = null

    /**
     * All tariffs used by this pricing structure. The returned collection is read only
     */
    val tariffs: MridCollection<Tariff> get() = LazyMridList(
        getter = { _tariffs },
        setter = { _tariffs = it },
        owner = this,
        elementDescription = "A Tariff"
    )

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region tariffs boilerplate

    @Deprecated(
        message = "Use tariffs.size instead.",
        replaceWith = ReplaceWith("tariffs.size")
    )
    fun numTariffs(): Int = tariffs.size

    @Deprecated(
        message = "Use tariffs.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("tariffs.getByMRID(mRID)")
    )
    fun getTariff(mRID: String): Tariff? = tariffs.getByMrid(mRID)

    @Deprecated(
        message = "Use tariffs.add(tariff) instead.",
        replaceWith = ReplaceWith("also { it.tariffs.add(tariff) }")
    )
    fun addTariff(tariff: Tariff): PricingStructure {
        tariffs.add(tariff)
        return this
    }

    @Deprecated(
        message = "Use tariffs.remove(tariff) instead.",
        replaceWith = ReplaceWith("tariffs.remove(tariff)")
    )
    fun removeTariff(tariff: Tariff): Boolean = tariffs.remove(tariff)

    @Deprecated(
        message = "Use tariffs.clear() instead.",
        replaceWith = ReplaceWith("tariffs.clear()")
    )
    fun clearTariffs(): PricingStructure {
        tariffs.clear()
        return this
    }

    // endregion

    // endregion
}
