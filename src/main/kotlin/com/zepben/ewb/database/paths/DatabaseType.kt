/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.paths

/**
 * Describes the type of database being resolved.
 *
 * @property perDate Indicates if there is a file per date, or a single file for all dates.
 * @property fileDescriptor The suffix of the filename for date based files, otherwise the filename (without extension).
 */
enum class DatabaseType(
    val perDate: Boolean,
    val fileDescriptor: String,
) {

    // ###################
    // # DAILY DATABASES #
    // ###################

    /**
     * The daily customer database.
     */
    CUSTOMER(perDate = true, fileDescriptor = "customers"),

    /**
     * The daily diagram database.
     */
    DIAGRAM(perDate = true, fileDescriptor = "diagrams"),

    /**
     * The daily measurement database.
     */
    MEASUREMENT(perDate = true, fileDescriptor = "measurements"),

    /**
     * The daily network model database.
     */
    NETWORK_MODEL(perDate = true, fileDescriptor = "network-model"),

    /**
     * The daily tile cache database.
     */
    TILE_CACHE(perDate = true, fileDescriptor = "tile-cache"),

    /**
     * The daily energy reading database.
     */
    ENERGY_READING(perDate = true, fileDescriptor = "load-readings"),

    // #####################
    // # OVERALL DATABASES #
    // #####################

    /**
     * The overall energy readings index database.
     */
    ENERGY_READINGS_INDEX(perDate = false, fileDescriptor = "load-readings-index"),

    /**
     * The overall load aggregator meters by date database.
     */
    LOAD_AGGREGATOR_METERS_BY_DATE(perDate = false, fileDescriptor = "load-aggregator-mbd"),

    /**
     * The overall weather reading database.
     */
    WEATHER_READING(perDate = false, fileDescriptor = "weather-readings"),

    /**
     * The overall results cache database.
     */
    RESULTS_CACHE(perDate = false, fileDescriptor = "results-cache"),

    /**
     * The overall aggregation index database.
     */
    @Deprecated("Remove when (if) CPPAL stop using the old SQLite load databases. Search for other copies of this message across repos when removing.")
    AGGREGATION_INDEX(perDate = false, fileDescriptor = "aggregation-index"),

}
