/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.filepaths

enum class PathType(val perDate: Boolean, val fileDescriptor: String)
{
    CUSTOMERS(perDate = true, fileDescriptor="customers"),
    DIAGRAMS(perDate = true, fileDescriptor="diagrams"),
    MEASUREMENTS(perDate = true, fileDescriptor="measurements"),
    NETWORK_MODEL(perDate = true, fileDescriptor="network-model"),
    TILE_CACHE(perDate = true, fileDescriptor="tile-cache"),
    ENERGY_READINGS(perDate = true, fileDescriptor="load-readings"),

    ENERGY_READINGS_INDEX(perDate = false, fileDescriptor="load-readings-index"),
    LOAD_AGGREGATOR_METERS_BY_DATE(perDate = false, fileDescriptor="load-aggregator-mbd"),
    WEATHER_READINGS(perDate = false, fileDescriptor="weather-readings"),
    RESULTS_CACHE(perDate = false, fileDescriptor="results_cache"),
}