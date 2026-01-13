/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.upgrade.changesets.network

import com.zepben.ewb.database.getNullableString
import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.upgrade.changesets.ChangeSetValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import java.sql.Statement

object ChangeSet61NetworkValidator : ChangeSetValidator(DatabaseType.NETWORK_MODEL, 61) {

    //
    // NOTE: We are utilising the SQLite feature of being able to put any type of data into a column by putting string into all fields. This stops us
    //       having to deal with the complexity of column types in the validations, but still does the nullability checking.
    //

    //
    // NOTE: Some columns were incorrectly left as `NOT NULL` in the v61 changeset, so the tests here will validate against thet. The fixes for these
    //       columns have been added in change set 62 as we already had databases in the wild on v61 before this was detected.
    //

    override fun setUpStatements(): List<String> = listOf(
        // Identified object tables.
        "INSERT INTO ac_line_segments (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, length, design_temperature, design_rating, wire_info_mrid, per_length_impedance_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'length_1', 'design_temperature_1', 'design_rating_1', 'wire_info_mrid_1', 'per_length_impedance_mrid_1');",
        "INSERT INTO accumulators (mrid, name, description, num_diagram_objects, power_system_resource_mrid, remote_source_mrid, terminal_mrid, phases, unit_symbol) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'power_system_resource_mrid_1', 'remote_source_mrid_1', 'terminal_mrid_1', 'phases_1', 'unit_symbol_1');",
        "INSERT INTO analogs (mrid, name, description, num_diagram_objects, power_system_resource_mrid, remote_source_mrid, terminal_mrid, phases, unit_symbol, positive_flow_in) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'power_system_resource_mrid_1', 'remote_source_mrid_1', 'terminal_mrid_1', 'phases_1', 'unit_symbol_1', 'positive_flow_in_1');",
        "INSERT INTO asset_owners (mrid, name, description, num_diagram_objects, organisation_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'organisation_mrid_1');",
        "INSERT INTO base_voltages (mrid, name, description, num_diagram_objects, nominal_voltage) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'nominal_voltage_1');",
        "INSERT INTO battery_controls (mrid, name, description, num_diagram_objects, location_mrid, num_controls, discrete, mode, monitored_phase, target_deadband, target_value, enabled, max_allowed_target_value, min_allowed_target_value, rated_current, terminal_mrid, ct_primary, min_target_deadband, charging_rate, discharging_rate, reserve_percent, control_mode) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'discrete_1', 'mode_1', 'monitored_phase_1', 'target_deadband_1', 'target_value_1', 'enabled_1', 'max_allowed_target_value_1', 'min_allowed_target_value_1', 'rated_current_1', 'terminal_mrid_1', 'ct_primary_1', 'min_target_deadband_1', 'charging_rate_1', 'discharging_rate_1', 'reserve_percent_1', 'control_mode_1');",
        "INSERT INTO battery_units (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, power_electronics_connection_mrid, max_p, min_p, battery_state, rated_e, stored_e) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'power_electronics_connection_mrid_1', 'max_p_1', 'min_p_1', 'battery_state_1', 'rated_e_1', 'stored_e_1');",
        "INSERT INTO breakers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid, breaking_capacity, in_transit_time) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'normal_open_1', 'open_1', 'rated_current_1', 'switch_info_mrid_1', 'breaking_capacity_1', 'in_transit_time_1');",
        "INSERT INTO busbar_sections (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1');",
        "INSERT INTO cable_info (mrid, name, description, num_diagram_objects, rated_current, material) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'rated_current_1', 'material_1');",
        "INSERT INTO circuits (mrid, name, description, num_diagram_objects, location_mrid, num_controls, loop_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'loop_mrid_1');",
        "INSERT INTO clamps (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, length_from_terminal_1, ac_line_segment_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'length_from_terminal_1_1', 'ac_line_segment_mrid_1');",
        "INSERT INTO connectivity_nodes (mrid, name, description, num_diagram_objects) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1');",
        "INSERT INTO controls (mrid, name, description, num_diagram_objects, power_system_resource_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'power_system_resource_mrid_1');",
        "INSERT INTO current_relays (mrid, name, description, num_diagram_objects, location_mrid, num_controls, model, reclosing, relay_delay_time, protection_kind, directable, power_direction, relay_info_mrid, current_limit_1, inverse_time_flag, time_delay_1) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'model_1', 'reclosing_1', 'relay_delay_time_1', 'protection_kind_1', 'directable_1', 'power_direction_1', 'relay_info_mrid_1', 'current_limit_1_1', 'inverse_time_flag_1', 'time_delay_1_1');",
        "INSERT INTO current_transformer_info (mrid, name, description, num_diagram_objects, accuracy_class, accuracy_limit, core_count, ct_class, knee_point_voltage, max_ratio_denominator, max_ratio_numerator, nominal_ratio_denominator, nominal_ratio_numerator, primary_ratio, rated_current, secondary_fls_rating, secondary_ratio, usage) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'accuracy_class_1', 'accuracy_limit_1', 'core_count_1', 'ct_class_1', 'knee_point_voltage_1', 'max_ratio_denominator_1', 'max_ratio_numerator_1', 'nominal_ratio_denominator_1', 'nominal_ratio_numerator_1', 'primary_ratio_1', 'rated_current_1', 'secondary_fls_rating_1', 'secondary_ratio_1', 'usage_1');",
        "INSERT INTO current_transformers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, terminal_mrid, current_transformer_info_mrid, core_burden) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'terminal_mrid_1', 'current_transformer_info_mrid_1', 'core_burden_1');",
        "INSERT INTO cuts (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid, length_from_terminal_1, ac_line_segment_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'normal_open_1', 'open_1', 'rated_current_1', 'switch_info_mrid_1', 'length_from_terminal_1_1', 'ac_line_segment_mrid_1');",
        "INSERT INTO disconnectors (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'normal_open_1', 'open_1', 'rated_current_1', 'switch_info_mrid_1');",
        "INSERT INTO discretes (mrid, name, description, num_diagram_objects, power_system_resource_mrid, remote_source_mrid, terminal_mrid, phases, unit_symbol) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'power_system_resource_mrid_1', 'remote_source_mrid_1', 'terminal_mrid_1', 'phases_1', 'unit_symbol_1');",
        "INSERT INTO distance_relays (mrid, name, description, num_diagram_objects, location_mrid, num_controls, model, reclosing, relay_delay_time, protection_kind, directable, power_direction, relay_info_mrid, backward_blind, backward_reach, backward_reactance, forward_blind, forward_reach, forward_reactance, operation_phase_angle1, operation_phase_angle2, operation_phase_angle3) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'model_1', 'reclosing_1', 'relay_delay_time_1', 'protection_kind_1', 'directable_1', 'power_direction_1', 'relay_info_mrid_1', 'backward_blind_1', 'backward_reach_1', 'backward_reactance_1', 'forward_blind_1', 'forward_reach_1', 'forward_reactance_1', 'operation_phase_angle1_1', 'operation_phase_angle2_1', 'operation_phase_angle3_1');",
        "INSERT INTO energy_consumer_phases (mrid, name, description, num_diagram_objects, location_mrid, num_controls, energy_consumer_mrid, phase, p, q, p_fixed, q_fixed) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'energy_consumer_mrid_1', 'phase_1', 'p_1', 'q_1', 'p_fixed_1', 'q_fixed_1');",
        "INSERT INTO energy_consumers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, customer_count, grounded, p, q, p_fixed, q_fixed, phase_connection) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'customer_count_1', 'grounded_1', 'p_1', 'q_1', 'p_fixed_1', 'q_fixed_1', 'phase_connection_1');",
        "INSERT INTO energy_source_phases (mrid, name, description, num_diagram_objects, location_mrid, num_controls, energy_source_mrid, phase) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'energy_source_mrid_1', 'phase_1');",
        "INSERT INTO energy_sources (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, active_power, reactive_power, voltage_angle, voltage_magnitude, p_max, p_min, r, r0, rn, x, x0, xn, is_external_grid, r_min, rn_min, r0_min, x_min, xn_min, x0_min, r_max, rn_max, r0_max, x_max, xn_max, x0_max) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'active_power_1', 'reactive_power_1', 'voltage_angle_1', 'voltage_magnitude_1', 'p_max_1', 'p_min_1', 'r_1', 'r0_1', 'rn_1', 'x_1', 'x0_1', 'xn_1', 'is_external_grid_1', 'r_min_1', 'rn_min_1', 'r0_min_1', 'x_min_1', 'xn_min_1', 'x0_min_1', 'r_max_1', 'rn_max_1', 'r0_max_1', 'x_max_1', 'xn_max_1', 'x0_max_1');",
        "INSERT INTO equivalent_branches (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, negative_r12, negative_r21, negative_x12, negative_x21, positive_r12, positive_r21, positive_x12, positive_x21, r, r21, x, x21, zero_r12, zero_r21, zero_x12, zero_x21) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'negative_r12_1', 'negative_r21_1', 'negative_x12_1', 'negative_x21_1', 'positive_r12_1', 'positive_r21_1', 'positive_x12_1', 'positive_x21_1', 'r_1', 'r21_1', 'x_1', 'x21_1', 'zero_r12_1', 'zero_r21_1', 'zero_x12_1', 'zero_x21_1');",
        "INSERT INTO ev_charging_units (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, power_electronics_connection_mrid, max_p, min_p) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'power_electronics_connection_mrid_1', 'max_p_1', 'min_p_1');",
        "INSERT INTO fault_indicators (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, terminal_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'terminal_mrid_1');",
        "INSERT INTO feeders (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normal_head_terminal_mrid, normal_energizing_substation_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normal_head_terminal_mrid_1', 'normal_energizing_substation_mrid_1');",
        "INSERT INTO fuses (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid, function_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'normal_open_1', 'open_1', 'rated_current_1', 'switch_info_mrid_1', 'function_mrid_1');",
        "INSERT INTO geographical_regions (mrid, name, description, num_diagram_objects) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1');",
        "INSERT INTO grounds (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1');",
        "INSERT INTO ground_disconnectors (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'normal_open_1', 'open_1', 'rated_current_1', 'switch_info_mrid_1');",
        "INSERT INTO grounding_impedances (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, r, x) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'r_1', 'x_1');",
        "INSERT INTO jumpers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'normal_open_1', 'open_1', 'rated_current_1', 'switch_info_mrid_1');",
        "INSERT INTO junctions (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1');",
        "INSERT INTO linear_shunt_compensators (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, control_enabled, regulating_control_mrid, shunt_compensator_info_mrid, grounded, nom_u, phase_connection, sections, b0_per_section, b_per_section, g0_per_section, g_per_section) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'control_enabled_1', 'regulating_control_mrid_1', 'shunt_compensator_info_mrid_1', 'grounded_1', 'nom_u_1', 'phase_connection_1', 'sections_1', 'b0_per_section_1', 'b_per_section_1', 'g0_per_section_1', 'g_per_section_1');",
        "INSERT INTO load_break_switches (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid, breaking_capacity) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'normal_open_1', 'open_1', 'rated_current_1', 'switch_info_mrid_1', 'breaking_capacity_1');",
        "INSERT INTO locations (mrid, name, description, num_diagram_objects) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1');",
        "INSERT INTO loops (mrid, name, description, num_diagram_objects) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1');",
        "INSERT INTO lv_feeders (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normal_head_terminal_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normal_head_terminal_mrid_1');",
        "INSERT INTO meters (mrid, name, description, num_diagram_objects, location_mrid, customer_mrid, service_location_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'customer_mrid_1', 'service_location_mrid_1');",
        "INSERT INTO no_load_tests (mrid, name, description, num_diagram_objects, base_power, temperature, energised_end_voltage, exciting_current, exciting_current_zero, loss, loss_zero) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'base_power_1', 'temperature_1', 'energised_end_voltage_1', 'exciting_current_1', 'exciting_current_zero_1', 'loss_1', 'loss_zero_1');",
        "INSERT INTO open_circuit_tests (mrid, name, description, num_diagram_objects, base_power, temperature, energised_end_step, energised_end_voltage, open_end_step, open_end_voltage, phase_shift) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'base_power_1', 'temperature_1', 'energised_end_step_1', 'energised_end_voltage_1', 'open_end_step_1', 'open_end_voltage_1', 'phase_shift_1');",
        "INSERT INTO operational_restrictions (mrid, name, description, num_diagram_objects, title, created_date_time, author_name, type, status, comment) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'title_1', 'created_date_time_1', 'author_name_1', 'type_1', 'status_1', 'comment_1');",
        "INSERT INTO organisations (mrid, name, description, num_diagram_objects) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1');",
        "INSERT INTO overhead_wire_info (mrid, name, description, num_diagram_objects, rated_current, material) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'rated_current_1', 'material_1');",
        "INSERT INTO pan_demand_response_functions (mrid, name, description, num_diagram_objects, enabled, kind, appliance) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'enabled_1', 'kind_1', 'appliance_1');",
        "INSERT INTO per_length_phase_impedances (mrid, name, description, num_diagram_objects) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1');",
        "INSERT INTO per_length_sequence_impedances (mrid, name, description, num_diagram_objects, r, x, r0, x0, bch, gch, b0ch, g0ch) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'r_1', 'x_1', 'r0_1', 'x0_1', 'bch_1', 'gch_1', 'b0ch_1', 'g0ch_1');",
        "INSERT INTO petersen_coils (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, r, x_ground_nominal) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'r_1', 'x_ground_nominal_1');",
        "INSERT INTO photo_voltaic_units (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, power_electronics_connection_mrid, max_p, min_p) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'power_electronics_connection_mrid_1', 'max_p_1', 'min_p_1');",
        "INSERT INTO poles (mrid, name, description, num_diagram_objects, location_mrid, classification) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'classification_1');",
        "INSERT INTO potential_transformer_info (mrid, name, description, num_diagram_objects, accuracy_class, nominal_ratio_denominator, nominal_ratio_numerator, primary_ratio, pt_class, rated_voltage, secondary_ratio) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'accuracy_class_1', 'nominal_ratio_denominator_1', 'nominal_ratio_numerator_1', 'primary_ratio_1', 'pt_class_1', 'rated_voltage_1', 'secondary_ratio_1');",
        "INSERT INTO potential_transformers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, terminal_mrid, potential_transformer_info_mrid, type) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'terminal_mrid_1', 'potential_transformer_info_mrid_1', 'type_1');",
        "INSERT INTO power_electronics_connections (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, control_enabled, regulating_control_mrid, max_i_fault, max_q, min_q, p, q, rated_s, rated_u, inverter_standard, sustain_op_overvolt_limit, stop_at_over_freq, stop_at_under_freq, inv_volt_watt_resp_mode, inv_watt_resp_v1, inv_watt_resp_v2, inv_watt_resp_v3, inv_watt_resp_v4, inv_watt_resp_p_at_v1, inv_watt_resp_p_at_v2, inv_watt_resp_p_at_v3, inv_watt_resp_p_at_v4, inv_volt_var_resp_mode, inv_var_resp_v1, inv_var_resp_v2, inv_var_resp_v3, inv_var_resp_v4, inv_var_resp_q_at_v1, inv_var_resp_q_at_v2, inv_var_resp_q_at_v3, inv_var_resp_q_at_v4, inv_reactive_power_mode, inv_fix_reactive_power) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'control_enabled_1', 'regulating_control_mrid_1', 'max_i_fault_1', 'max_q_1', 'min_q_1', 'p_1', 'q_1', 'rated_s_1', 'rated_u_1', 'inverter_standard_1', 'sustain_op_overvolt_limit_1', 'stop_at_over_freq_1', 'stop_at_under_freq_1', 'inv_volt_watt_resp_mode_1', 'inv_watt_resp_v1_1', 'inv_watt_resp_v2_1', 'inv_watt_resp_v3_1', 'inv_watt_resp_v4_1', 'inv_watt_resp_p_at_v1_1', 'inv_watt_resp_p_at_v2_1', 'inv_watt_resp_p_at_v3_1', 'inv_watt_resp_p_at_v4_1', 'inv_volt_var_resp_mode_1', 'inv_var_resp_v1_1', 'inv_var_resp_v2_1', 'inv_var_resp_v3_1', 'inv_var_resp_v4_1', 'inv_var_resp_q_at_v1_1', 'inv_var_resp_q_at_v2_1', 'inv_var_resp_q_at_v3_1', 'inv_var_resp_q_at_v4_1', 'inv_reactive_power_mode_1', 'inv_fix_reactive_power_1');",
        "INSERT INTO power_electronics_connection_phases (mrid, name, description, num_diagram_objects, location_mrid, num_controls, power_electronics_connection_mrid, p, phase, q) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'power_electronics_connection_mrid_1', 'p_1', 'phase_1', 'q_1');",
        "INSERT INTO power_electronics_wind_units (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, power_electronics_connection_mrid, max_p, min_p) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'power_electronics_connection_mrid_1', 'max_p_1', 'min_p_1');",
        "INSERT INTO power_transformer_ends (mrid, name, description, num_diagram_objects, end_number, terminal_mrid, base_voltage_mrid, grounded, r_ground, x_ground, star_impedance_mrid, power_transformer_mrid, connection_kind, phase_angle_clock, b, b0, g, g0, r, r0, rated_u, x, x0) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'end_number_1', 'terminal_mrid_1', 'base_voltage_mrid_1', 'grounded_1', 'r_ground_1', 'x_ground_1', 'star_impedance_mrid_1', 'power_transformer_mrid_1', 'connection_kind_1', 'phase_angle_clock_1', 'b_1', 'b0_1', 'g_1', 'g0_1', 'r_1', 'r0_1', 'rated_u_1', 'x_1', 'x0_1');",
        "INSERT INTO power_transformer_info (mrid, name, description, num_diagram_objects) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1');",
        "INSERT INTO power_transformers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, vector_group, transformer_utilisation, construction_kind, function, power_transformer_info_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'vector_group_1', 'transformer_utilisation_1', 'construction_kind_1', 'function_1', 'power_transformer_info_mrid_1');",
        "INSERT INTO protection_relay_schemes (mrid, name, description, num_diagram_objects, system_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'system_mrid_1');",
        "INSERT INTO protection_relay_systems (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, protection_kind) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'protection_kind_1');",
        "INSERT INTO ratio_tap_changers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, control_enabled, high_step, low_step, neutral_step, neutral_u, normal_step, step, tap_changer_control_mrid, transformer_end_mrid, step_voltage_increment) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'control_enabled_1', 'high_step_1', 'low_step_1', 'neutral_step_1', 'neutral_u_1', 'normal_step_1', 'step_1', 'tap_changer_control_mrid_1', 'transformer_end_mrid_1', 'step_voltage_increment_1');",
        "INSERT INTO reactive_capability_curves (mrid, name, description, num_diagram_objects) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1');",
        "INSERT INTO reclosers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid, breaking_capacity) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'normal_open_1', 'open_1', 'rated_current_1', 'switch_info_mrid_1', 'breaking_capacity_1');",
        "INSERT INTO relay_info (mrid, name, description, num_diagram_objects, curve_setting, reclose_fast) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'curve_setting_1', 'reclose_fast_1');",
        "INSERT INTO remote_controls (mrid, name, description, num_diagram_objects, control_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'control_mrid_1');",
        "INSERT INTO remote_sources (mrid, name, description, num_diagram_objects, measurement_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'measurement_mrid_1');",
        "INSERT INTO series_compensators (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, r, r0, x, x0, varistor_rated_current, varistor_voltage_threshold) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'r_1', 'r0_1', 'x_1', 'x0_1', 'varistor_rated_current_1', 'varistor_voltage_threshold_1');",
        "INSERT INTO short_circuit_tests (mrid, name, description, num_diagram_objects, base_power, temperature, current, energised_end_step, grounded_end_step, leakage_impedance, leakage_impedance_zero, loss, loss_zero, power, voltage, voltage_ohmic_part) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'base_power_1', 'temperature_1', 'current_1', 'energised_end_step_1', 'grounded_end_step_1', 'leakage_impedance_1', 'leakage_impedance_zero_1', 'loss_1', 'loss_zero_1', 'power_1', 'voltage_1', 'voltage_ohmic_part_1');",
        "INSERT INTO shunt_compensator_info (mrid, name, description, num_diagram_objects, max_power_loss, rated_current, rated_reactive_power, rated_voltage) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'max_power_loss_1', 'rated_current_1', 'rated_reactive_power_1', 'rated_voltage_1');",
        "INSERT INTO sites (mrid, name, description, num_diagram_objects, location_mrid, num_controls) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1');",
        "INSERT INTO streetlights (mrid, name, description, num_diagram_objects, location_mrid, pole_mrid, lamp_kind, light_rating) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'pole_mrid_1', 'lamp_kind_1', 'light_rating_1');",
        "INSERT INTO sub_geographical_regions (mrid, name, description, num_diagram_objects, geographical_region_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'geographical_region_mrid_1');",
        "INSERT INTO substations (mrid, name, description, num_diagram_objects, location_mrid, num_controls, sub_geographical_region_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'sub_geographical_region_mrid_1');",
        "INSERT INTO static_var_compensators (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, control_enabled, regulating_control_mrid, capacitive_rating, inductive_rating, q, svc_control_mode, voltage_set_point) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'control_enabled_1', 'regulating_control_mrid_1', 'capacitive_rating_1', 'inductive_rating_1', 'q_1', 'svc_control_mode_1', 'voltage_set_point_1');",
        "INSERT INTO switch_info (mrid, name, description, num_diagram_objects, rated_interrupting_time) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'rated_interrupting_time_1');",
        "INSERT INTO synchronous_machines (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, control_enabled, regulating_control_mrid, rated_power_factor, rated_s, rated_u, p, q, base_q, condenser_p, earthing, earthing_star_point_r, earthing_star_point_x, ikk, max_q, max_u, min_q, min_u, mu, r, r0, r2, sat_direct_subtrans_x, sat_direct_sync_x, sat_direct_trans_x, x0, x2, type, operating_mode) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'normally_in_service_1', 'in_service_1', 'commissioned_date_1', 'base_voltage_mrid_1', 'control_enabled_1', 'regulating_control_mrid_1', 'rated_power_factor_1', 'rated_s_1', 'rated_u_1', 'p_1', 'q_1', 'base_q_1', 'condenser_p_1', 'earthing_1', 'earthing_star_point_r_1', 'earthing_star_point_x_1', 'ikk_1', 'max_q_1', 'max_u_1', 'min_q_1', 'min_u_1', 'mu_1', 'r_1', 'r0_1', 'r2_1', 'sat_direct_subtrans_x_1', 'sat_direct_sync_x_1', 'sat_direct_trans_x_1', 'x0_1', 'x2_1', 'type_1', 'operating_mode_1');",
        "INSERT INTO tap_changer_controls (mrid, name, description, num_diagram_objects, location_mrid, num_controls, discrete, mode, monitored_phase, target_deadband, target_value, enabled, max_allowed_target_value, min_allowed_target_value, rated_current, terminal_mrid, ct_primary, min_target_deadband, limit_voltage, line_drop_compensation, line_drop_r, line_drop_x, reverse_line_drop_r, reverse_line_drop_x, forward_ldc_blocking, time_delay, co_generation_enabled) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'discrete_1', 'mode_1', 'monitored_phase_1', 'target_deadband_1', 'target_value_1', 'enabled_1', 'max_allowed_target_value_1', 'min_allowed_target_value_1', 'rated_current_1', 'terminal_mrid_1', 'ct_primary_1', 'min_target_deadband_1', 'limit_voltage_1', 'line_drop_compensation_1', 'line_drop_r_1', 'line_drop_x_1', 'reverse_line_drop_r_1', 'reverse_line_drop_x_1', 'forward_ldc_blocking_1', 'time_delay_1', 'co_generation_enabled_1');",
        "INSERT INTO terminals (mrid, name, description, num_diagram_objects, conducting_equipment_mrid, sequence_number, connectivity_node_mrid, phases) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'conducting_equipment_mrid_1', 'sequence_number_1', 'connectivity_node_mrid_1', 'phases_1');",
        "INSERT INTO transformer_end_info (mrid, name, description, num_diagram_objects, connection_kind, emergency_s, end_number, insulation_u, phase_angle_clock, r, rated_s, rated_u, short_term_s, transformer_tank_info_mrid, energised_end_no_load_tests, energised_end_short_circuit_tests, grounded_end_short_circuit_tests, open_end_open_circuit_tests, energised_end_open_circuit_tests) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'connection_kind_1', 'emergency_s_1', 'end_number_1', 'insulation_u_1', 'phase_angle_clock_1', 'r_1', 'rated_s_1', 'rated_u_1', 'short_term_s_1', 'transformer_tank_info_mrid_1', 'energised_end_no_load_tests_1', 'energised_end_short_circuit_tests_1', 'grounded_end_short_circuit_tests_1', 'open_end_open_circuit_tests_1', 'energised_end_open_circuit_tests_1');",
        "INSERT INTO transformer_star_impedances (mrid, name, description, num_diagram_objects, R, R0, X, X0, transformer_end_info_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'R_1', 'R0_1', 'X_1', 'X0_1', 'transformer_end_info_mrid_1');",
        "INSERT INTO transformer_tank_info (mrid, name, description, num_diagram_objects, power_transformer_info_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'power_transformer_info_mrid_1');",
        "INSERT INTO usage_points (mrid, name, description, num_diagram_objects, location_mrid, is_virtual, connection_category, rated_power, approved_inverter_capacity, phase_code) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'is_virtual_1', 'connection_category_1', 'rated_power_1', 'approved_inverter_capacity_1', 'phase_code_1');",
        "INSERT INTO voltage_relays (mrid, name, description, num_diagram_objects, location_mrid, num_controls, model, reclosing, relay_delay_time, protection_kind, directable, power_direction, relay_info_mrid) VALUES ('mrid_1', 'name_1', 'description_1', 'num_diagram_objects_1', 'location_mrid_1', 'num_controls_1', 'model_1', 'reclosing_1', 'relay_delay_time_1', 'protection_kind_1', 'directable_1', 'power_direction_1', 'relay_info_mrid_1');",

        // Array data tables.
        "INSERT INTO location_street_addresses (town_name, state_or_province, postal_code, po_box, building_name, floor_identification, name, number, suite_number, type, display_address, location_mrid, address_field) VALUES ('town_name_1', 'state_or_province_1', 'postal_code_1', 'po_box_1', 'building_name_1', 'floor_identification_1', 'name_1', 'number_1', 'suite_number_1', 'type_1', 'display_address_1', 'location_mrid_1', 'address_field_1');",
        "INSERT INTO phase_impedance_data (per_length_phase_impedance_mrid, from_phase, to_phase, b, g, r, x) VALUES ('per_length_phase_impedance_mrid_1', 'from_phase_1', 'to_phase_1', 'b_1', 'g_1', 'r_1', 'x_1');",
        "INSERT INTO protection_relay_function_thresholds (protection_relay_function_mrid, sequence_number, unit_symbol, value, name) VALUES ('protection_relay_function_mrid_1', 'sequence_number_1', 'unit_symbol_1', 'value_1', 'name_1');",
    )

    override fun populateStatements(): List<String> = listOf(
        // Identified object tables.
        "INSERT INTO ac_line_segments (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, length, design_temperature, design_rating, wire_info_mrid, per_length_impedance_mrid) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO accumulators (mrid, name, description, num_diagram_objects, power_system_resource_mrid, remote_source_mrid, terminal_mrid, phases, unit_symbol) VALUES ('mrid_2', null, null, null, null, null, null, 'phases_2', 'unit_symbol_2');",
        "INSERT INTO analogs (mrid, name, description, num_diagram_objects, power_system_resource_mrid, remote_source_mrid, terminal_mrid, phases, unit_symbol, positive_flow_in) VALUES ('mrid_2', null, null, null, null, null, null, 'phases_2', 'unit_symbol_2', null);",
        "INSERT INTO asset_owners (mrid, name, description, num_diagram_objects, organisation_mrid) VALUES ('mrid_2', null, null, null, null);",
        "INSERT INTO base_voltages (mrid, name, description, num_diagram_objects, nominal_voltage) VALUES ('mrid_2', null, null, null, 'nominal_voltage_2');",
        "INSERT INTO battery_controls (mrid, name, description, num_diagram_objects, location_mrid, num_controls, discrete, mode, monitored_phase, target_deadband, target_value, enabled, max_allowed_target_value, min_allowed_target_value, rated_current, terminal_mrid, ct_primary, min_target_deadband, charging_rate, discharging_rate, reserve_percent, control_mode) VALUES ('mrid_2', 'should_be_nullable', null, null, null, null, null, 'mode_2', 'monitored_phase_2', null, null, null, null, null, null, null, null, null, null, null, null, 'control_mode_2');",
        "INSERT INTO battery_units (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, power_electronics_connection_mrid, max_p, min_p, battery_state, rated_e, stored_e) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null, 'battery_state_2', null, null);",
        "INSERT INTO breakers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid, breaking_capacity, in_transit_time) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, 'normal_open_2', 'open_2', null, null, null, null);",
        "INSERT INTO busbar_sections (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null);",
        "INSERT INTO cable_info (mrid, name, description, num_diagram_objects, rated_current, material) VALUES ('mrid_2', null, null, null, null, 'material_2');",
        "INSERT INTO circuits (mrid, name, description, num_diagram_objects, location_mrid, num_controls, loop_mrid) VALUES ('mrid_2', null, null, null, null, null, null);",
        "INSERT INTO clamps (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, length_from_terminal_1, ac_line_segment_mrid) VALUES ('mrid_2', 'should_be_nullable', null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO connectivity_nodes (mrid, name, description, num_diagram_objects) VALUES ('mrid_2', null, null, null);",
        "INSERT INTO controls (mrid, name, description, num_diagram_objects, power_system_resource_mrid) VALUES ('mrid_2', null, null, null, null);",
        "INSERT INTO current_relays (mrid, name, description, num_diagram_objects, location_mrid, num_controls, model, reclosing, relay_delay_time, protection_kind, directable, power_direction, relay_info_mrid, current_limit_1, inverse_time_flag, time_delay_1) VALUES ('mrid_2', null, null, null, null, null, null, null, null, 'protection_kind_2', null, 'power_direction_2', null, null, null, null);",
        "INSERT INTO current_transformer_info (mrid, name, description, num_diagram_objects, accuracy_class, accuracy_limit, core_count, ct_class, knee_point_voltage, max_ratio_denominator, max_ratio_numerator, nominal_ratio_denominator, nominal_ratio_numerator, primary_ratio, rated_current, secondary_fls_rating, secondary_ratio, usage) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO current_transformers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, terminal_mrid, current_transformer_info_mrid, core_burden) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO cuts (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid, length_from_terminal_1, ac_line_segment_mrid) VALUES ('mrid_2', 'should_be_nullable', null, null, null, null, null, null, null, null, 'normal_open_2', 'open_2', null, null, null, null);",
        "INSERT INTO disconnectors (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, 'normal_open_2', 'open_2', null, null);",
        "INSERT INTO discretes (mrid, name, description, num_diagram_objects, power_system_resource_mrid, remote_source_mrid, terminal_mrid, phases, unit_symbol) VALUES ('mrid_2', null, null, null, null, null, null, 'phases_2', 'unit_symbol_2');",
        "INSERT INTO distance_relays (mrid, name, description, num_diagram_objects, location_mrid, num_controls, model, reclosing, relay_delay_time, protection_kind, directable, power_direction, relay_info_mrid, backward_blind, backward_reach, backward_reactance, forward_blind, forward_reach, forward_reactance, operation_phase_angle1, operation_phase_angle2, operation_phase_angle3) VALUES ('mrid_2', null, null, null, null, null, null, null, null, 'protection_kind_2', null, 'power_direction_2', null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO energy_consumer_phases (mrid, name, description, num_diagram_objects, location_mrid, num_controls, energy_consumer_mrid, phase, p, q, p_fixed, q_fixed) VALUES ('mrid_2', null, null, null, null, null, 'energy_consumer_mrid_2', 'phase_2', null, null, null, null);",
        "INSERT INTO energy_consumers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, customer_count, grounded, p, q, p_fixed, q_fixed, phase_connection) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 'phase_connection_2');",
        "INSERT INTO energy_source_phases (mrid, name, description, num_diagram_objects, location_mrid, num_controls, energy_source_mrid, phase) VALUES ('mrid_2', null, null, null, null, null, 'energy_source_mrid_2', 'phase_2');",
        "INSERT INTO energy_sources (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, active_power, reactive_power, voltage_angle, voltage_magnitude, p_max, p_min, r, r0, rn, x, x0, xn, is_external_grid, r_min, rn_min, r0_min, x_min, xn_min, x0_min, r_max, rn_max, r0_max, x_max, xn_max, x0_max) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO equivalent_branches (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, negative_r12, negative_r21, negative_x12, negative_x21, positive_r12, positive_r21, positive_x12, positive_x21, r, r21, x, x21, zero_r12, zero_r21, zero_x12, zero_x21) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO ev_charging_units (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, power_electronics_connection_mrid, max_p, min_p) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO fault_indicators (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, terminal_mrid) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null);",
        "INSERT INTO feeders (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normal_head_terminal_mrid, normal_energizing_substation_mrid) VALUES ('mrid_2', null, null, null, null, null, null, null);",
        "INSERT INTO fuses (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid, function_mrid) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, 'normal_open_2', 'open_2', null, null, null);",
        "INSERT INTO geographical_regions (mrid, name, description, num_diagram_objects) VALUES ('mrid_2', null, null, null);",
        "INSERT INTO grounds (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null);",
        "INSERT INTO ground_disconnectors (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, 'normal_open_2', 'open_2', null, null);",
        "INSERT INTO grounding_impedances (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, r, x) VALUES ('mrid_2', 'should_be_nullable', null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO jumpers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, 'normal_open_2', 'open_2', null, null);",
        "INSERT INTO junctions (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null);",
        "INSERT INTO linear_shunt_compensators (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, control_enabled, regulating_control_mrid, shunt_compensator_info_mrid, grounded, nom_u, phase_connection, sections, b0_per_section, b_per_section, g0_per_section, g_per_section) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null, null, null, null, 'phase_connection_2', null, null, null, null, null);",
        "INSERT INTO load_break_switches (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid, breaking_capacity) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, 'normal_open_2', 'open_2', null, null, null);",
        "INSERT INTO locations (mrid, name, description, num_diagram_objects) VALUES ('mrid_2', null, null, null);",
        "INSERT INTO loops (mrid, name, description, num_diagram_objects) VALUES ('mrid_2', null, null, null);",
        "INSERT INTO lv_feeders (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normal_head_terminal_mrid) VALUES ('mrid_2', null, null, null, null, null, null);",
        "INSERT INTO meters (mrid, name, description, num_diagram_objects, location_mrid, customer_mrid, service_location_mrid) VALUES ('mrid_2', null, null, null, null, null, null);",
        "INSERT INTO no_load_tests (mrid, name, description, num_diagram_objects, base_power, temperature, energised_end_voltage, exciting_current, exciting_current_zero, loss, loss_zero) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO open_circuit_tests (mrid, name, description, num_diagram_objects, base_power, temperature, energised_end_step, energised_end_voltage, open_end_step, open_end_voltage, phase_shift) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO operational_restrictions (mrid, name, description, num_diagram_objects, title, created_date_time, author_name, type, status, comment) VALUES ('mrid_2', null, null, null, null, null, null, null, null, 'should_be_nullable');",
        "INSERT INTO organisations (mrid, name, description, num_diagram_objects) VALUES ('mrid_2', null, null, null);",
        "INSERT INTO overhead_wire_info (mrid, name, description, num_diagram_objects, rated_current, material) VALUES ('mrid_2', null, null, null, null, 'material_2');",
        "INSERT INTO pan_demand_response_functions (mrid, name, description, num_diagram_objects, enabled, kind, appliance) VALUES ('mrid_2', 'should_be_nullable', null, null, null, null, null);",
        "INSERT INTO per_length_phase_impedances (mrid, name, description, num_diagram_objects) VALUES ('mrid_2', 'should_be_nullable', null, null);",
        "INSERT INTO per_length_sequence_impedances (mrid, name, description, num_diagram_objects, r, x, r0, x0, bch, gch, b0ch, g0ch) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO petersen_coils (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, r, x_ground_nominal) VALUES ('mrid_2', 'should_be_nullable', null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO photo_voltaic_units (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, power_electronics_connection_mrid, max_p, min_p) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO poles (mrid, name, description, num_diagram_objects, location_mrid, classification) VALUES ('mrid_2', null, null, null, null, 'should_be_nullable');",
        "INSERT INTO potential_transformer_info (mrid, name, description, num_diagram_objects, accuracy_class, nominal_ratio_denominator, nominal_ratio_numerator, primary_ratio, pt_class, rated_voltage, secondary_ratio) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO potential_transformers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, terminal_mrid, potential_transformer_info_mrid, type) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, 'type_2');",
        "INSERT INTO power_electronics_connections (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, control_enabled, regulating_control_mrid, max_i_fault, max_q, min_q, p, q, rated_s, rated_u, inverter_standard, sustain_op_overvolt_limit, stop_at_over_freq, stop_at_under_freq, inv_volt_watt_resp_mode, inv_watt_resp_v1, inv_watt_resp_v2, inv_watt_resp_v3, inv_watt_resp_v4, inv_watt_resp_p_at_v1, inv_watt_resp_p_at_v2, inv_watt_resp_p_at_v3, inv_watt_resp_p_at_v4, inv_volt_var_resp_mode, inv_var_resp_v1, inv_var_resp_v2, inv_var_resp_v3, inv_var_resp_v4, inv_var_resp_q_at_v1, inv_var_resp_q_at_v2, inv_var_resp_q_at_v3, inv_var_resp_q_at_v4, inv_reactive_power_mode, inv_fix_reactive_power) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO power_electronics_connection_phases (mrid, name, description, num_diagram_objects, location_mrid, num_controls, power_electronics_connection_mrid, p, phase, q) VALUES ('mrid_2', null, null, null, null, null, null, null, 'phase_2', null);",
        "INSERT INTO power_electronics_wind_units (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, power_electronics_connection_mrid, max_p, min_p) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO power_transformer_ends (mrid, name, description, num_diagram_objects, end_number, terminal_mrid, base_voltage_mrid, grounded, r_ground, x_ground, star_impedance_mrid, power_transformer_mrid, connection_kind, phase_angle_clock, b, b0, g, g0, r, r0, rated_u, x, x0) VALUES ('mrid_2', null, null, null, 'end_number_2', null, null, null, null, null, null, null, 'connection_kind_2', null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO power_transformer_info (mrid, name, description, num_diagram_objects) VALUES ('mrid_2', null, null, null);",
        "INSERT INTO power_transformers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, vector_group, transformer_utilisation, construction_kind, function, power_transformer_info_mrid) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, 'vector_group_2', null, 'construction_kind_2', 'function_2', null);",
        "INSERT INTO protection_relay_schemes (mrid, name, description, num_diagram_objects, system_mrid) VALUES ('mrid_2', null, null, null, null);",
        "INSERT INTO protection_relay_systems (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, protection_kind) VALUES ('mrid_2', null, null, null, null, null, null, null, null, 'protection_kind_2');",
        "INSERT INTO ratio_tap_changers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, control_enabled, high_step, low_step, neutral_step, neutral_u, normal_step, step, tap_changer_control_mrid, transformer_end_mrid, step_voltage_increment) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO reactive_capability_curves (mrid, name, description, num_diagram_objects) VALUES ('mrid_2', 'should_be_nullable', null, null);",
        "INSERT INTO reclosers (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, normal_open, open, rated_current, switch_info_mrid, breaking_capacity) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, 'normal_open_2', 'open_2', null, null, null);",
        "INSERT INTO relay_info (mrid, name, description, num_diagram_objects, curve_setting, reclose_fast) VALUES ('mrid_2', null, null, null, null, null);",
        "INSERT INTO remote_controls (mrid, name, description, num_diagram_objects, control_mrid) VALUES ('mrid_2', null, null, null, null);",
        "INSERT INTO remote_sources (mrid, name, description, num_diagram_objects, measurement_mrid) VALUES ('mrid_2', null, null, null, null);",
        "INSERT INTO series_compensators (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, r, r0, x, x0, varistor_rated_current, varistor_voltage_threshold) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO short_circuit_tests (mrid, name, description, num_diagram_objects, base_power, temperature, current, energised_end_step, grounded_end_step, leakage_impedance, leakage_impedance_zero, loss, loss_zero, power, voltage, voltage_ohmic_part) VALUES ('mrid_2', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO shunt_compensator_info (mrid, name, description, num_diagram_objects, max_power_loss, rated_current, rated_reactive_power, rated_voltage) VALUES ('mrid_2', null, null, null, null, null, null, null);",
        "INSERT INTO sites (mrid, name, description, num_diagram_objects, location_mrid, num_controls) VALUES ('mrid_2', null, null, null, null, null);",
        "INSERT INTO streetlights (mrid, name, description, num_diagram_objects, location_mrid, pole_mrid, lamp_kind, light_rating) VALUES ('mrid_2', null, null, null, null, null, 'lamp_kind_2', null);",
        "INSERT INTO sub_geographical_regions (mrid, name, description, num_diagram_objects, geographical_region_mrid) VALUES ('mrid_2', null, null, null, null);",
        "INSERT INTO substations (mrid, name, description, num_diagram_objects, location_mrid, num_controls, sub_geographical_region_mrid) VALUES ('mrid_2', null, null, null, null, null, null);",
        "INSERT INTO static_var_compensators (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, control_enabled, regulating_control_mrid, capacitive_rating, inductive_rating, q, svc_control_mode, voltage_set_point) VALUES ('mrid_2', 'should_be_nullable', null, null, null, null, null, null, null, null, null, null, null, null, null, 'svc_control_mode_2', null);",
        "INSERT INTO switch_info (mrid, name, description, num_diagram_objects, rated_interrupting_time) VALUES ('mrid_2', null, null, null, null);",
        "INSERT INTO synchronous_machines (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, commissioned_date, base_voltage_mrid, control_enabled, regulating_control_mrid, rated_power_factor, rated_s, rated_u, p, q, base_q, condenser_p, earthing, earthing_star_point_r, earthing_star_point_x, ikk, max_q, max_u, min_q, min_u, mu, r, r0, r2, sat_direct_subtrans_x, sat_direct_sync_x, sat_direct_trans_x, x0, x2, type, operating_mode) VALUES ('mrid_2', 'should_be_nullable', null, null, null, 'should_be_nullable', null, null, null, null, 'should_be_nullable', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 'type_2', 'operating_mode_2');",
        "INSERT INTO tap_changer_controls (mrid, name, description, num_diagram_objects, location_mrid, num_controls, discrete, mode, monitored_phase, target_deadband, target_value, enabled, max_allowed_target_value, min_allowed_target_value, rated_current, terminal_mrid, ct_primary, min_target_deadband, limit_voltage, line_drop_compensation, line_drop_r, line_drop_x, reverse_line_drop_r, reverse_line_drop_x, forward_ldc_blocking, time_delay, co_generation_enabled) VALUES ('mrid_2', null, null, null, null, null, null, 'mode_2', 'monitored_phase_2', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO terminals (mrid, name, description, num_diagram_objects, conducting_equipment_mrid, sequence_number, connectivity_node_mrid, phases) VALUES ('mrid_2', null, null, null, null, 'sequence_number_2', null, 'phases_2');",
        "INSERT INTO transformer_end_info (mrid, name, description, num_diagram_objects, connection_kind, emergency_s, end_number, insulation_u, phase_angle_clock, r, rated_s, rated_u, short_term_s, transformer_tank_info_mrid, energised_end_no_load_tests, energised_end_short_circuit_tests, grounded_end_short_circuit_tests, open_end_open_circuit_tests, energised_end_open_circuit_tests) VALUES ('mrid_2', null, null, null, 'connection_kind_2', null, 'end_number_2', null, null, null, null, null, null, null, null, null, null, null, null);",
        "INSERT INTO transformer_star_impedances (mrid, name, description, num_diagram_objects, R, R0, X, X0, transformer_end_info_mrid) VALUES ('mrid_2', null, null, null, null, null, null, null, null);",
        "INSERT INTO transformer_tank_info (mrid, name, description, num_diagram_objects, power_transformer_info_mrid) VALUES ('mrid_2', null, null, null, null);",
        "INSERT INTO usage_points (mrid, name, description, num_diagram_objects, location_mrid, is_virtual, connection_category, rated_power, approved_inverter_capacity, phase_code) VALUES ('mrid_2', null, null, null, null, null, null, null, null, 'phase_code_2');",
        "INSERT INTO voltage_relays (mrid, name, description, num_diagram_objects, location_mrid, num_controls, model, reclosing, relay_delay_time, protection_kind, directable, power_direction, relay_info_mrid) VALUES ('mrid_2', null, null, null, null, null, null, null, null, 'protection_kind_2', null, 'power_direction_2', null);",

        // Array data tables.
        "INSERT INTO location_street_addresses (town_name, state_or_province, postal_code, po_box, building_name, floor_identification, name, number, suite_number, type, display_address, location_mrid, address_field) VALUES (null, null, null, null, null, null, null, null, null, null, null, 'location_mrid_2', 'address_field_2');",
        "INSERT INTO phase_impedance_data (per_length_phase_impedance_mrid, from_phase, to_phase, b, g, r, x) VALUES ('per_length_phase_impedance_mrid_2', 'from_phase_2', 'to_phase_2', null, null, null, null);",
        "INSERT INTO protection_relay_function_thresholds (protection_relay_function_mrid, sequence_number, unit_symbol, value, name) VALUES ('protection_relay_function_mrid_2', 'sequence_number_2', 'unit_symbol_2', 'value_2', null);",
    )

    override fun validateChanges(statement: Statement) {
        // Identified object tables.
        `validate ac_line_segments`(statement)
        `validate accumulators`(statement)
        `validate analogs`(statement)
        `validate asset_owners`(statement)
        `validate base_voltages`(statement)
        `validate battery_controls`(statement)
        `validate battery_units`(statement)
        `validate breakers`(statement)
        `validate busbar_sections`(statement)
        `validate cable_info`(statement)
        `validate circuits`(statement)
        `validate clamps`(statement)
        `validate connectivity_nodes`(statement)
        `validate controls`(statement)
        `validate current_relays`(statement)
        `validate current_transformer_info`(statement)
        `validate current_transformers`(statement)
        `validate cuts`(statement)
        `validate disconnectors`(statement)
        `validate discretes`(statement)
        `validate distance_relays`(statement)
        `validate energy_consumer_phases`(statement)
        `validate energy_consumers`(statement)
        `validate energy_source_phases`(statement)
        `validate energy_sources`(statement)
        `validate equivalent_branches`(statement)
        `validate ev_charging_units`(statement)
        `validate fault_indicators`(statement)
        `validate feeders`(statement)
        `validate fuses`(statement)
        `validate geographical_regions`(statement)
        `validate grounds`(statement)
        `validate ground_disconnectors`(statement)
        `validate grounding_impedances`(statement)
        `validate jumpers`(statement)
        `validate junctions`(statement)
        `validate linear_shunt_compensators`(statement)
        `validate load_break_switches`(statement)
        `validate locations`(statement)
        `validate loops`(statement)
        `validate lv_feeders`(statement)
        `validate meters`(statement)
        `validate no_load_tests`(statement)
        `validate open_circuit_tests`(statement)
        `validate operational_restrictions`(statement)
        `validate organisations`(statement)
        `validate overhead_wire_info`(statement)
        `validate pan_demand_response_functions`(statement)
        `validate per_length_phase_impedances`(statement)
        `validate per_length_sequence_impedances`(statement)
        `validate petersen_coils`(statement)
        `validate photo_voltaic_units`(statement)
        `validate poles`(statement)
        `validate potential_transformer_info`(statement)
        `validate potential_transformers`(statement)
        `validate power_electronics_connections`(statement)
        `validate power_electronics_connection_phases`(statement)
        `validate power_electronics_wind_units`(statement)
        `validate power_transformer_ends`(statement)
        `validate power_transformer_info`(statement)
        `validate power_transformers`(statement)
        `validate protection_relay_schemes`(statement)
        `validate protection_relay_systems`(statement)
        `validate ratio_tap_changers`(statement)
        `validate reactive_capability_curves`(statement)
        `validate reclosers`(statement)
        `validate relay_info`(statement)
        `validate remote_controls`(statement)
        `validate remote_sources`(statement)
        `validate series_compensators`(statement)
        `validate short_circuit_tests`(statement)
        `validate shunt_compensator_info`(statement)
        `validate sites`(statement)
        `validate streetlights`(statement)
        `validate sub_geographical_regions`(statement)
        `validate substations`(statement)
        `validate static_var_compensators`(statement)
        `validate switch_info`(statement)
        `validate synchronous_machines`(statement)
        `validate tap_changer_controls`(statement)
        `validate terminals`(statement)
        `validate transformer_end_info`(statement)
        `validate transformer_star_impedances`(statement)
        `validate transformer_tank_info`(statement)
        `validate usage_points`(statement)
        `validate voltage_relays`(statement)

        // Array data tables.
        `validate location_street_addresses`(statement)
        `validate phase_impedance_data`(statement)
        `validate protection_relay_function_thresholds`(statement)
    }

    override fun tearDownStatements(): List<String> =
        listOf(
            // Identified object tables.
            "DELETE FROM ac_line_segments;",
            "DELETE FROM accumulators;",
            "DELETE FROM analogs;",
            "DELETE FROM asset_owners;",
            "DELETE FROM base_voltages;",
            "DELETE FROM battery_controls;",
            "DELETE FROM battery_units;",
            "DELETE FROM breakers;",
            "DELETE FROM busbar_sections;",
            "DELETE FROM cable_info;",
            "DELETE FROM circuits;",
            "DELETE FROM clamps;",
            "DELETE FROM connectivity_nodes;",
            "DELETE FROM controls;",
            "DELETE FROM current_relays;",
            "DELETE FROM current_transformer_info;",
            "DELETE FROM current_transformers;",
            "DELETE FROM cuts;",
            "DELETE FROM disconnectors;",
            "DELETE FROM discretes;",
            "DELETE FROM distance_relays;",
            "DELETE FROM energy_consumer_phases;",
            "DELETE FROM energy_consumers;",
            "DELETE FROM energy_source_phases;",
            "DELETE FROM energy_sources;",
            "DELETE FROM equivalent_branches;",
            "DELETE FROM ev_charging_units;",
            "DELETE FROM fault_indicators;",
            "DELETE FROM feeders;",
            "DELETE FROM fuses;",
            "DELETE FROM geographical_regions;",
            "DELETE FROM grounds;",
            "DELETE FROM ground_disconnectors;",
            "DELETE FROM grounding_impedances;",
            "DELETE FROM jumpers;",
            "DELETE FROM junctions;",
            "DELETE FROM linear_shunt_compensators;",
            "DELETE FROM load_break_switches;",
            "DELETE FROM locations;",
            "DELETE FROM loops;",
            "DELETE FROM lv_feeders;",
            "DELETE FROM meters;",
            "DELETE FROM no_load_tests;",
            "DELETE FROM open_circuit_tests;",
            "DELETE FROM operational_restrictions;",
            "DELETE FROM organisations;",
            "DELETE FROM overhead_wire_info;",
            "DELETE FROM pan_demand_response_functions;",
            "DELETE FROM per_length_phase_impedances;",
            "DELETE FROM per_length_sequence_impedances;",
            "DELETE FROM petersen_coils;",
            "DELETE FROM photo_voltaic_units;",
            "DELETE FROM poles;",
            "DELETE FROM potential_transformer_info;",
            "DELETE FROM potential_transformers;",
            "DELETE FROM power_electronics_connections;",
            "DELETE FROM power_electronics_connection_phases;",
            "DELETE FROM power_electronics_wind_units;",
            "DELETE FROM power_transformer_ends;",
            "DELETE FROM power_transformer_info;",
            "DELETE FROM power_transformers;",
            "DELETE FROM protection_relay_schemes;",
            "DELETE FROM protection_relay_systems;",
            "DELETE FROM ratio_tap_changers;",
            "DELETE FROM reactive_capability_curves;",
            "DELETE FROM reclosers;",
            "DELETE FROM relay_info;",
            "DELETE FROM remote_controls;",
            "DELETE FROM remote_sources;",
            "DELETE FROM series_compensators;",
            "DELETE FROM short_circuit_tests;",
            "DELETE FROM shunt_compensator_info;",
            "DELETE FROM sites;",
            "DELETE FROM streetlights;",
            "DELETE FROM sub_geographical_regions;",
            "DELETE FROM substations;",
            "DELETE FROM static_var_compensators;",
            "DELETE FROM switch_info;",
            "DELETE FROM synchronous_machines;",
            "DELETE FROM tap_changer_controls;",
            "DELETE FROM terminals;",
            "DELETE FROM transformer_end_info;",
            "DELETE FROM transformer_star_impedances;",
            "DELETE FROM transformer_tank_info;",
            "DELETE FROM usage_points;",
            "DELETE FROM voltage_relays;",

            // Array data tables.
            "DELETE FROM location_street_addresses;",
            "DELETE FROM phase_impedance_data;",
            "DELETE FROM protection_relay_function_thresholds;",
        )

    // Identified object tables.
    private fun `validate ac_line_segments`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM ac_line_segments;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("length"), equalTo("length_1"))
                assertThat(rs.getString("design_temperature"), equalTo("design_temperature_1"))
                assertThat(rs.getString("design_rating"), equalTo("design_rating_1"))
                assertThat(rs.getString("wire_info_mrid"), equalTo("wire_info_mrid_1"))
                assertThat(rs.getString("per_length_impedance_mrid"), equalTo("per_length_impedance_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getNullableString("length"), nullValue())
                assertThat(rs.getNullableString("design_temperature"), nullValue())
                assertThat(rs.getNullableString("design_rating"), nullValue())
                assertThat(rs.getNullableString("wire_info_mrid"), nullValue())
                assertThat(rs.getNullableString("per_length_impedance_mrid"), nullValue())
            }
        )
    }

    private fun `validate accumulators`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM accumulators;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("power_system_resource_mrid"), equalTo("power_system_resource_mrid_1"))
                assertThat(rs.getString("remote_source_mrid"), equalTo("remote_source_mrid_1"))
                assertThat(rs.getString("terminal_mrid"), equalTo("terminal_mrid_1"))
                assertThat(rs.getString("phases"), equalTo("phases_1"))
                assertThat(rs.getString("unit_symbol"), equalTo("unit_symbol_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("power_system_resource_mrid"), nullValue())
                assertThat(rs.getNullableString("remote_source_mrid"), nullValue())
                assertThat(rs.getNullableString("terminal_mrid"), nullValue())
                assertThat(rs.getString("phases"), equalTo("phases_2"))
                assertThat(rs.getString("unit_symbol"), equalTo("unit_symbol_2"))
            }
        )
    }

    private fun `validate analogs`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM analogs;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("power_system_resource_mrid"), equalTo("power_system_resource_mrid_1"))
                assertThat(rs.getString("remote_source_mrid"), equalTo("remote_source_mrid_1"))
                assertThat(rs.getString("terminal_mrid"), equalTo("terminal_mrid_1"))
                assertThat(rs.getString("phases"), equalTo("phases_1"))
                assertThat(rs.getString("unit_symbol"), equalTo("unit_symbol_1"))
                assertThat(rs.getString("positive_flow_in"), equalTo("positive_flow_in_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("power_system_resource_mrid"), nullValue())
                assertThat(rs.getNullableString("remote_source_mrid"), nullValue())
                assertThat(rs.getNullableString("terminal_mrid"), nullValue())
                assertThat(rs.getString("phases"), equalTo("phases_2"))
                assertThat(rs.getString("unit_symbol"), equalTo("unit_symbol_2"))
                assertThat(rs.getNullableString("positive_flow_in"), nullValue())
            }
        )
    }

    private fun `validate asset_owners`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM asset_owners;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("organisation_mrid"), equalTo("organisation_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("organisation_mrid"), nullValue())
            }
        )
    }

    private fun `validate base_voltages`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM base_voltages;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("nominal_voltage"), equalTo("nominal_voltage_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getString("nominal_voltage"), equalTo("nominal_voltage_2"))
            }
        )
    }

    private fun `validate battery_controls`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM battery_controls;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("discrete"), equalTo("discrete_1"))
                assertThat(rs.getString("mode"), equalTo("mode_1"))
                assertThat(rs.getString("monitored_phase"), equalTo("monitored_phase_1"))
                assertThat(rs.getString("target_deadband"), equalTo("target_deadband_1"))
                assertThat(rs.getString("target_value"), equalTo("target_value_1"))
                assertThat(rs.getString("enabled"), equalTo("enabled_1"))
                assertThat(rs.getString("max_allowed_target_value"), equalTo("max_allowed_target_value_1"))
                assertThat(rs.getString("min_allowed_target_value"), equalTo("min_allowed_target_value_1"))
                assertThat(rs.getString("rated_current"), equalTo("rated_current_1"))
                assertThat(rs.getString("terminal_mrid"), equalTo("terminal_mrid_1"))
                assertThat(rs.getString("ct_primary"), equalTo("ct_primary_1"))
                assertThat(rs.getString("min_target_deadband"), equalTo("min_target_deadband_1"))
                assertThat(rs.getString("charging_rate"), equalTo("charging_rate_1"))
                assertThat(rs.getString("discharging_rate"), equalTo("discharging_rate_1"))
                assertThat(rs.getString("reserve_percent"), equalTo("reserve_percent_1"))
                assertThat(rs.getString("control_mode"), equalTo("control_mode_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getString("name"), equalTo("should_be_nullable"))
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("discrete"), nullValue())
                assertThat(rs.getString("mode"), equalTo("mode_2"))
                assertThat(rs.getString("monitored_phase"), equalTo("monitored_phase_2"))
                assertThat(rs.getNullableString("target_deadband"), nullValue())
                assertThat(rs.getNullableString("target_value"), nullValue())
                assertThat(rs.getNullableString("enabled"), nullValue())
                assertThat(rs.getNullableString("max_allowed_target_value"), nullValue())
                assertThat(rs.getNullableString("min_allowed_target_value"), nullValue())
                assertThat(rs.getNullableString("rated_current"), nullValue())
                assertThat(rs.getNullableString("terminal_mrid"), nullValue())
                assertThat(rs.getNullableString("ct_primary"), nullValue())
                assertThat(rs.getNullableString("min_target_deadband"), nullValue())
                assertThat(rs.getNullableString("charging_rate"), nullValue())
                assertThat(rs.getNullableString("discharging_rate"), nullValue())
                assertThat(rs.getNullableString("reserve_percent"), nullValue())
                assertThat(rs.getString("control_mode"), equalTo("control_mode_2"))
            }
        )
    }

    private fun `validate battery_units`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM battery_units;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("power_electronics_connection_mrid_1"))
                assertThat(rs.getString("max_p"), equalTo("max_p_1"))
                assertThat(rs.getString("min_p"), equalTo("min_p_1"))
                assertThat(rs.getString("battery_state"), equalTo("battery_state_1"))
                assertThat(rs.getString("rated_e"), equalTo("rated_e_1"))
                assertThat(rs.getString("stored_e"), equalTo("stored_e_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("power_electronics_connection_mrid"), nullValue())
                assertThat(rs.getNullableString("max_p"), nullValue())
                assertThat(rs.getNullableString("min_p"), nullValue())
                assertThat(rs.getString("battery_state"), equalTo("battery_state_2"))
                assertThat(rs.getNullableString("rated_e"), nullValue())
                assertThat(rs.getNullableString("stored_e"), nullValue())
            }
        )
    }

    private fun `validate breakers`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM breakers;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("normal_open"), equalTo("normal_open_1"))
                assertThat(rs.getString("open"), equalTo("open_1"))
                assertThat(rs.getString("rated_current"), equalTo("rated_current_1"))
                assertThat(rs.getString("switch_info_mrid"), equalTo("switch_info_mrid_1"))
                assertThat(rs.getString("breaking_capacity"), equalTo("breaking_capacity_1"))
                assertThat(rs.getString("in_transit_time"), equalTo("in_transit_time_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getString("normal_open"), equalTo("normal_open_2"))
                assertThat(rs.getString("open"), equalTo("open_2"))
                assertThat(rs.getNullableString("rated_current"), nullValue())
                assertThat(rs.getNullableString("switch_info_mrid"), nullValue())
                assertThat(rs.getNullableString("breaking_capacity"), nullValue())
                assertThat(rs.getNullableString("in_transit_time"), nullValue())
            }
        )
    }

    private fun `validate busbar_sections`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM busbar_sections;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
            }
        )
    }

    private fun `validate cable_info`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM cable_info;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("rated_current"), equalTo("rated_current_1"))
                assertThat(rs.getString("material"), equalTo("material_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("rated_current"), nullValue())
                assertThat(rs.getString("material"), equalTo("material_2"))
            }
        )
    }

    private fun `validate circuits`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM circuits;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("loop_mrid"), equalTo("loop_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("loop_mrid"), nullValue())
            }
        )
    }

    private fun `validate clamps`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM clamps;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("length_from_terminal_1"), equalTo("length_from_terminal_1_1"))
                assertThat(rs.getString("ac_line_segment_mrid"), equalTo("ac_line_segment_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getString("name"), equalTo("should_be_nullable"))
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getNullableString("length_from_terminal_1"), nullValue())
                assertThat(rs.getNullableString("ac_line_segment_mrid"), nullValue())
            }
        )
    }

    private fun `validate connectivity_nodes`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM connectivity_nodes;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
            }
        )
    }

    private fun `validate controls`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM controls;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("power_system_resource_mrid"), equalTo("power_system_resource_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("power_system_resource_mrid"), nullValue())
            }
        )
    }

    private fun `validate current_relays`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM current_relays;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("model"), equalTo("model_1"))
                assertThat(rs.getString("reclosing"), equalTo("reclosing_1"))
                assertThat(rs.getString("relay_delay_time"), equalTo("relay_delay_time_1"))
                assertThat(rs.getString("protection_kind"), equalTo("protection_kind_1"))
                assertThat(rs.getString("directable"), equalTo("directable_1"))
                assertThat(rs.getString("power_direction"), equalTo("power_direction_1"))
                assertThat(rs.getString("relay_info_mrid"), equalTo("relay_info_mrid_1"))
                assertThat(rs.getString("current_limit_1"), equalTo("current_limit_1_1"))
                assertThat(rs.getString("inverse_time_flag"), equalTo("inverse_time_flag_1"))
                assertThat(rs.getString("time_delay_1"), equalTo("time_delay_1_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("model"), nullValue())
                assertThat(rs.getNullableString("reclosing"), nullValue())
                assertThat(rs.getNullableString("relay_delay_time"), nullValue())
                assertThat(rs.getString("protection_kind"), equalTo("protection_kind_2"))
                assertThat(rs.getNullableString("directable"), nullValue())
                assertThat(rs.getString("power_direction"), equalTo("power_direction_2"))
                assertThat(rs.getNullableString("relay_info_mrid"), nullValue())
                assertThat(rs.getNullableString("current_limit_1"), nullValue())
                assertThat(rs.getNullableString("inverse_time_flag"), nullValue())
                assertThat(rs.getNullableString("time_delay_1"), nullValue())
            }
        )
    }

    private fun `validate current_transformer_info`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM current_transformer_info;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("accuracy_class"), equalTo("accuracy_class_1"))
                assertThat(rs.getString("accuracy_limit"), equalTo("accuracy_limit_1"))
                assertThat(rs.getString("core_count"), equalTo("core_count_1"))
                assertThat(rs.getString("ct_class"), equalTo("ct_class_1"))
                assertThat(rs.getString("knee_point_voltage"), equalTo("knee_point_voltage_1"))
                assertThat(rs.getString("max_ratio_denominator"), equalTo("max_ratio_denominator_1"))
                assertThat(rs.getString("max_ratio_numerator"), equalTo("max_ratio_numerator_1"))
                assertThat(rs.getString("nominal_ratio_denominator"), equalTo("nominal_ratio_denominator_1"))
                assertThat(rs.getString("nominal_ratio_numerator"), equalTo("nominal_ratio_numerator_1"))
                assertThat(rs.getString("primary_ratio"), equalTo("primary_ratio_1"))
                assertThat(rs.getString("rated_current"), equalTo("rated_current_1"))
                assertThat(rs.getString("secondary_fls_rating"), equalTo("secondary_fls_rating_1"))
                assertThat(rs.getString("secondary_ratio"), equalTo("secondary_ratio_1"))
                assertThat(rs.getString("usage"), equalTo("usage_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("accuracy_class"), nullValue())
                assertThat(rs.getNullableString("accuracy_limit"), nullValue())
                assertThat(rs.getNullableString("core_count"), nullValue())
                assertThat(rs.getNullableString("ct_class"), nullValue())
                assertThat(rs.getNullableString("knee_point_voltage"), nullValue())
                assertThat(rs.getNullableString("max_ratio_denominator"), nullValue())
                assertThat(rs.getNullableString("max_ratio_numerator"), nullValue())
                assertThat(rs.getNullableString("nominal_ratio_denominator"), nullValue())
                assertThat(rs.getNullableString("nominal_ratio_numerator"), nullValue())
                assertThat(rs.getNullableString("primary_ratio"), nullValue())
                assertThat(rs.getNullableString("rated_current"), nullValue())
                assertThat(rs.getNullableString("secondary_fls_rating"), nullValue())
                assertThat(rs.getNullableString("secondary_ratio"), nullValue())
                assertThat(rs.getNullableString("usage"), nullValue())
            }
        )
    }

    private fun `validate current_transformers`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM current_transformers;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("terminal_mrid"), equalTo("terminal_mrid_1"))
                assertThat(rs.getString("current_transformer_info_mrid"), equalTo("current_transformer_info_mrid_1"))
                assertThat(rs.getString("core_burden"), equalTo("core_burden_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("terminal_mrid"), nullValue())
                assertThat(rs.getNullableString("current_transformer_info_mrid"), nullValue())
                assertThat(rs.getNullableString("core_burden"), nullValue())
            }
        )
    }

    private fun `validate cuts`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM cuts;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("normal_open"), equalTo("normal_open_1"))
                assertThat(rs.getString("open"), equalTo("open_1"))
                assertThat(rs.getString("rated_current"), equalTo("rated_current_1"))
                assertThat(rs.getString("switch_info_mrid"), equalTo("switch_info_mrid_1"))
                assertThat(rs.getString("length_from_terminal_1"), equalTo("length_from_terminal_1_1"))
                assertThat(rs.getString("ac_line_segment_mrid"), equalTo("ac_line_segment_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getString("name"), equalTo("should_be_nullable"))
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getString("normal_open"), equalTo("normal_open_2"))
                assertThat(rs.getString("open"), equalTo("open_2"))
                assertThat(rs.getNullableString("rated_current"), nullValue())
                assertThat(rs.getNullableString("switch_info_mrid"), nullValue())
                assertThat(rs.getNullableString("length_from_terminal_1"), nullValue())
                assertThat(rs.getNullableString("ac_line_segment_mrid"), nullValue())
            }
        )
    }

    private fun `validate disconnectors`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM disconnectors;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("normal_open"), equalTo("normal_open_1"))
                assertThat(rs.getString("open"), equalTo("open_1"))
                assertThat(rs.getString("rated_current"), equalTo("rated_current_1"))
                assertThat(rs.getString("switch_info_mrid"), equalTo("switch_info_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getString("normal_open"), equalTo("normal_open_2"))
                assertThat(rs.getString("open"), equalTo("open_2"))
                assertThat(rs.getNullableString("rated_current"), nullValue())
                assertThat(rs.getNullableString("switch_info_mrid"), nullValue())
            }
        )
    }

    private fun `validate discretes`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM discretes;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("power_system_resource_mrid"), equalTo("power_system_resource_mrid_1"))
                assertThat(rs.getString("remote_source_mrid"), equalTo("remote_source_mrid_1"))
                assertThat(rs.getString("terminal_mrid"), equalTo("terminal_mrid_1"))
                assertThat(rs.getString("phases"), equalTo("phases_1"))
                assertThat(rs.getString("unit_symbol"), equalTo("unit_symbol_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("power_system_resource_mrid"), nullValue())
                assertThat(rs.getNullableString("remote_source_mrid"), nullValue())
                assertThat(rs.getNullableString("terminal_mrid"), nullValue())
                assertThat(rs.getString("phases"), equalTo("phases_2"))
                assertThat(rs.getString("unit_symbol"), equalTo("unit_symbol_2"))
            }
        )
    }

    private fun `validate distance_relays`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM distance_relays;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("model"), equalTo("model_1"))
                assertThat(rs.getString("reclosing"), equalTo("reclosing_1"))
                assertThat(rs.getString("relay_delay_time"), equalTo("relay_delay_time_1"))
                assertThat(rs.getString("protection_kind"), equalTo("protection_kind_1"))
                assertThat(rs.getString("directable"), equalTo("directable_1"))
                assertThat(rs.getString("power_direction"), equalTo("power_direction_1"))
                assertThat(rs.getString("relay_info_mrid"), equalTo("relay_info_mrid_1"))
                assertThat(rs.getString("backward_blind"), equalTo("backward_blind_1"))
                assertThat(rs.getString("backward_reach"), equalTo("backward_reach_1"))
                assertThat(rs.getString("backward_reactance"), equalTo("backward_reactance_1"))
                assertThat(rs.getString("forward_blind"), equalTo("forward_blind_1"))
                assertThat(rs.getString("forward_reach"), equalTo("forward_reach_1"))
                assertThat(rs.getString("forward_reactance"), equalTo("forward_reactance_1"))
                assertThat(rs.getString("operation_phase_angle1"), equalTo("operation_phase_angle1_1"))
                assertThat(rs.getString("operation_phase_angle2"), equalTo("operation_phase_angle2_1"))
                assertThat(rs.getString("operation_phase_angle3"), equalTo("operation_phase_angle3_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("model"), nullValue())
                assertThat(rs.getNullableString("reclosing"), nullValue())
                assertThat(rs.getNullableString("relay_delay_time"), nullValue())
                assertThat(rs.getString("protection_kind"), equalTo("protection_kind_2"))
                assertThat(rs.getNullableString("directable"), nullValue())
                assertThat(rs.getString("power_direction"), equalTo("power_direction_2"))
                assertThat(rs.getNullableString("relay_info_mrid"), nullValue())
                assertThat(rs.getNullableString("backward_blind"), nullValue())
                assertThat(rs.getNullableString("backward_reach"), nullValue())
                assertThat(rs.getNullableString("backward_reactance"), nullValue())
                assertThat(rs.getNullableString("forward_blind"), nullValue())
                assertThat(rs.getNullableString("forward_reach"), nullValue())
                assertThat(rs.getNullableString("forward_reactance"), nullValue())
                assertThat(rs.getNullableString("operation_phase_angle1"), nullValue())
                assertThat(rs.getNullableString("operation_phase_angle2"), nullValue())
                assertThat(rs.getNullableString("operation_phase_angle3"), nullValue())
            }
        )
    }

    private fun `validate energy_consumer_phases`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM energy_consumer_phases;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("energy_consumer_mrid"), equalTo("energy_consumer_mrid_1"))
                assertThat(rs.getString("phase"), equalTo("phase_1"))
                assertThat(rs.getString("p"), equalTo("p_1"))
                assertThat(rs.getString("q"), equalTo("q_1"))
                assertThat(rs.getString("p_fixed"), equalTo("p_fixed_1"))
                assertThat(rs.getString("q_fixed"), equalTo("q_fixed_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getString("energy_consumer_mrid"), equalTo("energy_consumer_mrid_2"))
                assertThat(rs.getString("phase"), equalTo("phase_2"))
                assertThat(rs.getNullableString("p"), nullValue())
                assertThat(rs.getNullableString("q"), nullValue())
                assertThat(rs.getNullableString("p_fixed"), nullValue())
                assertThat(rs.getNullableString("q_fixed"), nullValue())
            }
        )
    }

    private fun `validate energy_consumers`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM energy_consumers;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("customer_count"), equalTo("customer_count_1"))
                assertThat(rs.getString("grounded"), equalTo("grounded_1"))
                assertThat(rs.getString("p"), equalTo("p_1"))
                assertThat(rs.getString("q"), equalTo("q_1"))
                assertThat(rs.getString("p_fixed"), equalTo("p_fixed_1"))
                assertThat(rs.getString("q_fixed"), equalTo("q_fixed_1"))
                assertThat(rs.getString("phase_connection"), equalTo("phase_connection_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getNullableString("customer_count"), nullValue())
                assertThat(rs.getNullableString("grounded"), nullValue())
                assertThat(rs.getNullableString("p"), nullValue())
                assertThat(rs.getNullableString("q"), nullValue())
                assertThat(rs.getNullableString("p_fixed"), nullValue())
                assertThat(rs.getNullableString("q_fixed"), nullValue())
                assertThat(rs.getString("phase_connection"), equalTo("phase_connection_2"))
            }
        )
    }

    private fun `validate energy_source_phases`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM energy_source_phases;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("energy_source_mrid"), equalTo("energy_source_mrid_1"))
                assertThat(rs.getString("phase"), equalTo("phase_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getString("energy_source_mrid"), equalTo("energy_source_mrid_2"))
                assertThat(rs.getString("phase"), equalTo("phase_2"))
            }
        )
    }

    private fun `validate energy_sources`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM energy_sources;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("active_power"), equalTo("active_power_1"))
                assertThat(rs.getString("reactive_power"), equalTo("reactive_power_1"))
                assertThat(rs.getString("voltage_angle"), equalTo("voltage_angle_1"))
                assertThat(rs.getString("voltage_magnitude"), equalTo("voltage_magnitude_1"))
                assertThat(rs.getString("p_max"), equalTo("p_max_1"))
                assertThat(rs.getString("p_min"), equalTo("p_min_1"))
                assertThat(rs.getString("r"), equalTo("r_1"))
                assertThat(rs.getString("r0"), equalTo("r0_1"))
                assertThat(rs.getString("rn"), equalTo("rn_1"))
                assertThat(rs.getString("x"), equalTo("x_1"))
                assertThat(rs.getString("x0"), equalTo("x0_1"))
                assertThat(rs.getString("xn"), equalTo("xn_1"))
                assertThat(rs.getString("is_external_grid"), equalTo("is_external_grid_1"))
                assertThat(rs.getString("r_min"), equalTo("r_min_1"))
                assertThat(rs.getString("rn_min"), equalTo("rn_min_1"))
                assertThat(rs.getString("r0_min"), equalTo("r0_min_1"))
                assertThat(rs.getString("x_min"), equalTo("x_min_1"))
                assertThat(rs.getString("xn_min"), equalTo("xn_min_1"))
                assertThat(rs.getString("x0_min"), equalTo("x0_min_1"))
                assertThat(rs.getString("r_max"), equalTo("r_max_1"))
                assertThat(rs.getString("rn_max"), equalTo("rn_max_1"))
                assertThat(rs.getString("r0_max"), equalTo("r0_max_1"))
                assertThat(rs.getString("x_max"), equalTo("x_max_1"))
                assertThat(rs.getString("xn_max"), equalTo("xn_max_1"))
                assertThat(rs.getString("x0_max"), equalTo("x0_max_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getNullableString("active_power"), nullValue())
                assertThat(rs.getNullableString("reactive_power"), nullValue())
                assertThat(rs.getNullableString("voltage_angle"), nullValue())
                assertThat(rs.getNullableString("voltage_magnitude"), nullValue())
                assertThat(rs.getNullableString("p_max"), nullValue())
                assertThat(rs.getNullableString("p_min"), nullValue())
                assertThat(rs.getNullableString("r"), nullValue())
                assertThat(rs.getNullableString("r0"), nullValue())
                assertThat(rs.getNullableString("rn"), nullValue())
                assertThat(rs.getNullableString("x"), nullValue())
                assertThat(rs.getNullableString("x0"), nullValue())
                assertThat(rs.getNullableString("xn"), nullValue())
                assertThat(rs.getNullableString("is_external_grid"), nullValue())
                assertThat(rs.getNullableString("r_min"), nullValue())
                assertThat(rs.getNullableString("rn_min"), nullValue())
                assertThat(rs.getNullableString("r0_min"), nullValue())
                assertThat(rs.getNullableString("x_min"), nullValue())
                assertThat(rs.getNullableString("xn_min"), nullValue())
                assertThat(rs.getNullableString("x0_min"), nullValue())
                assertThat(rs.getNullableString("r_max"), nullValue())
                assertThat(rs.getNullableString("rn_max"), nullValue())
                assertThat(rs.getNullableString("r0_max"), nullValue())
                assertThat(rs.getNullableString("x_max"), nullValue())
                assertThat(rs.getNullableString("xn_max"), nullValue())
                assertThat(rs.getNullableString("x0_max"), nullValue())
            }
        )
    }

    private fun `validate equivalent_branches`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM equivalent_branches;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("negative_r12"), equalTo("negative_r12_1"))
                assertThat(rs.getString("negative_r21"), equalTo("negative_r21_1"))
                assertThat(rs.getString("negative_x12"), equalTo("negative_x12_1"))
                assertThat(rs.getString("negative_x21"), equalTo("negative_x21_1"))
                assertThat(rs.getString("positive_r12"), equalTo("positive_r12_1"))
                assertThat(rs.getString("positive_r21"), equalTo("positive_r21_1"))
                assertThat(rs.getString("positive_x12"), equalTo("positive_x12_1"))
                assertThat(rs.getString("positive_x21"), equalTo("positive_x21_1"))
                assertThat(rs.getString("r"), equalTo("r_1"))
                assertThat(rs.getString("r21"), equalTo("r21_1"))
                assertThat(rs.getString("x"), equalTo("x_1"))
                assertThat(rs.getString("x21"), equalTo("x21_1"))
                assertThat(rs.getString("zero_r12"), equalTo("zero_r12_1"))
                assertThat(rs.getString("zero_r21"), equalTo("zero_r21_1"))
                assertThat(rs.getString("zero_x12"), equalTo("zero_x12_1"))
                assertThat(rs.getString("zero_x21"), equalTo("zero_x21_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getNullableString("negative_r12"), nullValue())
                assertThat(rs.getNullableString("negative_r21"), nullValue())
                assertThat(rs.getNullableString("negative_x12"), nullValue())
                assertThat(rs.getNullableString("negative_x21"), nullValue())
                assertThat(rs.getNullableString("positive_r12"), nullValue())
                assertThat(rs.getNullableString("positive_r21"), nullValue())
                assertThat(rs.getNullableString("positive_x12"), nullValue())
                assertThat(rs.getNullableString("positive_x21"), nullValue())
                assertThat(rs.getNullableString("r"), nullValue())
                assertThat(rs.getNullableString("r21"), nullValue())
                assertThat(rs.getNullableString("x"), nullValue())
                assertThat(rs.getNullableString("x21"), nullValue())
                assertThat(rs.getNullableString("zero_r12"), nullValue())
                assertThat(rs.getNullableString("zero_r21"), nullValue())
                assertThat(rs.getNullableString("zero_x12"), nullValue())
                assertThat(rs.getNullableString("zero_x21"), nullValue())
            }
        )
    }

    private fun `validate ev_charging_units`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM ev_charging_units;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("power_electronics_connection_mrid_1"))
                assertThat(rs.getString("max_p"), equalTo("max_p_1"))
                assertThat(rs.getString("min_p"), equalTo("min_p_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("power_electronics_connection_mrid"), nullValue())
                assertThat(rs.getNullableString("max_p"), nullValue())
                assertThat(rs.getNullableString("min_p"), nullValue())
            }
        )
    }

    private fun `validate fault_indicators`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM fault_indicators;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("terminal_mrid"), equalTo("terminal_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("terminal_mrid"), nullValue())
            }
        )
    }

    private fun `validate feeders`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM feeders;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normal_head_terminal_mrid"), equalTo("normal_head_terminal_mrid_1"))
                assertThat(rs.getString("normal_energizing_substation_mrid"), equalTo("normal_energizing_substation_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normal_head_terminal_mrid"), nullValue())
                assertThat(rs.getNullableString("normal_energizing_substation_mrid"), nullValue())
            }
        )
    }

    private fun `validate fuses`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM fuses;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("normal_open"), equalTo("normal_open_1"))
                assertThat(rs.getString("open"), equalTo("open_1"))
                assertThat(rs.getString("rated_current"), equalTo("rated_current_1"))
                assertThat(rs.getString("switch_info_mrid"), equalTo("switch_info_mrid_1"))
                assertThat(rs.getString("function_mrid"), equalTo("function_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getString("normal_open"), equalTo("normal_open_2"))
                assertThat(rs.getString("open"), equalTo("open_2"))
                assertThat(rs.getNullableString("rated_current"), nullValue())
                assertThat(rs.getNullableString("switch_info_mrid"), nullValue())
                assertThat(rs.getNullableString("function_mrid"), nullValue())
            }
        )
    }

    private fun `validate geographical_regions`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM geographical_regions;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
            }
        )
    }

    private fun `validate grounds`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM grounds;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
            }
        )
    }

    private fun `validate ground_disconnectors`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM ground_disconnectors;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("normal_open"), equalTo("normal_open_1"))
                assertThat(rs.getString("open"), equalTo("open_1"))
                assertThat(rs.getString("rated_current"), equalTo("rated_current_1"))
                assertThat(rs.getString("switch_info_mrid"), equalTo("switch_info_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getString("normal_open"), equalTo("normal_open_2"))
                assertThat(rs.getString("open"), equalTo("open_2"))
                assertThat(rs.getNullableString("rated_current"), nullValue())
                assertThat(rs.getNullableString("switch_info_mrid"), nullValue())
            }
        )
    }

    private fun `validate grounding_impedances`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM grounding_impedances;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("r"), equalTo("r_1"))
                assertThat(rs.getString("x"), equalTo("x_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getString("name"), equalTo("should_be_nullable"))
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getNullableString("r"), nullValue())
                assertThat(rs.getNullableString("x"), nullValue())
            }
        )
    }

    private fun `validate jumpers`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM jumpers;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("normal_open"), equalTo("normal_open_1"))
                assertThat(rs.getString("open"), equalTo("open_1"))
                assertThat(rs.getString("rated_current"), equalTo("rated_current_1"))
                assertThat(rs.getString("switch_info_mrid"), equalTo("switch_info_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getString("normal_open"), equalTo("normal_open_2"))
                assertThat(rs.getString("open"), equalTo("open_2"))
                assertThat(rs.getNullableString("rated_current"), nullValue())
                assertThat(rs.getNullableString("switch_info_mrid"), nullValue())
            }
        )
    }

    private fun `validate junctions`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM junctions;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
            }
        )
    }

    private fun `validate linear_shunt_compensators`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM linear_shunt_compensators;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("control_enabled"), equalTo("control_enabled_1"))
                assertThat(rs.getString("regulating_control_mrid"), equalTo("regulating_control_mrid_1"))
                assertThat(rs.getString("shunt_compensator_info_mrid"), equalTo("shunt_compensator_info_mrid_1"))
                assertThat(rs.getString("grounded"), equalTo("grounded_1"))
                assertThat(rs.getString("nom_u"), equalTo("nom_u_1"))
                assertThat(rs.getString("phase_connection"), equalTo("phase_connection_1"))
                assertThat(rs.getString("sections"), equalTo("sections_1"))
                assertThat(rs.getString("b0_per_section"), equalTo("b0_per_section_1"))
                assertThat(rs.getString("b_per_section"), equalTo("b_per_section_1"))
                assertThat(rs.getString("g0_per_section"), equalTo("g0_per_section_1"))
                assertThat(rs.getString("g_per_section"), equalTo("g_per_section_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getNullableString("control_enabled"), nullValue())
                assertThat(rs.getNullableString("regulating_control_mrid"), nullValue())
                assertThat(rs.getNullableString("shunt_compensator_info_mrid"), nullValue())
                assertThat(rs.getNullableString("grounded"), nullValue())
                assertThat(rs.getNullableString("nom_u"), nullValue())
                assertThat(rs.getString("phase_connection"), equalTo("phase_connection_2"))
                assertThat(rs.getNullableString("sections"), nullValue())
                assertThat(rs.getNullableString("b0_per_section"), nullValue())
                assertThat(rs.getNullableString("b_per_section"), nullValue())
                assertThat(rs.getNullableString("g0_per_section"), nullValue())
                assertThat(rs.getNullableString("g_per_section"), nullValue())
            }
        )
    }

    private fun `validate load_break_switches`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM load_break_switches;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("normal_open"), equalTo("normal_open_1"))
                assertThat(rs.getString("open"), equalTo("open_1"))
                assertThat(rs.getString("rated_current"), equalTo("rated_current_1"))
                assertThat(rs.getString("switch_info_mrid"), equalTo("switch_info_mrid_1"))
                assertThat(rs.getString("breaking_capacity"), equalTo("breaking_capacity_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getString("normal_open"), equalTo("normal_open_2"))
                assertThat(rs.getString("open"), equalTo("open_2"))
                assertThat(rs.getNullableString("rated_current"), nullValue())
                assertThat(rs.getNullableString("switch_info_mrid"), nullValue())
                assertThat(rs.getNullableString("breaking_capacity"), nullValue())
            }
        )
    }

    private fun `validate locations`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM locations;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
            }
        )
    }

    private fun `validate loops`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM loops;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
            }
        )
    }

    private fun `validate lv_feeders`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM lv_feeders;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normal_head_terminal_mrid"), equalTo("normal_head_terminal_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normal_head_terminal_mrid"), nullValue())
            }
        )
    }

    private fun `validate meters`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM meters;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("customer_mrid"), equalTo("customer_mrid_1"))
                assertThat(rs.getString("service_location_mrid"), equalTo("service_location_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("customer_mrid"), nullValue())
                assertThat(rs.getNullableString("service_location_mrid"), nullValue())
            }
        )
    }

    private fun `validate no_load_tests`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM no_load_tests;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("base_power"), equalTo("base_power_1"))
                assertThat(rs.getString("temperature"), equalTo("temperature_1"))
                assertThat(rs.getString("energised_end_voltage"), equalTo("energised_end_voltage_1"))
                assertThat(rs.getString("exciting_current"), equalTo("exciting_current_1"))
                assertThat(rs.getString("exciting_current_zero"), equalTo("exciting_current_zero_1"))
                assertThat(rs.getString("loss"), equalTo("loss_1"))
                assertThat(rs.getString("loss_zero"), equalTo("loss_zero_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("base_power"), nullValue())
                assertThat(rs.getNullableString("temperature"), nullValue())
                assertThat(rs.getNullableString("energised_end_voltage"), nullValue())
                assertThat(rs.getNullableString("exciting_current"), nullValue())
                assertThat(rs.getNullableString("exciting_current_zero"), nullValue())
                assertThat(rs.getNullableString("loss"), nullValue())
                assertThat(rs.getNullableString("loss_zero"), nullValue())
            }
        )
    }

    private fun `validate open_circuit_tests`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM open_circuit_tests;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("base_power"), equalTo("base_power_1"))
                assertThat(rs.getString("temperature"), equalTo("temperature_1"))
                assertThat(rs.getString("energised_end_step"), equalTo("energised_end_step_1"))
                assertThat(rs.getString("energised_end_voltage"), equalTo("energised_end_voltage_1"))
                assertThat(rs.getString("open_end_step"), equalTo("open_end_step_1"))
                assertThat(rs.getString("open_end_voltage"), equalTo("open_end_voltage_1"))
                assertThat(rs.getString("phase_shift"), equalTo("phase_shift_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("base_power"), nullValue())
                assertThat(rs.getNullableString("temperature"), nullValue())
                assertThat(rs.getNullableString("energised_end_step"), nullValue())
                assertThat(rs.getNullableString("energised_end_voltage"), nullValue())
                assertThat(rs.getNullableString("open_end_step"), nullValue())
                assertThat(rs.getNullableString("open_end_voltage"), nullValue())
                assertThat(rs.getNullableString("phase_shift"), nullValue())
            }
        )
    }

    private fun `validate operational_restrictions`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM operational_restrictions;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("title"), equalTo("title_1"))
                assertThat(rs.getString("created_date_time"), equalTo("created_date_time_1"))
                assertThat(rs.getString("author_name"), equalTo("author_name_1"))
                assertThat(rs.getString("type"), equalTo("type_1"))
                assertThat(rs.getString("status"), equalTo("status_1"))
                assertThat(rs.getString("comment"), equalTo("comment_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("title"), nullValue())
                assertThat(rs.getNullableString("created_date_time"), nullValue())
                assertThat(rs.getNullableString("author_name"), nullValue())
                assertThat(rs.getNullableString("type"), nullValue())
                assertThat(rs.getNullableString("status"), nullValue())
                assertThat(rs.getString("comment"), equalTo("should_be_nullable"))
            }
        )
    }

    private fun `validate organisations`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM organisations;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
            }
        )
    }

    private fun `validate overhead_wire_info`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM overhead_wire_info;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("rated_current"), equalTo("rated_current_1"))
                assertThat(rs.getString("material"), equalTo("material_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("rated_current"), nullValue())
                assertThat(rs.getString("material"), equalTo("material_2"))
            }
        )
    }

    private fun `validate pan_demand_response_functions`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM pan_demand_response_functions;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("enabled"), equalTo("enabled_1"))
                assertThat(rs.getString("kind"), equalTo("kind_1"))
                assertThat(rs.getString("appliance"), equalTo("appliance_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getString("name"), equalTo("should_be_nullable"))
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("enabled"), nullValue())
                assertThat(rs.getNullableString("kind"), nullValue())
                assertThat(rs.getNullableString("appliance"), nullValue())
            }
        )
    }

    private fun `validate per_length_phase_impedances`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM per_length_phase_impedances;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getString("name"), equalTo("should_be_nullable"))
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
            }
        )
    }

    private fun `validate per_length_sequence_impedances`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM per_length_sequence_impedances;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("r"), equalTo("r_1"))
                assertThat(rs.getString("x"), equalTo("x_1"))
                assertThat(rs.getString("r0"), equalTo("r0_1"))
                assertThat(rs.getString("x0"), equalTo("x0_1"))
                assertThat(rs.getString("bch"), equalTo("bch_1"))
                assertThat(rs.getString("gch"), equalTo("gch_1"))
                assertThat(rs.getString("b0ch"), equalTo("b0ch_1"))
                assertThat(rs.getString("g0ch"), equalTo("g0ch_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("r"), nullValue())
                assertThat(rs.getNullableString("x"), nullValue())
                assertThat(rs.getNullableString("r0"), nullValue())
                assertThat(rs.getNullableString("x0"), nullValue())
                assertThat(rs.getNullableString("bch"), nullValue())
                assertThat(rs.getNullableString("gch"), nullValue())
                assertThat(rs.getNullableString("b0ch"), nullValue())
                assertThat(rs.getNullableString("g0ch"), nullValue())
            }
        )
    }

    private fun `validate petersen_coils`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM petersen_coils;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("r"), equalTo("r_1"))
                assertThat(rs.getString("x_ground_nominal"), equalTo("x_ground_nominal_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getString("name"), equalTo("should_be_nullable"))
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getNullableString("r"), nullValue())
                assertThat(rs.getNullableString("x_ground_nominal"), nullValue())
            }
        )
    }

    private fun `validate photo_voltaic_units`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM photo_voltaic_units;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("power_electronics_connection_mrid_1"))
                assertThat(rs.getString("max_p"), equalTo("max_p_1"))
                assertThat(rs.getString("min_p"), equalTo("min_p_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("power_electronics_connection_mrid"), nullValue())
                assertThat(rs.getNullableString("max_p"), nullValue())
                assertThat(rs.getNullableString("min_p"), nullValue())
            }
        )
    }

    private fun `validate poles`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM poles;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("classification"), equalTo("classification_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getString("classification"), equalTo("should_be_nullable"))
            }
        )
    }

    private fun `validate potential_transformer_info`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM potential_transformer_info;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("accuracy_class"), equalTo("accuracy_class_1"))
                assertThat(rs.getString("nominal_ratio_denominator"), equalTo("nominal_ratio_denominator_1"))
                assertThat(rs.getString("nominal_ratio_numerator"), equalTo("nominal_ratio_numerator_1"))
                assertThat(rs.getString("primary_ratio"), equalTo("primary_ratio_1"))
                assertThat(rs.getString("pt_class"), equalTo("pt_class_1"))
                assertThat(rs.getString("rated_voltage"), equalTo("rated_voltage_1"))
                assertThat(rs.getString("secondary_ratio"), equalTo("secondary_ratio_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("accuracy_class"), nullValue())
                assertThat(rs.getNullableString("nominal_ratio_denominator"), nullValue())
                assertThat(rs.getNullableString("nominal_ratio_numerator"), nullValue())
                assertThat(rs.getNullableString("primary_ratio"), nullValue())
                assertThat(rs.getNullableString("pt_class"), nullValue())
                assertThat(rs.getNullableString("rated_voltage"), nullValue())
                assertThat(rs.getNullableString("secondary_ratio"), nullValue())
            }
        )
    }

    private fun `validate potential_transformers`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM potential_transformers;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("terminal_mrid"), equalTo("terminal_mrid_1"))
                assertThat(rs.getString("potential_transformer_info_mrid"), equalTo("potential_transformer_info_mrid_1"))
                assertThat(rs.getString("type"), equalTo("type_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("terminal_mrid"), nullValue())
                assertThat(rs.getNullableString("potential_transformer_info_mrid"), nullValue())
                assertThat(rs.getString("type"), equalTo("type_2"))
            }
        )
    }

    private fun `validate power_electronics_connections`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM power_electronics_connections;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("control_enabled"), equalTo("control_enabled_1"))
                assertThat(rs.getString("regulating_control_mrid"), equalTo("regulating_control_mrid_1"))
                assertThat(rs.getString("max_i_fault"), equalTo("max_i_fault_1"))
                assertThat(rs.getString("max_q"), equalTo("max_q_1"))
                assertThat(rs.getString("min_q"), equalTo("min_q_1"))
                assertThat(rs.getString("p"), equalTo("p_1"))
                assertThat(rs.getString("q"), equalTo("q_1"))
                assertThat(rs.getString("rated_s"), equalTo("rated_s_1"))
                assertThat(rs.getString("rated_u"), equalTo("rated_u_1"))
                assertThat(rs.getString("inverter_standard"), equalTo("inverter_standard_1"))
                assertThat(rs.getString("sustain_op_overvolt_limit"), equalTo("sustain_op_overvolt_limit_1"))
                assertThat(rs.getString("stop_at_over_freq"), equalTo("stop_at_over_freq_1"))
                assertThat(rs.getString("stop_at_under_freq"), equalTo("stop_at_under_freq_1"))
                assertThat(rs.getString("inv_volt_watt_resp_mode"), equalTo("inv_volt_watt_resp_mode_1"))
                assertThat(rs.getString("inv_watt_resp_v1"), equalTo("inv_watt_resp_v1_1"))
                assertThat(rs.getString("inv_watt_resp_v2"), equalTo("inv_watt_resp_v2_1"))
                assertThat(rs.getString("inv_watt_resp_v3"), equalTo("inv_watt_resp_v3_1"))
                assertThat(rs.getString("inv_watt_resp_v4"), equalTo("inv_watt_resp_v4_1"))
                assertThat(rs.getString("inv_watt_resp_p_at_v1"), equalTo("inv_watt_resp_p_at_v1_1"))
                assertThat(rs.getString("inv_watt_resp_p_at_v2"), equalTo("inv_watt_resp_p_at_v2_1"))
                assertThat(rs.getString("inv_watt_resp_p_at_v3"), equalTo("inv_watt_resp_p_at_v3_1"))
                assertThat(rs.getString("inv_watt_resp_p_at_v4"), equalTo("inv_watt_resp_p_at_v4_1"))
                assertThat(rs.getString("inv_volt_var_resp_mode"), equalTo("inv_volt_var_resp_mode_1"))
                assertThat(rs.getString("inv_var_resp_v1"), equalTo("inv_var_resp_v1_1"))
                assertThat(rs.getString("inv_var_resp_v2"), equalTo("inv_var_resp_v2_1"))
                assertThat(rs.getString("inv_var_resp_v3"), equalTo("inv_var_resp_v3_1"))
                assertThat(rs.getString("inv_var_resp_v4"), equalTo("inv_var_resp_v4_1"))
                assertThat(rs.getString("inv_var_resp_q_at_v1"), equalTo("inv_var_resp_q_at_v1_1"))
                assertThat(rs.getString("inv_var_resp_q_at_v2"), equalTo("inv_var_resp_q_at_v2_1"))
                assertThat(rs.getString("inv_var_resp_q_at_v3"), equalTo("inv_var_resp_q_at_v3_1"))
                assertThat(rs.getString("inv_var_resp_q_at_v4"), equalTo("inv_var_resp_q_at_v4_1"))
                assertThat(rs.getString("inv_reactive_power_mode"), equalTo("inv_reactive_power_mode_1"))
                assertThat(rs.getString("inv_fix_reactive_power"), equalTo("inv_fix_reactive_power_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getNullableString("control_enabled"), nullValue())
                assertThat(rs.getNullableString("regulating_control_mrid"), nullValue())
                assertThat(rs.getNullableString("max_i_fault"), nullValue())
                assertThat(rs.getNullableString("max_q"), nullValue())
                assertThat(rs.getNullableString("min_q"), nullValue())
                assertThat(rs.getNullableString("p"), nullValue())
                assertThat(rs.getNullableString("q"), nullValue())
                assertThat(rs.getNullableString("rated_s"), nullValue())
                assertThat(rs.getNullableString("rated_u"), nullValue())
                assertThat(rs.getNullableString("inverter_standard"), nullValue())
                assertThat(rs.getNullableString("sustain_op_overvolt_limit"), nullValue())
                assertThat(rs.getNullableString("stop_at_over_freq"), nullValue())
                assertThat(rs.getNullableString("stop_at_under_freq"), nullValue())
                assertThat(rs.getNullableString("inv_volt_watt_resp_mode"), nullValue())
                assertThat(rs.getNullableString("inv_watt_resp_v1"), nullValue())
                assertThat(rs.getNullableString("inv_watt_resp_v2"), nullValue())
                assertThat(rs.getNullableString("inv_watt_resp_v3"), nullValue())
                assertThat(rs.getNullableString("inv_watt_resp_v4"), nullValue())
                assertThat(rs.getNullableString("inv_watt_resp_p_at_v1"), nullValue())
                assertThat(rs.getNullableString("inv_watt_resp_p_at_v2"), nullValue())
                assertThat(rs.getNullableString("inv_watt_resp_p_at_v3"), nullValue())
                assertThat(rs.getNullableString("inv_watt_resp_p_at_v4"), nullValue())
                assertThat(rs.getNullableString("inv_volt_var_resp_mode"), nullValue())
                assertThat(rs.getNullableString("inv_var_resp_v1"), nullValue())
                assertThat(rs.getNullableString("inv_var_resp_v2"), nullValue())
                assertThat(rs.getNullableString("inv_var_resp_v3"), nullValue())
                assertThat(rs.getNullableString("inv_var_resp_v4"), nullValue())
                assertThat(rs.getNullableString("inv_var_resp_q_at_v1"), nullValue())
                assertThat(rs.getNullableString("inv_var_resp_q_at_v2"), nullValue())
                assertThat(rs.getNullableString("inv_var_resp_q_at_v3"), nullValue())
                assertThat(rs.getNullableString("inv_var_resp_q_at_v4"), nullValue())
                assertThat(rs.getNullableString("inv_reactive_power_mode"), nullValue())
                assertThat(rs.getNullableString("inv_fix_reactive_power"), nullValue())
            }
        )
    }

    private fun `validate power_electronics_connection_phases`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM power_electronics_connection_phases;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("power_electronics_connection_mrid_1"))
                assertThat(rs.getString("p"), equalTo("p_1"))
                assertThat(rs.getString("phase"), equalTo("phase_1"))
                assertThat(rs.getString("q"), equalTo("q_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("power_electronics_connection_mrid"), nullValue())
                assertThat(rs.getNullableString("p"), nullValue())
                assertThat(rs.getString("phase"), equalTo("phase_2"))
                assertThat(rs.getNullableString("q"), nullValue())
            }
        )
    }

    private fun `validate power_electronics_wind_units`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM power_electronics_wind_units;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("power_electronics_connection_mrid_1"))
                assertThat(rs.getString("max_p"), equalTo("max_p_1"))
                assertThat(rs.getString("min_p"), equalTo("min_p_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("power_electronics_connection_mrid"), nullValue())
                assertThat(rs.getNullableString("max_p"), nullValue())
                assertThat(rs.getNullableString("min_p"), nullValue())
            }
        )
    }

    private fun `validate power_transformer_ends`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM power_transformer_ends;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("end_number"), equalTo("end_number_1"))
                assertThat(rs.getString("terminal_mrid"), equalTo("terminal_mrid_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("grounded"), equalTo("grounded_1"))
                assertThat(rs.getString("r_ground"), equalTo("r_ground_1"))
                assertThat(rs.getString("x_ground"), equalTo("x_ground_1"))
                assertThat(rs.getString("star_impedance_mrid"), equalTo("star_impedance_mrid_1"))
                assertThat(rs.getString("power_transformer_mrid"), equalTo("power_transformer_mrid_1"))
                assertThat(rs.getString("connection_kind"), equalTo("connection_kind_1"))
                assertThat(rs.getString("phase_angle_clock"), equalTo("phase_angle_clock_1"))
                assertThat(rs.getString("b"), equalTo("b_1"))
                assertThat(rs.getString("b0"), equalTo("b0_1"))
                assertThat(rs.getString("g"), equalTo("g_1"))
                assertThat(rs.getString("g0"), equalTo("g0_1"))
                assertThat(rs.getString("r"), equalTo("r_1"))
                assertThat(rs.getString("r0"), equalTo("r0_1"))
                assertThat(rs.getString("rated_u"), equalTo("rated_u_1"))
                assertThat(rs.getString("x"), equalTo("x_1"))
                assertThat(rs.getString("x0"), equalTo("x0_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getString("end_number"), equalTo("end_number_2"))
                assertThat(rs.getNullableString("terminal_mrid"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getNullableString("grounded"), nullValue())
                assertThat(rs.getNullableString("r_ground"), nullValue())
                assertThat(rs.getNullableString("x_ground"), nullValue())
                assertThat(rs.getNullableString("star_impedance_mrid"), nullValue())
                assertThat(rs.getNullableString("power_transformer_mrid"), nullValue())
                assertThat(rs.getString("connection_kind"), equalTo("connection_kind_2"))
                assertThat(rs.getNullableString("phase_angle_clock"), nullValue())
                assertThat(rs.getNullableString("b"), nullValue())
                assertThat(rs.getNullableString("b0"), nullValue())
                assertThat(rs.getNullableString("g"), nullValue())
                assertThat(rs.getNullableString("g0"), nullValue())
                assertThat(rs.getNullableString("r"), nullValue())
                assertThat(rs.getNullableString("r0"), nullValue())
                assertThat(rs.getNullableString("rated_u"), nullValue())
                assertThat(rs.getNullableString("x"), nullValue())
                assertThat(rs.getNullableString("x0"), nullValue())
            }
        )
    }

    private fun `validate power_transformer_info`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM power_transformer_info;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
            }
        )
    }

    private fun `validate power_transformers`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM power_transformers;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("vector_group"), equalTo("vector_group_1"))
                assertThat(rs.getString("transformer_utilisation"), equalTo("transformer_utilisation_1"))
                assertThat(rs.getString("construction_kind"), equalTo("construction_kind_1"))
                assertThat(rs.getString("function"), equalTo("function_1"))
                assertThat(rs.getString("power_transformer_info_mrid"), equalTo("power_transformer_info_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getString("vector_group"), equalTo("vector_group_2"))
                assertThat(rs.getNullableString("transformer_utilisation"), nullValue())
                assertThat(rs.getString("construction_kind"), equalTo("construction_kind_2"))
                assertThat(rs.getString("function"), equalTo("function_2"))
                assertThat(rs.getNullableString("power_transformer_info_mrid"), nullValue())
            }
        )
    }

    private fun `validate protection_relay_schemes`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM protection_relay_schemes;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("system_mrid"), equalTo("system_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("system_mrid"), nullValue())
            }
        )
    }

    private fun `validate protection_relay_systems`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM protection_relay_systems;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("protection_kind"), equalTo("protection_kind_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getString("protection_kind"), equalTo("protection_kind_2"))
            }
        )
    }

    private fun `validate ratio_tap_changers`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM ratio_tap_changers;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("control_enabled"), equalTo("control_enabled_1"))
                assertThat(rs.getString("high_step"), equalTo("high_step_1"))
                assertThat(rs.getString("low_step"), equalTo("low_step_1"))
                assertThat(rs.getString("neutral_step"), equalTo("neutral_step_1"))
                assertThat(rs.getString("neutral_u"), equalTo("neutral_u_1"))
                assertThat(rs.getString("normal_step"), equalTo("normal_step_1"))
                assertThat(rs.getString("step"), equalTo("step_1"))
                assertThat(rs.getString("tap_changer_control_mrid"), equalTo("tap_changer_control_mrid_1"))
                assertThat(rs.getString("transformer_end_mrid"), equalTo("transformer_end_mrid_1"))
                assertThat(rs.getString("step_voltage_increment"), equalTo("step_voltage_increment_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("control_enabled"), nullValue())
                assertThat(rs.getNullableString("high_step"), nullValue())
                assertThat(rs.getNullableString("low_step"), nullValue())
                assertThat(rs.getNullableString("neutral_step"), nullValue())
                assertThat(rs.getNullableString("neutral_u"), nullValue())
                assertThat(rs.getNullableString("normal_step"), nullValue())
                assertThat(rs.getNullableString("step"), nullValue())
                assertThat(rs.getNullableString("tap_changer_control_mrid"), nullValue())
                assertThat(rs.getNullableString("transformer_end_mrid"), nullValue())
                assertThat(rs.getNullableString("step_voltage_increment"), nullValue())
            }
        )
    }

    private fun `validate reactive_capability_curves`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM reactive_capability_curves;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getString("name"), equalTo("should_be_nullable"))
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
            }
        )
    }

    private fun `validate reclosers`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM reclosers;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("normal_open"), equalTo("normal_open_1"))
                assertThat(rs.getString("open"), equalTo("open_1"))
                assertThat(rs.getString("rated_current"), equalTo("rated_current_1"))
                assertThat(rs.getString("switch_info_mrid"), equalTo("switch_info_mrid_1"))
                assertThat(rs.getString("breaking_capacity"), equalTo("breaking_capacity_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getString("normal_open"), equalTo("normal_open_2"))
                assertThat(rs.getString("open"), equalTo("open_2"))
                assertThat(rs.getNullableString("rated_current"), nullValue())
                assertThat(rs.getNullableString("switch_info_mrid"), nullValue())
                assertThat(rs.getNullableString("breaking_capacity"), nullValue())
            }
        )
    }

    private fun `validate relay_info`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM relay_info;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("curve_setting"), equalTo("curve_setting_1"))
                assertThat(rs.getString("reclose_fast"), equalTo("reclose_fast_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("curve_setting"), nullValue())
                assertThat(rs.getNullableString("reclose_fast"), nullValue())
            }
        )
    }

    private fun `validate remote_controls`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM remote_controls;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("control_mrid"), equalTo("control_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("control_mrid"), nullValue())
            }
        )
    }

    private fun `validate remote_sources`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM remote_sources;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("measurement_mrid"), equalTo("measurement_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("measurement_mrid"), nullValue())
            }
        )
    }

    private fun `validate series_compensators`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM series_compensators;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("r"), equalTo("r_1"))
                assertThat(rs.getString("r0"), equalTo("r0_1"))
                assertThat(rs.getString("x"), equalTo("x_1"))
                assertThat(rs.getString("x0"), equalTo("x0_1"))
                assertThat(rs.getString("varistor_rated_current"), equalTo("varistor_rated_current_1"))
                assertThat(rs.getString("varistor_voltage_threshold"), equalTo("varistor_voltage_threshold_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getNullableString("r"), nullValue())
                assertThat(rs.getNullableString("r0"), nullValue())
                assertThat(rs.getNullableString("x"), nullValue())
                assertThat(rs.getNullableString("x0"), nullValue())
                assertThat(rs.getNullableString("varistor_rated_current"), nullValue())
                assertThat(rs.getNullableString("varistor_voltage_threshold"), nullValue())
            }
        )
    }

    private fun `validate short_circuit_tests`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM short_circuit_tests;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("base_power"), equalTo("base_power_1"))
                assertThat(rs.getString("temperature"), equalTo("temperature_1"))
                assertThat(rs.getString("current"), equalTo("current_1"))
                assertThat(rs.getString("energised_end_step"), equalTo("energised_end_step_1"))
                assertThat(rs.getString("grounded_end_step"), equalTo("grounded_end_step_1"))
                assertThat(rs.getString("leakage_impedance"), equalTo("leakage_impedance_1"))
                assertThat(rs.getString("leakage_impedance_zero"), equalTo("leakage_impedance_zero_1"))
                assertThat(rs.getString("loss"), equalTo("loss_1"))
                assertThat(rs.getString("loss_zero"), equalTo("loss_zero_1"))
                assertThat(rs.getString("power"), equalTo("power_1"))
                assertThat(rs.getString("voltage"), equalTo("voltage_1"))
                assertThat(rs.getString("voltage_ohmic_part"), equalTo("voltage_ohmic_part_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("base_power"), nullValue())
                assertThat(rs.getNullableString("temperature"), nullValue())
                assertThat(rs.getNullableString("current"), nullValue())
                assertThat(rs.getNullableString("energised_end_step"), nullValue())
                assertThat(rs.getNullableString("grounded_end_step"), nullValue())
                assertThat(rs.getNullableString("leakage_impedance"), nullValue())
                assertThat(rs.getNullableString("leakage_impedance_zero"), nullValue())
                assertThat(rs.getNullableString("loss"), nullValue())
                assertThat(rs.getNullableString("loss_zero"), nullValue())
                assertThat(rs.getNullableString("power"), nullValue())
                assertThat(rs.getNullableString("voltage"), nullValue())
                assertThat(rs.getNullableString("voltage_ohmic_part"), nullValue())
            }
        )
    }

    private fun `validate shunt_compensator_info`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM shunt_compensator_info;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("max_power_loss"), equalTo("max_power_loss_1"))
                assertThat(rs.getString("rated_current"), equalTo("rated_current_1"))
                assertThat(rs.getString("rated_reactive_power"), equalTo("rated_reactive_power_1"))
                assertThat(rs.getString("rated_voltage"), equalTo("rated_voltage_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("max_power_loss"), nullValue())
                assertThat(rs.getNullableString("rated_current"), nullValue())
                assertThat(rs.getNullableString("rated_reactive_power"), nullValue())
                assertThat(rs.getNullableString("rated_voltage"), nullValue())
            }
        )
    }

    private fun `validate sites`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM sites;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
            }
        )
    }

    private fun `validate streetlights`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM streetlights;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("pole_mrid"), equalTo("pole_mrid_1"))
                assertThat(rs.getString("lamp_kind"), equalTo("lamp_kind_1"))
                assertThat(rs.getString("light_rating"), equalTo("light_rating_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("pole_mrid"), nullValue())
                assertThat(rs.getString("lamp_kind"), equalTo("lamp_kind_2"))
                assertThat(rs.getString("light_rating"), nullValue())
            }
        )
    }

    private fun `validate sub_geographical_regions`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM sub_geographical_regions;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("geographical_region_mrid"), equalTo("geographical_region_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("geographical_region_mrid"), nullValue())
            }
        )
    }

    private fun `validate substations`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM substations;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("sub_geographical_region_mrid"), equalTo("sub_geographical_region_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("sub_geographical_region_mrid"), nullValue())
            }
        )
    }

    private fun `validate static_var_compensators`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM static_var_compensators;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("control_enabled"), equalTo("control_enabled_1"))
                assertThat(rs.getString("regulating_control_mrid"), equalTo("regulating_control_mrid_1"))
                assertThat(rs.getString("capacitive_rating"), equalTo("capacitive_rating_1"))
                assertThat(rs.getString("inductive_rating"), equalTo("inductive_rating_1"))
                assertThat(rs.getString("q"), equalTo("q_1"))
                assertThat(rs.getString("svc_control_mode"), equalTo("svc_control_mode_1"))
                assertThat(rs.getString("voltage_set_point"), equalTo("voltage_set_point_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getString("name"), equalTo("should_be_nullable"))
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getNullableString("control_enabled"), nullValue())
                assertThat(rs.getNullableString("regulating_control_mrid"), nullValue())
                assertThat(rs.getNullableString("capacitive_rating"), nullValue())
                assertThat(rs.getNullableString("inductive_rating"), nullValue())
                assertThat(rs.getNullableString("q"), nullValue())
                assertThat(rs.getString("svc_control_mode"), equalTo("svc_control_mode_2"))
                assertThat(rs.getString("voltage_set_point"), nullValue())
            }
        )
    }

    private fun `validate switch_info`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM switch_info;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("rated_interrupting_time"), equalTo("rated_interrupting_time_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("rated_interrupting_time"), nullValue())
            }
        )
    }

    private fun `validate synchronous_machines`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM synchronous_machines;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("normally_in_service"), equalTo("normally_in_service_1"))
                assertThat(rs.getString("in_service"), equalTo("in_service_1"))
                assertThat(rs.getString("commissioned_date"), equalTo("commissioned_date_1"))
                assertThat(rs.getString("base_voltage_mrid"), equalTo("base_voltage_mrid_1"))
                assertThat(rs.getString("control_enabled"), equalTo("control_enabled_1"))
                assertThat(rs.getString("regulating_control_mrid"), equalTo("regulating_control_mrid_1"))
                assertThat(rs.getString("rated_power_factor"), equalTo("rated_power_factor_1"))
                assertThat(rs.getString("rated_s"), equalTo("rated_s_1"))
                assertThat(rs.getString("rated_u"), equalTo("rated_u_1"))
                assertThat(rs.getString("p"), equalTo("p_1"))
                assertThat(rs.getString("q"), equalTo("q_1"))
                assertThat(rs.getString("base_q"), equalTo("base_q_1"))
                assertThat(rs.getString("condenser_p"), equalTo("condenser_p_1"))
                assertThat(rs.getString("earthing"), equalTo("earthing_1"))
                assertThat(rs.getString("earthing_star_point_r"), equalTo("earthing_star_point_r_1"))
                assertThat(rs.getString("earthing_star_point_x"), equalTo("earthing_star_point_x_1"))
                assertThat(rs.getString("ikk"), equalTo("ikk_1"))
                assertThat(rs.getString("max_q"), equalTo("max_q_1"))
                assertThat(rs.getString("max_u"), equalTo("max_u_1"))
                assertThat(rs.getString("min_q"), equalTo("min_q_1"))
                assertThat(rs.getString("min_u"), equalTo("min_u_1"))
                assertThat(rs.getString("mu"), equalTo("mu_1"))
                assertThat(rs.getString("r"), equalTo("r_1"))
                assertThat(rs.getString("r0"), equalTo("r0_1"))
                assertThat(rs.getString("r2"), equalTo("r2_1"))
                assertThat(rs.getString("sat_direct_subtrans_x"), equalTo("sat_direct_subtrans_x_1"))
                assertThat(rs.getString("sat_direct_sync_x"), equalTo("sat_direct_sync_x_1"))
                assertThat(rs.getString("sat_direct_trans_x"), equalTo("sat_direct_trans_x_1"))
                assertThat(rs.getString("x0"), equalTo("x0_1"))
                assertThat(rs.getString("x2"), equalTo("x2_1"))
                assertThat(rs.getString("type"), equalTo("type_1"))
                assertThat(rs.getString("operating_mode"), equalTo("operating_mode_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getString("name"), equalTo("should_be_nullable"))
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getString("num_controls"), equalTo("should_be_nullable"))
                assertThat(rs.getNullableString("normally_in_service"), nullValue())
                assertThat(rs.getNullableString("in_service"), nullValue())
                assertThat(rs.getNullableString("commissioned_date"), nullValue())
                assertThat(rs.getNullableString("base_voltage_mrid"), nullValue())
                assertThat(rs.getString("control_enabled"), equalTo("should_be_nullable"))
                assertThat(rs.getNullableString("regulating_control_mrid"), nullValue())
                assertThat(rs.getNullableString("rated_power_factor"), nullValue())
                assertThat(rs.getNullableString("rated_s"), nullValue())
                assertThat(rs.getNullableString("rated_u"), nullValue())
                assertThat(rs.getNullableString("p"), nullValue())
                assertThat(rs.getNullableString("q"), nullValue())
                assertThat(rs.getNullableString("base_q"), nullValue())
                assertThat(rs.getNullableString("condenser_p"), nullValue())
                assertThat(rs.getNullableString("earthing"), nullValue())
                assertThat(rs.getNullableString("earthing_star_point_r"), nullValue())
                assertThat(rs.getNullableString("earthing_star_point_x"), nullValue())
                assertThat(rs.getNullableString("ikk"), nullValue())
                assertThat(rs.getNullableString("max_q"), nullValue())
                assertThat(rs.getNullableString("max_u"), nullValue())
                assertThat(rs.getNullableString("min_q"), nullValue())
                assertThat(rs.getNullableString("min_u"), nullValue())
                assertThat(rs.getNullableString("mu"), nullValue())
                assertThat(rs.getNullableString("r"), nullValue())
                assertThat(rs.getNullableString("r0"), nullValue())
                assertThat(rs.getNullableString("r2"), nullValue())
                assertThat(rs.getNullableString("sat_direct_subtrans_x"), nullValue())
                assertThat(rs.getNullableString("sat_direct_sync_x"), nullValue())
                assertThat(rs.getNullableString("sat_direct_trans_x"), nullValue())
                assertThat(rs.getNullableString("x0"), nullValue())
                assertThat(rs.getNullableString("x2"), nullValue())
                assertThat(rs.getString("type"), equalTo("type_2"))
                assertThat(rs.getString("operating_mode"), equalTo("operating_mode_2"))
            }
        )
    }

    private fun `validate tap_changer_controls`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM tap_changer_controls;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("discrete"), equalTo("discrete_1"))
                assertThat(rs.getString("mode"), equalTo("mode_1"))
                assertThat(rs.getString("monitored_phase"), equalTo("monitored_phase_1"))
                assertThat(rs.getString("target_deadband"), equalTo("target_deadband_1"))
                assertThat(rs.getString("target_value"), equalTo("target_value_1"))
                assertThat(rs.getString("enabled"), equalTo("enabled_1"))
                assertThat(rs.getString("max_allowed_target_value"), equalTo("max_allowed_target_value_1"))
                assertThat(rs.getString("min_allowed_target_value"), equalTo("min_allowed_target_value_1"))
                assertThat(rs.getString("rated_current"), equalTo("rated_current_1"))
                assertThat(rs.getString("terminal_mrid"), equalTo("terminal_mrid_1"))
                assertThat(rs.getString("ct_primary"), equalTo("ct_primary_1"))
                assertThat(rs.getString("min_target_deadband"), equalTo("min_target_deadband_1"))
                assertThat(rs.getString("limit_voltage"), equalTo("limit_voltage_1"))
                assertThat(rs.getString("line_drop_compensation"), equalTo("line_drop_compensation_1"))
                assertThat(rs.getString("line_drop_r"), equalTo("line_drop_r_1"))
                assertThat(rs.getString("line_drop_x"), equalTo("line_drop_x_1"))
                assertThat(rs.getString("reverse_line_drop_r"), equalTo("reverse_line_drop_r_1"))
                assertThat(rs.getString("reverse_line_drop_x"), equalTo("reverse_line_drop_x_1"))
                assertThat(rs.getString("forward_ldc_blocking"), equalTo("forward_ldc_blocking_1"))
                assertThat(rs.getString("time_delay"), equalTo("time_delay_1"))
                assertThat(rs.getString("co_generation_enabled"), equalTo("co_generation_enabled_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("discrete"), nullValue())
                assertThat(rs.getString("mode"), equalTo("mode_2"))
                assertThat(rs.getString("monitored_phase"), equalTo("monitored_phase_2"))
                assertThat(rs.getNullableString("target_deadband"), nullValue())
                assertThat(rs.getNullableString("target_value"), nullValue())
                assertThat(rs.getNullableString("enabled"), nullValue())
                assertThat(rs.getNullableString("max_allowed_target_value"), nullValue())
                assertThat(rs.getNullableString("min_allowed_target_value"), nullValue())
                assertThat(rs.getNullableString("rated_current"), nullValue())
                assertThat(rs.getNullableString("terminal_mrid"), nullValue())
                assertThat(rs.getNullableString("ct_primary"), nullValue())
                assertThat(rs.getNullableString("min_target_deadband"), nullValue())
                assertThat(rs.getNullableString("limit_voltage"), nullValue())
                assertThat(rs.getNullableString("line_drop_compensation"), nullValue())
                assertThat(rs.getNullableString("line_drop_r"), nullValue())
                assertThat(rs.getNullableString("line_drop_x"), nullValue())
                assertThat(rs.getNullableString("reverse_line_drop_r"), nullValue())
                assertThat(rs.getNullableString("reverse_line_drop_x"), nullValue())
                assertThat(rs.getNullableString("forward_ldc_blocking"), nullValue())
                assertThat(rs.getNullableString("time_delay"), nullValue())
                assertThat(rs.getNullableString("co_generation_enabled"), nullValue())
            }
        )
    }

    private fun `validate terminals`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM terminals;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("conducting_equipment_mrid"), equalTo("conducting_equipment_mrid_1"))
                assertThat(rs.getString("sequence_number"), equalTo("sequence_number_1"))
                assertThat(rs.getString("connectivity_node_mrid"), equalTo("connectivity_node_mrid_1"))
                assertThat(rs.getString("phases"), equalTo("phases_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("conducting_equipment_mrid"), nullValue())
                assertThat(rs.getString("sequence_number"), equalTo("sequence_number_2"))
                assertThat(rs.getNullableString("connectivity_node_mrid"), nullValue())
                assertThat(rs.getString("phases"), equalTo("phases_2"))
            }
        )
    }

    private fun `validate transformer_end_info`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM transformer_end_info;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("connection_kind"), equalTo("connection_kind_1"))
                assertThat(rs.getString("emergency_s"), equalTo("emergency_s_1"))
                assertThat(rs.getString("end_number"), equalTo("end_number_1"))
                assertThat(rs.getString("insulation_u"), equalTo("insulation_u_1"))
                assertThat(rs.getString("phase_angle_clock"), equalTo("phase_angle_clock_1"))
                assertThat(rs.getString("r"), equalTo("r_1"))
                assertThat(rs.getString("rated_s"), equalTo("rated_s_1"))
                assertThat(rs.getString("rated_u"), equalTo("rated_u_1"))
                assertThat(rs.getString("short_term_s"), equalTo("short_term_s_1"))
                assertThat(rs.getString("transformer_tank_info_mrid"), equalTo("transformer_tank_info_mrid_1"))
                assertThat(rs.getString("energised_end_no_load_tests"), equalTo("energised_end_no_load_tests_1"))
                assertThat(rs.getString("energised_end_short_circuit_tests"), equalTo("energised_end_short_circuit_tests_1"))
                assertThat(rs.getString("grounded_end_short_circuit_tests"), equalTo("grounded_end_short_circuit_tests_1"))
                assertThat(rs.getString("open_end_open_circuit_tests"), equalTo("open_end_open_circuit_tests_1"))
                assertThat(rs.getString("energised_end_open_circuit_tests"), equalTo("energised_end_open_circuit_tests_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getString("connection_kind"), equalTo("connection_kind_2"))
                assertThat(rs.getNullableString("emergency_s"), nullValue())
                assertThat(rs.getString("end_number"), equalTo("end_number_2"))
                assertThat(rs.getNullableString("insulation_u"), nullValue())
                assertThat(rs.getNullableString("phase_angle_clock"), nullValue())
                assertThat(rs.getNullableString("r"), nullValue())
                assertThat(rs.getNullableString("rated_s"), nullValue())
                assertThat(rs.getNullableString("rated_u"), nullValue())
                assertThat(rs.getNullableString("short_term_s"), nullValue())
                assertThat(rs.getNullableString("transformer_tank_info_mrid"), nullValue())
                assertThat(rs.getNullableString("energised_end_no_load_tests"), nullValue())
                assertThat(rs.getNullableString("energised_end_short_circuit_tests"), nullValue())
                assertThat(rs.getNullableString("grounded_end_short_circuit_tests"), nullValue())
                assertThat(rs.getNullableString("open_end_open_circuit_tests"), nullValue())
                assertThat(rs.getNullableString("energised_end_open_circuit_tests"), nullValue())
            }
        )
    }

    private fun `validate transformer_star_impedances`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM transformer_star_impedances;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("R"), equalTo("R_1"))
                assertThat(rs.getString("R0"), equalTo("R0_1"))
                assertThat(rs.getString("X"), equalTo("X_1"))
                assertThat(rs.getString("X0"), equalTo("X0_1"))
                assertThat(rs.getString("transformer_end_info_mrid"), equalTo("transformer_end_info_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("R"), nullValue())
                assertThat(rs.getNullableString("R0"), nullValue())
                assertThat(rs.getNullableString("X"), nullValue())
                assertThat(rs.getNullableString("X0"), nullValue())
                assertThat(rs.getNullableString("transformer_end_info_mrid"), nullValue())
            }
        )
    }

    private fun `validate transformer_tank_info`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM transformer_tank_info;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("power_transformer_info_mrid"), equalTo("power_transformer_info_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("power_transformer_info_mrid"), nullValue())
            }
        )
    }

    private fun `validate usage_points`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM usage_points;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("is_virtual"), equalTo("is_virtual_1"))
                assertThat(rs.getString("connection_category"), equalTo("connection_category_1"))
                assertThat(rs.getString("rated_power"), equalTo("rated_power_1"))
                assertThat(rs.getString("approved_inverter_capacity"), equalTo("approved_inverter_capacity_1"))
                assertThat(rs.getString("phase_code"), equalTo("phase_code_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("is_virtual"), nullValue())
                assertThat(rs.getNullableString("connection_category"), nullValue())
                assertThat(rs.getNullableString("rated_power"), nullValue())
                assertThat(rs.getNullableString("approved_inverter_capacity"), nullValue())
                assertThat(rs.getString("phase_code"), equalTo("phase_code_2"))
            }
        )
    }

    private fun `validate voltage_relays`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM voltage_relays;",
            { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("description"), equalTo("description_1"))
                assertThat(rs.getString("num_diagram_objects"), equalTo("num_diagram_objects_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("num_controls"), equalTo("num_controls_1"))
                assertThat(rs.getString("model"), equalTo("model_1"))
                assertThat(rs.getString("reclosing"), equalTo("reclosing_1"))
                assertThat(rs.getString("relay_delay_time"), equalTo("relay_delay_time_1"))
                assertThat(rs.getString("protection_kind"), equalTo("protection_kind_1"))
                assertThat(rs.getString("directable"), equalTo("directable_1"))
                assertThat(rs.getString("power_direction"), equalTo("power_direction_1"))
                assertThat(rs.getString("relay_info_mrid"), equalTo("relay_info_mrid_1"))
            }, { rs ->
                assertThat(rs.getString("mrid"), equalTo("mrid_2"))
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("description"), nullValue())
                assertThat(rs.getNullableString("num_diagram_objects"), nullValue())
                assertThat(rs.getNullableString("location_mrid"), nullValue())
                assertThat(rs.getNullableString("num_controls"), nullValue())
                assertThat(rs.getNullableString("model"), nullValue())
                assertThat(rs.getNullableString("reclosing"), nullValue())
                assertThat(rs.getNullableString("relay_delay_time"), nullValue())
                assertThat(rs.getString("protection_kind"), equalTo("protection_kind_2"))
                assertThat(rs.getNullableString("directable"), nullValue())
                assertThat(rs.getString("power_direction"), equalTo("power_direction_2"))
                assertThat(rs.getNullableString("relay_info_mrid"), nullValue())
            }
        )
    }

    // Array data tables.
    private fun `validate location_street_addresses`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM location_street_addresses;",
            { rs ->
                assertThat(rs.getString("town_name"), equalTo("town_name_1"))
                assertThat(rs.getString("state_or_province"), equalTo("state_or_province_1"))
                assertThat(rs.getString("postal_code"), equalTo("postal_code_1"))
                assertThat(rs.getString("po_box"), equalTo("po_box_1"))
                assertThat(rs.getString("building_name"), equalTo("building_name_1"))
                assertThat(rs.getString("floor_identification"), equalTo("floor_identification_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
                assertThat(rs.getString("number"), equalTo("number_1"))
                assertThat(rs.getString("suite_number"), equalTo("suite_number_1"))
                assertThat(rs.getString("type"), equalTo("type_1"))
                assertThat(rs.getString("display_address"), equalTo("display_address_1"))
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_1"))
                assertThat(rs.getString("address_field"), equalTo("address_field_1"))
            }, { rs ->
                assertThat(rs.getNullableString("town_name"), nullValue())
                assertThat(rs.getNullableString("state_or_province"), nullValue())
                assertThat(rs.getNullableString("postal_code"), nullValue())
                assertThat(rs.getNullableString("po_box"), nullValue())
                assertThat(rs.getNullableString("building_name"), nullValue())
                assertThat(rs.getNullableString("floor_identification"), nullValue())
                assertThat(rs.getNullableString("name"), nullValue())
                assertThat(rs.getNullableString("number"), nullValue())
                assertThat(rs.getNullableString("suite_number"), nullValue())
                assertThat(rs.getNullableString("type"), nullValue())
                assertThat(rs.getNullableString("display_address"), nullValue())
                assertThat(rs.getString("location_mrid"), equalTo("location_mrid_2"))
                assertThat(rs.getString("address_field"), equalTo("address_field_2"))
            }
        )
    }

    private fun `validate phase_impedance_data`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM phase_impedance_data;",
            { rs ->
                assertThat(rs.getString("per_length_phase_impedance_mrid"), equalTo("per_length_phase_impedance_mrid_1"))
                assertThat(rs.getString("from_phase"), equalTo("from_phase_1"))
                assertThat(rs.getString("to_phase"), equalTo("to_phase_1"))
                assertThat(rs.getString("b"), equalTo("b_1"))
                assertThat(rs.getString("g"), equalTo("g_1"))
                assertThat(rs.getString("r"), equalTo("r_1"))
                assertThat(rs.getString("x"), equalTo("x_1"))
            }, { rs ->
                assertThat(rs.getString("per_length_phase_impedance_mrid"), equalTo("per_length_phase_impedance_mrid_2"))
                assertThat(rs.getString("from_phase"), equalTo("from_phase_2"))
                assertThat(rs.getString("to_phase"), equalTo("to_phase_2"))
                assertThat(rs.getNullableString("b"), nullValue())
                assertThat(rs.getNullableString("g"), nullValue())
                assertThat(rs.getNullableString("r"), nullValue())
                assertThat(rs.getNullableString("x"), nullValue())
            }
        )
    }

    private fun `validate protection_relay_function_thresholds`(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM protection_relay_function_thresholds;",
            { rs ->
                assertThat(rs.getString("protection_relay_function_mrid"), equalTo("protection_relay_function_mrid_1"))
                assertThat(rs.getString("sequence_number"), equalTo("sequence_number_1"))
                assertThat(rs.getString("unit_symbol"), equalTo("unit_symbol_1"))
                assertThat(rs.getString("value"), equalTo("value_1"))
                assertThat(rs.getString("name"), equalTo("name_1"))
            }, { rs ->
                assertThat(rs.getString("protection_relay_function_mrid"), equalTo("protection_relay_function_mrid_2"))
                assertThat(rs.getString("sequence_number"), equalTo("sequence_number_2"))
                assertThat(rs.getString("unit_symbol"), equalTo("unit_symbol_2"))
                assertThat(rs.getString("value"), equalTo("value_2"))
                assertThat(rs.getNullableString("name"), nullValue())
            }
        )
    }

}
