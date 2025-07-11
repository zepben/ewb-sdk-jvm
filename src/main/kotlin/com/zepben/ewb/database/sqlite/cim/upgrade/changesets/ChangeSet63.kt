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
        // Network Change
        `Create directional current relay table`,
        `Create usage point contact details table`
    )
)

// ###################
// # Network Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `Create directional current relay table` = Change(
    listOf(
        """
        CREATE TABLE directional_current_relays (
            mrid TEXT NOT NULL, 
            name TEXT NOT NULL, 
            description TEXT NOT NULL, 
            num_diagram_objects INTEGER NOT NULL, 
            location_mrid TEXT NULL, 
            num_controls INTEGER NOT NULL, 
            model TEXT NULL, 
            reclosing BOOLEAN NULL, 
            relay_delay_time NUMBER NULL, 
            protection_kind TEXT NOT NULL, 
            directable BOOLEAN NULL, 
            power_direction TEXT NOT NULL, 
            relay_info_mrid TEXT NULL, 
            directional_characteristic_angle NUMBER NULL, 
            polarizing_quantity_type TEXT NULL, 
            relay_element_phase TEXT NULL, 
            minimum_pickup_current NUMBER NULL, 
            current_limit_1 NUMBER NULL, 
            inverse_time_flag NUMBER NULL, 
            time_delay_1 NUMBER NULL
        );""".trimIndent(),
    "CREATE UNIQUE INDEX directional_current_relays_mrid ON directional_current_relays (mrid);",
    "CREATE INDEX directional_current_relays_name ON directional_current_relays (name);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

@Suppress("ObjectPropertyName")
private val `Create usage point contact details table` = Change(
    listOf(
        """
        CREATE TABLE contact_details_electronic_addresses (email_1 TEXT NULL, is_primary BOOLEAN NOT NULL, description TEXT NULL, contact_details_id TEXT NOT NULL);
        CREATE UNIQUE INDEX contact_details_electronic_addresses_contact_details_id_email_1 ON contact_details_electronic_addresses (contact_details_id, email_1);
        CREATE INDEX contact_details_electronic_addresses_contact_details_id ON contact_details_electronic_addresses (contact_details_id);

        CREATE TABLE contact_details_street_addresses (town_name TEXT NULL, state_or_province TEXT NULL, postal_code TEXT NOT NULL, po_box TEXT NULL, building_name TEXT NULL, floor_identification TEXT NULL, name TEXT NULL, number TEXT NULL, suite_number TEXT NULL, type TEXT NULL, display_address TEXT NULL, contact_details_id TEXT NOT NULL);
        CREATE UNIQUE INDEX contact_details_street_addresses_contact_details_id ON contact_details_street_addresses (contact_details_id);
        
        CREATE TABLE contact_details_telephone_numbers (area_code TEXT NULL, city_code TEXT NULL, country_code TEXT NULL, dial_out TEXT NULL, extension TEXT NULL, international_prefix TEXT NULL, local_number TEXT NULL, is_primary BOOLEAN NULL, description TEXT NULL, contact_details_id TEXT NOT NULL);
        CREATE INDEX contact_details_telephone_numbers_contact_details_id ON contact_details_telephone_numbers (contact_details_id);

        CREATE TABLE usage_point_contact_details (contact_address TEXT NULL, contact_type TEXT NULL, id TEXT NULL, first_name TEXT NULL, last_name TEXT NULL, preferred_contact_method TEXT NULL, is_primary TEXT NULL, business_name TEXT NULL, contact_details_id TEXT NOT NULL, usage_point_mrid TEXT NOT NULL);
        CREATE UNIQUE INDEX usage_point_contact_details_usage_point_mrid_contact_details_id ON usage_point_contact_details (usage_point_mrid, contact_details_id);
        CREATE INDEX usage_point_contact_details_usage_point_mrid ON usage_point_contact_details (usage_point_mrid);

        """.trimIndent()

    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)
