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

/**
 * Kind of customer.
 *
 * @property UNKNOWN Default
 * @property commercialIndustrial Commercial industrial customer.
 * @property energyServiceScheduler Customer as energy service scheduler.
 * @property energyServiceSupplier Customer as energy service supplier.
 * @property enterprise --- Missing form CIM ---
 * @property internalUse Internal use customer.
 * @property other Other kind of customer.
 * @property pumpingLoad Pumping load customer.
 * @property regionalOperator --- Missing form CIM ---
 * @property residential Residential customer.
 * @property residentialAndCommercial Residential and commercial customer.
 * @property residentialAndStreetlight Residential and streetlight customer.
 * @property residentialFarmService Residential farm service customer.
 * @property residentialStreetlightOthers Residential streetlight or other related customer.
 * @property subsidiary --- Missing form CIM ---
 */
@Suppress("EnumEntryName")
enum class CustomerKind {

    UNKNOWN,
    commercialIndustrial,
    energyServiceScheduler,
    energyServiceSupplier,
    enterprise,
    internalUse,
    other,
    pumpingLoad,
    regionalOperator,
    residential,
    residentialAndCommercial,
    residentialAndStreetlight,
    residentialFarmService,
    residentialStreetlightOthers,
    subsidiary,
    windMachine
}
