/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61968.customers

import com.zepben.evolve.cim.iec61968.common.Document
import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.getByMRID
import com.zepben.evolve.services.common.extensions.validateReference

/**
 * Grouping of pricing components and prices used in the creation of customer charges and the eligibility
 * criteria under which these terms may be offered to a customer. The reasons for grouping include state,
 * customer classification, site characteristics, classification (i.e. fee price structure, deposit price
 * structure, electric service price structure, etc.) and accounting requirements.
 */
class PricingStructure @JvmOverloads constructor(mRID: String = "") : Document(mRID) {

    private var _tariffs: MutableList<Tariff>? = null

    /**
     * All tariffs used by this pricing structure. The returned collection is read only
     */
    val tariffs: Collection<Tariff> get() = _tariffs.asUnmodifiable()

    /**
     * Get the number of entries in the [Tariff] collection.
     */
    fun numTariffs() = _tariffs?.size ?: 0

    /**
     * All tariffs used by this pricing structure.
     *
     * @param mRID the mRID of the required [Tariff]
     * @return The [Tariff] with the specified [mRID] if it exists, otherwise null
     */
    fun getTariff(mRID: String) = _tariffs?.getByMRID(mRID)

    fun addTariff(tariff: Tariff): PricingStructure {
        if (validateReference(tariff, ::getTariff, "A Tariff"))
            return this

        _tariffs = _tariffs ?: mutableListOf()
        _tariffs!!.add(tariff)

        return this
    }

    fun removeTariff(tariff: Tariff?): Boolean {
        val ret = _tariffs?.remove(tariff) == true
        if (_tariffs.isNullOrEmpty()) _tariffs = null
        return ret
    }

    fun clearTariffs(): PricingStructure {
        _tariffs = null
        return this
    }
}
