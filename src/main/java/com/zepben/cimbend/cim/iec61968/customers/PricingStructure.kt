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

import com.zepben.cimbend.cim.iec61968.common.Document
import com.zepben.cimbend.common.extensions.asUnmodifiable
import com.zepben.cimbend.common.extensions.getByMRID
import com.zepben.cimbend.common.extensions.validateReference

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
