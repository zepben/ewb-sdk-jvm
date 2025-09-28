/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.upgrade.changesets

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.cim.upgrade.Change
import com.zepben.ewb.database.sqlite.cim.upgrade.ChangeSet

internal fun changeSet63() = ChangeSet(
    63,
    listOf(
        // Network changes
        `create usage_points_contact_details table`,
        `create contact_details_electronic_addresses table`,
        `create contact_details_street_addresses table`,
        `create contact_details_telephone_numbers table`,
        `create directional_current_relays table`,
        `add building number and country fields to street address tables`,

        // Customer changes
        `add validity interval fields to agreements table`,
    )
)

// ###################
// # Network Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `create usage_points_contact_details table` = Change(
    listOf(
        "CREATE TABLE usage_points_contact_details (id TEXT NOT NULL, contact_type TEXT NULL, first_name TEXT NULL, last_name TEXT NULL, preferred_contact_method TEXT NULL, is_primary BOOLEAN NULL, business_name TEXT NULL, usage_point_mrid TEXT NOT NULL);",
        "CREATE UNIQUE INDEX usage_points_contact_details_id ON usage_points_contact_details (id);",
        "CREATE INDEX usage_points_contact_details_usage_point_mrid ON usage_points_contact_details (usage_point_mrid);",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `create contact_details_electronic_addresses table` = Change(
    listOf(
        "CREATE TABLE contact_details_electronic_addresses (email_1 TEXT NULL, is_primary BOOLEAN NULL, description TEXT NULL, contact_details_id TEXT NOT NULL);",
        "CREATE INDEX contact_details_electronic_addresses_contact_details_id ON contact_details_electronic_addresses (contact_details_id);",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `create contact_details_street_addresses table` = Change(
    listOf(
        "CREATE TABLE contact_details_street_addresses (town_name TEXT NULL, state_or_province TEXT NULL, country TEXT NULL, postal_code TEXT NULL, po_box TEXT NULL, building_name TEXT NULL, floor_identification TEXT NULL, name TEXT NULL, number TEXT NULL, suite_number TEXT NULL, type TEXT NULL, display_address TEXT NULL, building_number TEXT NULL, contact_details_id TEXT NOT NULL);",
        "CREATE INDEX contact_details_street_addresses_contact_details_id ON contact_details_street_addresses (contact_details_id);",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `create contact_details_telephone_numbers table` = Change(
    listOf(
        "CREATE TABLE contact_details_telephone_numbers (area_code TEXT NULL, city_code TEXT NULL, country_code TEXT NULL, dial_out TEXT NULL, extension TEXT NULL, international_prefix TEXT NULL, local_number TEXT NULL, is_primary BOOLEAN NULL, description TEXT NULL, contact_details_id TEXT NOT NULL);",
        "CREATE INDEX contact_details_telephone_numbers_contact_details_id ON contact_details_telephone_numbers (contact_details_id);",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `create directional_current_relays table` = Change(
    listOf(
        "CREATE TABLE directional_current_relays (mrid TEXT NOT NULL, name TEXT NULL, description TEXT NULL, num_diagram_objects INTEGER NULL, location_mrid TEXT NULL, num_controls INTEGER NULL, model TEXT NULL, reclosing BOOLEAN NULL, relay_delay_time NUMBER NULL, protection_kind TEXT NOT NULL, directable BOOLEAN NULL, power_direction TEXT NOT NULL, relay_info_mrid TEXT NULL, directional_characteristic_angle NUMBER NULL, polarizing_quantity_type TEXT NULL, relay_element_phase TEXT NULL, minimum_pickup_current NUMBER NULL, current_limit_1 NUMBER NULL, inverse_time_flag BOOLEAN NULL, time_delay_1 NUMBER NULL);",
        "CREATE UNIQUE INDEX directional_current_relays_mrid ON directional_current_relays (mrid);",
        "CREATE INDEX directional_current_relays_name ON directional_current_relays (name);",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `add building number and country fields to street address tables` = Change(
    listOf(
        "ALTER TABLE location_street_addresses ADD building_number TEXT NULL;",
        "ALTER TABLE location_street_addresses ADD country TEXT NULL;",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

// ####################
// # Customer Changes #
// ####################

@Suppress("ObjectPropertyName")
private val `add validity interval fields to agreements table` = Change(
    listOf(
        "ALTER TABLE customer_agreements ADD validity_interval_start TEXT NULL;",
        "ALTER TABLE customer_agreements ADD validity_interval_end TEXT NULL;",
    ),
    targetDatabases = setOf(DatabaseType.CUSTOMER)
)
