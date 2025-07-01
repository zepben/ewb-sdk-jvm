/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.customers

/**
 * Kind of customer.
 */
@Suppress("EnumEntryName")
enum class CustomerKind {

    /**
     * Default.
     */
    UNKNOWN,

    /**
     * Commercial industrial customer.
     */
    commercialIndustrial,

    /**
     * Customer as energy service scheduler.
     */
    energyServiceScheduler,

    /**
     * Customer as energy service supplier.
     */
    energyServiceSupplier,

    /**
     * --- Missing form CIM ---
     */
    enterprise,

    /**
     * Internal use customer.
     */
    internalUse,

    /**
     * Other kind of customer.
     */
    other,

    /**
     * Pumping load customer.
     */
    pumpingLoad,

    /**
     * --- Missing form CIM ---
     */
    regionalOperator,

    /**
     * Residential customer.
     */
    residential,

    /**
     * Residential and commercial customer.
     */
    residentialAndCommercial,

    /**
     * Residential and streetlight customer.
     */
    residentialAndStreetlight,

    /**
     * Residential farm service customer.
     */
    residentialFarmService,

    /**
     * Residential streetlight or other related customer.
     */
    residentialStreetlightOthers,

    /**
     * --- Missing form CIM ---
     */
    subsidiary,

    /**
     * Wind machine customer.
     */
    windMachine

}
