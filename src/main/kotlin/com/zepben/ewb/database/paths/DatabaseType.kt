/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.paths

enum class DatabaseType(
    val perDate: Boolean,
    val fileDescriptor: String
) {

    CUSTOMER(perDate = true, fileDescriptor = "customers"),
    DIAGRAM(perDate = true, fileDescriptor = "diagrams"),
    MEASUREMENT(perDate = true, fileDescriptor = "measurements"),
    NETWORK_MODEL(perDate = true, fileDescriptor = "network-model"),
    VARIANT(perDate = true, fileDescriptor = "variants"),
    TILE_CACHE(perDate = true, fileDescriptor = "tile-cache"),
    ENERGY_READING(perDate = true, fileDescriptor = "load-readings"),

    ENERGY_READINGS_INDEX(perDate = false, fileDescriptor = "load-readings-index"),
    LOAD_AGGREGATOR_METERS_BY_DATE(perDate = false, fileDescriptor = "load-aggregator-mbd"),
    WEATHER_READING(perDate = false, fileDescriptor = "weather-readings"),
    RESULTS_CACHE(perDate = false, fileDescriptor = "results-cache")

}

/**
 * The contents contained within the variant.
 * @property DELETIONS_REVERSEMODIFICATIONS Contains the target of [ObjectDeletion]s and [ObjectReverseModification]s. These are called 'original' because
 *   the target will be the original object from the base model that the ChangeSet was derived from.
 * @property CREATIONS_MODIFICATIONS Contains the target of [ObjectCreation]s and [ObjectModification]s. These are called 'new' as the targets contain
 *   only updates or additions to the base model.
 * @property CHANGESET Contains the ChangeSet and its ChangeSetMembers.
 */
enum class VariantContents(val subDirectory: String, val types: Set<DatabaseType>) {
    DELETIONS_REVERSEMODIFICATIONS("original", setOf(DatabaseType.NETWORK_MODEL, DatabaseType.CUSTOMER, DatabaseType.DIAGRAM, DatabaseType.MEASUREMENT)),
    CREATIONS_MODIFICATIONS("new", setOf(DatabaseType.NETWORK_MODEL, DatabaseType.CUSTOMER, DatabaseType.DIAGRAM, DatabaseType.MEASUREMENT)),
    CHANGESET("", setOf(DatabaseType.VARIANT))
}
