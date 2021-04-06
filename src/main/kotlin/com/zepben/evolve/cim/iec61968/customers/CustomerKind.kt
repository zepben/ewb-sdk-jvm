/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61968.customers

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
