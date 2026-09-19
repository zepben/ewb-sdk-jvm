/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.paths

import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.*

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
     * The daily variant database.
     */
    VARIANT(perDate = true, fileDescriptor = "variants"),

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

/**
 * The contents contained within the variant.
 *
 * @property subDirectory Determines which subdirectory in the variant home this database resolves to.
 * @property types Specifies which database types are contained in the subdirectory.
 */
enum class VariantContents(val subDirectory: String, val types: Set<DatabaseType>) {

    /**
     * Contains the target of [ObjectDeletion]s and [ObjectReverseModification]s. These are called 'original' because
     * the target will be the original object from the base model that the ChangeSet was derived from.
     */
    DELETIONS_REVERSEMODIFICATIONS("original", setOf(DatabaseType.NETWORK_MODEL, DatabaseType.CUSTOMER, DatabaseType.DIAGRAM, DatabaseType.MEASUREMENT)),

    /**
     * Contains the target of [ObjectCreation]s and [ObjectModification]s. These are called 'new' as the targets contain
     * only updates or additions to the base model.
     */
    CREATIONS_MODIFICATIONS("new", setOf(DatabaseType.NETWORK_MODEL, DatabaseType.CUSTOMER, DatabaseType.DIAGRAM, DatabaseType.MEASUREMENT)),

    /**
     * Contains the [ChangeSet] and its [ChangeSetMember]s.
     */
    CHANGESET("", setOf(DatabaseType.VARIANT))

}
