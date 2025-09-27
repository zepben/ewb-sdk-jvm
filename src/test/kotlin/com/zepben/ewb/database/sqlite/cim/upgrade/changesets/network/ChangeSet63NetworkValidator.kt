/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.upgrade.changesets.network

import com.zepben.ewb.database.getNullableString
import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.cim.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import java.sql.Statement

object ChangeSet63NetworkValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 63) {

    override fun setUpStatements(): List<String> = listOf(
        "INSERT INTO location_street_addresses (town_name, state_or_province, postal_code, po_box, building_name, floor_identification, name, number, suite_number, type, display_address, location_mrid, address_field) VALUES ('town_name_1', 'state_or_province_1', 'postal_code_1', 'po_box_1', 'building_name_1', 'floor_identification_1', 'name_1', 'number_1', 'suite_number_1', 'type_1', 'display_address_1', 'location_mrid_1', 'address_field_1');",
    )

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO contact_details_electronic_addresses (email_1, is_primary, description, contact_details_id) VALUES ('email_1_2', 'is_primary_2', 'description_2', 'contact_details_id_2');",
        "INSERT INTO contact_details_street_addresses (town_name, state_or_province, country, postal_code, po_box, building_name, floor_identification, name, number, suite_number, type, display_address, building_number, contact_details_id) VALUES ('town_name_2', 'state_or_province_2', 'country_2', 'postal_code_2', 'po_box_2', 'building_name_2', 'floor_identification_2', 'name_2', 'number_2', 'suite_number_2', 'type_2', 'display_address_2', 'building_number_2', 'contact_details_id_2');",
        "INSERT INTO contact_details_telephone_numbers (area_code, city_code, country_code, dial_out, extension, international_prefix, local_number, is_primary, description, contact_details_id) VALUES ('area_code_2', 'city_code_2', 'country_code_2', 'dial_out_2', 'extension_2', 'international_prefix_2', 'local_number_2', 'is_primary_2', 'description_2', 'contact_details_id_2');",
        "INSERT INTO directional_current_relays (mrid, name, description, num_diagram_objects, location_mrid, num_controls, model, reclosing, relay_delay_time, protection_kind, directable, power_direction, relay_info_mrid, directional_characteristic_angle, polarizing_quantity_type, relay_element_phase, minimum_pickup_current, current_limit_1, inverse_time_flag, time_delay_1) VALUES ('mrid_2', 'name_2', 'description_2', 'num_diagram_objects_2', 'location_mrid_2', 'num_controls_2', 'model_2', 'reclosing_2', 'relay_delay_time_2', 'protection_kind_2', 'directable_2', 'power_direction_2', 'relay_info_mrid_2', 'directional_characteristic_angle_2', 'polarizing_quantity_type_2', 'relay_element_phase_2', 'minimum_pickup_current_2', 'current_limit_1_2', 'inverse_time_flag_2', 'time_delay_1_2');",
        "INSERT INTO location_street_addresses (town_name, state_or_province, country, postal_code, po_box, building_name, floor_identification, name, number, suite_number, type, display_address, building_number, location_mrid, address_field) VALUES ('town_name_2', 'state_or_province_2', 'country_2', 'postal_code_2', 'po_box_2', 'building_name_2', 'floor_identification_2', 'name_2', 'number_2', 'suite_number_2', 'type_2', 'display_address_2', 'building_number_2', 'location_mrid_2', 'address_field_2');",
        "INSERT INTO usage_points_contact_details (id, contact_type, first_name, last_name, preferred_contact_method, is_primary, business_name, usage_point_mrid) VALUES ('id_2', 'contact_type_2', 'first_name_2', 'last_name_2', 'preferred_contact_method_2', 'is_primary_2', 'business_name_2', 'usage_point_mrid_2');",
    )

    override fun validateChanges(statement: Statement) {
        `validate contact_details_electronic_addresses`(statement)
        `validate contact_details_street_addresses`(statement)
        `validate contact_details_telephone_numbers`(statement)
        `validate directional_current_relays`(statement)
        `validate location_street_addresses`(statement)
        `validate usage_points_contact_details`(statement)

        ensureIndexes(
            statement,
            "contact_details_electronic_addresses_contact_details_id",
            "contact_details_street_addresses_contact_details_id",
            "contact_details_telephone_numbers_contact_details_id",
            "directional_current_relays_mrid",
            "directional_current_relays_name",
            "usage_points_contact_details_id",
            "usage_points_contact_details_usage_point_mrid",
        )
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM contact_details_electronic_addresses;",
        "DELETE FROM contact_details_street_addresses;",
        "DELETE FROM contact_details_telephone_numbers;",
        "DELETE FROM directional_current_relays;",
        "DELETE FROM location_street_addresses;",
        "DELETE FROM usage_points_contact_details;",
    )

    private fun `validate contact_details_electronic_addresses`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM contact_details_electronic_addresses",
            { rs ->
                assertThat(rs.getNullableString("email_1"), equalTo("email_1_2"))
                assertThat(rs.getNullableString("is_primary"), equalTo("is_primary_2"))
                assertThat(rs.getNullableString("description"), equalTo("description_2"))
                assertThat(rs.getNullableString("contact_details_id"), equalTo("contact_details_id_2"))
            }
        )
    }

    private fun `validate contact_details_street_addresses`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM contact_details_street_addresses",
            { rs ->
                assertThat(rs.getNullableString("town_name"), equalTo("town_name_2"))
                assertThat(rs.getNullableString("state_or_province"), equalTo("state_or_province_2"))
                assertThat(rs.getNullableString("country"), equalTo("country_2"))
                assertThat(rs.getNullableString("postal_code"), equalTo("postal_code_2"))
                assertThat(rs.getNullableString("po_box"), equalTo("po_box_2"))
                assertThat(rs.getNullableString("building_name"), equalTo("building_name_2"))
                assertThat(rs.getNullableString("floor_identification"), equalTo("floor_identification_2"))
                assertThat(rs.getNullableString("name"), equalTo("name_2"))
                assertThat(rs.getNullableString("number"), equalTo("number_2"))
                assertThat(rs.getNullableString("suite_number"), equalTo("suite_number_2"))
                assertThat(rs.getNullableString("type"), equalTo("type_2"))
                assertThat(rs.getNullableString("display_address"), equalTo("display_address_2"))
                assertThat(rs.getNullableString("building_number"), equalTo("building_number_2"))
                assertThat(rs.getNullableString("contact_details_id"), equalTo("contact_details_id_2"))
            }
        )
    }

    private fun `validate contact_details_telephone_numbers`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM contact_details_telephone_numbers",
            { rs ->
                assertThat(rs.getNullableString("area_code"), equalTo("area_code_2"))
                assertThat(rs.getNullableString("city_code"), equalTo("city_code_2"))
                assertThat(rs.getNullableString("country_code"), equalTo("country_code_2"))
                assertThat(rs.getNullableString("dial_out"), equalTo("dial_out_2"))
                assertThat(rs.getNullableString("extension"), equalTo("extension_2"))
                assertThat(rs.getNullableString("international_prefix"), equalTo("international_prefix_2"))
                assertThat(rs.getNullableString("local_number"), equalTo("local_number_2"))
                assertThat(rs.getNullableString("is_primary"), equalTo("is_primary_2"))
                assertThat(rs.getNullableString("description"), equalTo("description_2"))
                assertThat(rs.getNullableString("contact_details_id"), equalTo("contact_details_id_2"))
            }
        )
    }

    private fun `validate directional_current_relays`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM directional_current_relays",
            { rs ->
                assertThat(rs.getNullableString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), equalTo("name_2"))
                assertThat(rs.getNullableString("description"), equalTo("description_2"))
                assertThat(rs.getNullableString("num_diagram_objects"), equalTo("num_diagram_objects_2"))
                assertThat(rs.getNullableString("location_mrid"), equalTo("location_mrid_2"))
                assertThat(rs.getNullableString("num_controls"), equalTo("num_controls_2"))
                assertThat(rs.getNullableString("model"), equalTo("model_2"))
                assertThat(rs.getNullableString("reclosing"), equalTo("reclosing_2"))
                assertThat(rs.getNullableString("relay_delay_time"), equalTo("relay_delay_time_2"))
                assertThat(rs.getNullableString("protection_kind"), equalTo("protection_kind_2"))
                assertThat(rs.getNullableString("directable"), equalTo("directable_2"))
                assertThat(rs.getNullableString("power_direction"), equalTo("power_direction_2"))
                assertThat(rs.getNullableString("relay_info_mrid"), equalTo("relay_info_mrid_2"))
                assertThat(rs.getNullableString("directional_characteristic_angle"), equalTo("directional_characteristic_angle_2"))
                assertThat(rs.getNullableString("polarizing_quantity_type"), equalTo("polarizing_quantity_type_2"))
                assertThat(rs.getNullableString("relay_element_phase"), equalTo("relay_element_phase_2"))
                assertThat(rs.getNullableString("minimum_pickup_current"), equalTo("minimum_pickup_current_2"))
                assertThat(rs.getNullableString("current_limit_1"), equalTo("current_limit_1_2"))
                assertThat(rs.getNullableString("inverse_time_flag"), equalTo("inverse_time_flag_2"))
                assertThat(rs.getNullableString("time_delay_1"), equalTo("time_delay_1_2"))
            }
        )
    }

    private fun `validate location_street_addresses`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM location_street_addresses",
            { rs ->
                assertThat(rs.getNullableString("town_name"), equalTo("town_name_1"))
                assertThat(rs.getNullableString("state_or_province"), equalTo("state_or_province_1"))
                assertThat(rs.getNullableString("country"), nullValue())
                assertThat(rs.getNullableString("postal_code"), equalTo("postal_code_1"))
                assertThat(rs.getNullableString("po_box"), equalTo("po_box_1"))
                assertThat(rs.getNullableString("building_name"), equalTo("building_name_1"))
                assertThat(rs.getNullableString("floor_identification"), equalTo("floor_identification_1"))
                assertThat(rs.getNullableString("name"), equalTo("name_1"))
                assertThat(rs.getNullableString("number"), equalTo("number_1"))
                assertThat(rs.getNullableString("suite_number"), equalTo("suite_number_1"))
                assertThat(rs.getNullableString("type"), equalTo("type_1"))
                assertThat(rs.getNullableString("display_address"), equalTo("display_address_1"))
                assertThat(rs.getNullableString("building_number"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getNullableString("address_field"), equalTo("address_field_1"))
            },
            { rs ->
                assertThat(rs.getNullableString("town_name"), equalTo("town_name_2"))
                assertThat(rs.getNullableString("state_or_province"), equalTo("state_or_province_2"))
                assertThat(rs.getNullableString("country"), equalTo("country_2"))
                assertThat(rs.getNullableString("postal_code"), equalTo("postal_code_2"))
                assertThat(rs.getNullableString("po_box"), equalTo("po_box_2"))
                assertThat(rs.getNullableString("building_name"), equalTo("building_name_2"))
                assertThat(rs.getNullableString("floor_identification"), equalTo("floor_identification_2"))
                assertThat(rs.getNullableString("name"), equalTo("name_2"))
                assertThat(rs.getNullableString("number"), equalTo("number_2"))
                assertThat(rs.getNullableString("suite_number"), equalTo("suite_number_2"))
                assertThat(rs.getNullableString("type"), equalTo("type_2"))
                assertThat(rs.getNullableString("display_address"), equalTo("display_address_2"))
                assertThat(rs.getNullableString("building_number"), equalTo("building_number_2"))
                assertThat(rs.getNullableString("location_mrid"), equalTo("location_mrid_2"))
                assertThat(rs.getNullableString("address_field"), equalTo("address_field_2"))
            }
        )
    }

    private fun `validate usage_points_contact_details`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM usage_points_contact_details",
            { rs ->
                assertThat(rs.getNullableString("id"), equalTo("id_2"))
                assertThat(rs.getNullableString("contact_type"), equalTo("contact_type_2"))
                assertThat(rs.getNullableString("first_name"), equalTo("first_name_2"))
                assertThat(rs.getNullableString("last_name"), equalTo("last_name_2"))
                assertThat(rs.getNullableString("preferred_contact_method"), equalTo("preferred_contact_method_2"))
                assertThat(rs.getNullableString("is_primary"), equalTo("is_primary_2"))
                assertThat(rs.getNullableString("business_name"), equalTo("business_name_2"))
                assertThat(rs.getNullableString("usage_point_mrid"), equalTo("usage_point_mrid_2"))
            }
        )
    }

}
