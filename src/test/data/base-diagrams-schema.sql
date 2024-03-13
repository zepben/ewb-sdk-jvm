-- ##################
-- # Diagram Tables #
-- ##################

CREATE TABLE metadata_data_sources (source TEXT NOT NULL, version TEXT NOT NULL, timestamp TEXT NOT NULL);

CREATE TABLE version (version TEXT NOT NULL);

CREATE TABLE name_types (name TEXT NOT NULL, description TEXT NULL);
CREATE UNIQUE INDEX name_types_name ON name_types (name);

CREATE TABLE names (name TEXT NOT NULL, identified_object_mrid TEXT NOT NULL, name_type_name TEXT NOT NULL);
CREATE UNIQUE INDEX names_identified_object_mrid_name_type_name_name ON names (identified_object_mrid, name_type_name, name);
CREATE INDEX names_identified_object_mrid ON names (identified_object_mrid);
CREATE INDEX names_name ON names (name);
CREATE INDEX names_name_type_name ON names (name_type_name);

CREATE TABLE diagram_object_points (diagram_object_mrid TEXT NOT NULL, sequence_number TEXT NOT NULL, x_position TEXT NULL, y_position TEXT NULL);
CREATE UNIQUE INDEX diagram_object_points_diagram_object_mrid_sequence_number ON diagram_object_points (diagram_object_mrid, sequence_number);
CREATE INDEX diagram_object_points_diagram_object_mrid ON diagram_object_points (diagram_object_mrid);

CREATE TABLE diagram_objects (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, identified_object_mrid TEXT NULL, diagram_mrid TEXT NULL, style TEXT NULL, rotation NUMBER NOT NULL);
CREATE UNIQUE INDEX diagram_objects_mrid ON diagram_objects (mrid);
CREATE INDEX diagram_objects_name ON diagram_objects (name);
CREATE INDEX diagram_objects_identified_object_mrid ON diagram_objects (identified_object_mrid);
CREATE INDEX diagram_objects_diagram_mrid ON diagram_objects (diagram_mrid);

CREATE TABLE diagrams (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, diagram_style TEXT NOT NULL, orientation_kind TEXT NOT NULL);
CREATE UNIQUE INDEX diagrams_mrid ON diagrams (mrid);
CREATE INDEX diagrams_name ON diagrams (name);

-- #############################
-- # Customer Tables To Remove #
-- #############################

CREATE TABLE customer_agreements (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, title TEXT NOT NULL, created_date_time TEXT NULL, author_name TEXT NOT NULL, type TEXT NOT NULL, status TEXT NOT NULL, comment TEXT NOT NULL, customer_mrid TEXT NULL);
CREATE UNIQUE INDEX customer_agreements_mrid ON customer_agreements (mrid);
CREATE INDEX customer_agreements_name ON customer_agreements (name);
CREATE INDEX customer_agreements_customer_mrid ON customer_agreements (customer_mrid);

CREATE TABLE customer_agreements_pricing_structures (customer_agreement_mrid TEXT NOT NULL, pricing_structure_mrid TEXT NOT NULL);
CREATE UNIQUE INDEX customer_agreements_pricing_structures_customer_agreement_mrid_pricing_structure_mrid ON customer_agreements_pricing_structures (customer_agreement_mrid, pricing_structure_mrid);
CREATE INDEX customer_agreements_pricing_structures_customer_agreement_mrid ON customer_agreements_pricing_structures (customer_agreement_mrid);
CREATE INDEX customer_agreements_pricing_structures_pricing_structure_mrid ON customer_agreements_pricing_structures (pricing_structure_mrid);

CREATE TABLE customers (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, organisation_mrid TEXT NULL, kind TEXT NOT NULL, num_end_devices INTEGER NULL);
CREATE UNIQUE INDEX customers_mrid ON customers (mrid);
CREATE INDEX customers_name ON customers (name);

CREATE TABLE organisations (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL);
CREATE UNIQUE INDEX organisations_mrid ON organisations (mrid);
CREATE INDEX organisations_name ON organisations (name);

CREATE TABLE pricing_structures (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, title TEXT NOT NULL, created_date_time TEXT NULL, author_name TEXT NOT NULL, type TEXT NOT NULL, status TEXT NOT NULL, comment TEXT NOT NULL);
CREATE UNIQUE INDEX pricing_structures_mrid ON pricing_structures (mrid);
CREATE INDEX pricing_structures_name ON pricing_structures (name);

CREATE TABLE pricing_structures_tariffs (pricing_structure_mrid TEXT NOT NULL, tariff_mrid TEXT NOT NULL);
CREATE UNIQUE INDEX pricing_structures_tariffs_pricing_structure_mrid_tariff_mrid ON pricing_structures_tariffs (pricing_structure_mrid, tariff_mrid);
CREATE INDEX pricing_structures_tariffs_pricing_structure_mrid ON pricing_structures_tariffs (pricing_structure_mrid);
CREATE INDEX pricing_structures_tariffs_tariff_mrid ON pricing_structures_tariffs (tariff_mrid);

CREATE TABLE tariffs (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, title TEXT NOT NULL, created_date_time TEXT NULL, author_name TEXT NOT NULL, type TEXT NOT NULL, status TEXT NOT NULL, comment TEXT NOT NULL);
CREATE UNIQUE INDEX tariffs_mrid ON tariffs (mrid);
CREATE INDEX tariffs_name ON tariffs (name);

-- ############################
-- # Network Tables To Remove #
-- ############################

CREATE TABLE ac_line_segments (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL, length NUMBER NULL, wire_info_mrid TEXT NULL, per_length_sequence_impedance_mrid TEXT NULL);
CREATE UNIQUE INDEX ac_line_segments_mrid ON ac_line_segments (mrid);
CREATE INDEX ac_line_segments_name ON ac_line_segments (name);

CREATE TABLE accumulators (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, power_system_resource_mrid TEXT NULL, remote_source_mrid TEXT NULL, terminal_mrid TEXT NULL, phases TEXT NOT NULL, unit_symbol TEXT NOT NULL);
CREATE UNIQUE INDEX accumulators_mrid ON accumulators (mrid);
CREATE INDEX accumulators_name ON accumulators (name);
CREATE INDEX accumulators_power_system_resource_mrid ON accumulators (power_system_resource_mrid);
CREATE INDEX accumulators_remote_source_mrid ON accumulators (remote_source_mrid);
CREATE INDEX accumulators_terminal_mrid ON accumulators (terminal_mrid);

CREATE TABLE analogs (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, power_system_resource_mrid TEXT NULL, remote_source_mrid TEXT NULL, terminal_mrid TEXT NULL, phases TEXT NOT NULL, unit_symbol TEXT NOT NULL, positive_flow_in BOOLEAN NOT NULL);
CREATE UNIQUE INDEX analogs_mrid ON analogs (mrid);
CREATE INDEX analogs_name ON analogs (name);
CREATE INDEX analogs_power_system_resource_mrid ON analogs (power_system_resource_mrid);
CREATE INDEX analogs_remote_source_mrid ON analogs (remote_source_mrid);
CREATE INDEX analogs_terminal_mrid ON analogs (terminal_mrid);

CREATE TABLE asset_organisation_roles_assets (asset_organisation_role_mrid TEXT NOT NULL, asset_mrid TEXT NOT NULL);
CREATE UNIQUE INDEX asset_organisation_roles_assets_asset_organisation_role_mrid_asset_mrid ON asset_organisation_roles_assets (asset_organisation_role_mrid, asset_mrid);
CREATE INDEX asset_organisation_roles_assets_asset_organisation_role_mrid ON asset_organisation_roles_assets (asset_organisation_role_mrid);
CREATE INDEX asset_organisation_roles_assets_asset_mrid ON asset_organisation_roles_assets (asset_mrid);

CREATE TABLE asset_owners (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, organisation_mrid TEXT NULL);
CREATE UNIQUE INDEX asset_owners_mrid ON asset_owners (mrid);
CREATE INDEX asset_owners_name ON asset_owners (name);

CREATE TABLE base_voltages (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, nominal_voltage INTEGER NOT NULL);
CREATE UNIQUE INDEX base_voltages_mrid ON base_voltages (mrid);
CREATE INDEX base_voltages_name ON base_voltages (name);

CREATE TABLE battery_unit (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, power_electronics_connection_mrid TEXT NULL, max_p NUMBER NULL, min_p NUMBER NULL, battery_state TEXT NOT NULL, rated_e INTEGER NULL, stored_e INTEGER NULL);
CREATE UNIQUE INDEX battery_unit_mrid ON battery_unit (mrid);
CREATE INDEX battery_unit_name ON battery_unit (name);
CREATE INDEX battery_unit_power_electronics_connection_mrid ON battery_unit (power_electronics_connection_mrid);

CREATE TABLE breakers (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL, normal_open INTEGER NOT NULL, open INTEGER NOT NULL, rated_current INTEGER NULL, switch_info_mrid TEXT NULL, breaking_capacity INTEGER NULL, in_transit_time NUMBER NULL);
CREATE UNIQUE INDEX breakers_mrid ON breakers (mrid);
CREATE INDEX breakers_name ON breakers (name);

CREATE TABLE busbar_sections (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL);
CREATE UNIQUE INDEX busbar_sections_mrid ON busbar_sections (mrid);
CREATE INDEX busbar_sections_name ON busbar_sections (name);

CREATE TABLE cable_info (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, rated_current NUMBER NULL, material TEXT NOT NULL);
CREATE UNIQUE INDEX cable_info_mrid ON cable_info (mrid);
CREATE INDEX cable_info_name ON cable_info (name);

CREATE TABLE circuits (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, loop_mrid TEXT NULL);
CREATE UNIQUE INDEX circuits_mrid ON circuits (mrid);
CREATE INDEX circuits_name ON circuits (name);
CREATE INDEX circuits_loop_mrid ON circuits (loop_mrid);

CREATE TABLE circuits_substations (circuit_mrid TEXT NOT NULL, substation_mrid TEXT NOT NULL);
CREATE UNIQUE INDEX circuits_substations_circuit_mrid_substation_mrid ON circuits_substations (circuit_mrid, substation_mrid);
CREATE INDEX circuits_substations_circuit_mrid ON circuits_substations (circuit_mrid);
CREATE INDEX circuits_substations_substation_mrid ON circuits_substations (substation_mrid);

CREATE TABLE circuits_terminals (circuit_mrid TEXT NOT NULL, terminal_mrid TEXT NOT NULL);
CREATE UNIQUE INDEX circuits_terminals_circuit_mrid_terminal_mrid ON circuits_terminals (circuit_mrid, terminal_mrid);
CREATE INDEX circuits_terminals_circuit_mrid ON circuits_terminals (circuit_mrid);
CREATE INDEX circuits_terminals_terminal_mrid ON circuits_terminals (terminal_mrid);

CREATE TABLE connectivity_nodes (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL);
CREATE UNIQUE INDEX connectivity_nodes_mrid ON connectivity_nodes (mrid);
CREATE INDEX connectivity_nodes_name ON connectivity_nodes (name);

CREATE TABLE controls (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, power_system_resource_mrid TEXT NULL);
CREATE UNIQUE INDEX controls_mrid ON controls (mrid);
CREATE INDEX controls_name ON controls (name);

CREATE TABLE current_relays (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, model TEXT NULL, reclosing BOOLEAN NULL, relay_delay_time NUMBER NULL, protection_kind TEXT NOT NULL, directable BOOLEAN NULL, power_direction TEXT NOT NULL, relay_info_mrid TEXT NULL, current_limit_1 NUMBER NULL, inverse_time_flag BOOLEAN NULL, time_delay_1 NUMBER NULL);
CREATE UNIQUE INDEX current_relays_mrid ON current_relays (mrid);
CREATE INDEX current_relays_name ON current_relays (name);

CREATE TABLE current_transformer_info (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, accuracy_class TEXT NULL, accuracy_limit NUMBER NULL, core_count INTEGER NULL, ct_class TEXT NULL, knee_point_voltage INTEGER NULL, max_ratio_denominator NUMBER NULL, max_ratio_numerator NUMBER NULL, nominal_ratio_denominator NUMBER NULL, nominal_ratio_numerator NUMBER NULL, primary_ratio NUMBER NULL, rated_current INTEGER NULL, secondary_fls_rating INTEGER NULL, secondary_ratio NUMBER NULL, usage TEXT NULL);
CREATE UNIQUE INDEX current_transformer_info_mrid ON current_transformer_info (mrid);
CREATE INDEX current_transformer_info_name ON current_transformer_info (name);

CREATE TABLE current_transformers (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, terminal_mrid TEXT NULL, current_transformer_info_mrid TEXT NULL, core_burden INTEGER NULL);
CREATE UNIQUE INDEX current_transformers_mrid ON current_transformers (mrid);
CREATE INDEX current_transformers_name ON current_transformers (name);

CREATE TABLE disconnectors (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL, normal_open INTEGER NOT NULL, open INTEGER NOT NULL, rated_current INTEGER NULL, switch_info_mrid TEXT NULL);
CREATE UNIQUE INDEX disconnectors_mrid ON disconnectors (mrid);
CREATE INDEX disconnectors_name ON disconnectors (name);

CREATE TABLE discretes (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, power_system_resource_mrid TEXT NULL, remote_source_mrid TEXT NULL, terminal_mrid TEXT NULL, phases TEXT NOT NULL, unit_symbol TEXT NOT NULL);
CREATE UNIQUE INDEX discretes_mrid ON discretes (mrid);
CREATE INDEX discretes_name ON discretes (name);
CREATE INDEX discretes_power_system_resource_mrid ON discretes (power_system_resource_mrid);
CREATE INDEX discretes_remote_source_mrid ON discretes (remote_source_mrid);
CREATE INDEX discretes_terminal_mrid ON discretes (terminal_mrid);

CREATE TABLE distance_relays (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, model TEXT NULL, reclosing BOOLEAN NULL, relay_delay_time NUMBER NULL, protection_kind TEXT NOT NULL, directable BOOLEAN NULL, power_direction TEXT NOT NULL, relay_info_mrid TEXT NULL, backward_blind NUMBER NULL, backward_reach NUMBER NULL, backward_reactance NUMBER NULL, forward_blind NUMBER NULL, forward_reach NUMBER NULL, forward_reactance NUMBER NULL, operation_phase_angle1 NUMBER NULL, operation_phase_angle2 NUMBER NULL, operation_phase_angle3 NUMBER NULL);
CREATE UNIQUE INDEX distance_relays_mrid ON distance_relays (mrid);
CREATE INDEX distance_relays_name ON distance_relays (name);

CREATE TABLE energy_consumer_phases (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, energy_consumer_mrid TEXT NOT NULL, phase TEXT NOT NULL, p NUMBER NULL, q NUMBER NULL, p_fixed NUMBER NULL, q_fixed NUMBER NULL);
CREATE UNIQUE INDEX energy_consumer_phases_mrid ON energy_consumer_phases (mrid);
CREATE UNIQUE INDEX energy_consumer_phases_energy_consumer_mrid_phase ON energy_consumer_phases (energy_consumer_mrid, phase);
CREATE INDEX energy_consumer_phases_name ON energy_consumer_phases (name);
CREATE INDEX energy_consumer_phases_energy_consumer_mrid ON energy_consumer_phases (energy_consumer_mrid);

CREATE TABLE energy_consumers (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL, customer_count INTEGER NULL, grounded BOOLEAN NOT NULL, p NUMBER NULL, q NUMBER NULL, p_fixed NUMBER NULL, q_fixed NUMBER NULL, phase_connection TEXT NOT NULL);
CREATE UNIQUE INDEX energy_consumers_mrid ON energy_consumers (mrid);
CREATE INDEX energy_consumers_name ON energy_consumers (name);

CREATE TABLE energy_source_phases (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, energy_source_mrid TEXT NOT NULL, phase TEXT NOT NULL);
CREATE UNIQUE INDEX energy_source_phases_mrid ON energy_source_phases (mrid);
CREATE UNIQUE INDEX energy_source_phases_energy_source_mrid_phase ON energy_source_phases (energy_source_mrid, phase);
CREATE INDEX energy_source_phases_name ON energy_source_phases (name);
CREATE INDEX energy_source_phases_energy_source_mrid ON energy_source_phases (energy_source_mrid);

CREATE TABLE energy_sources (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL, active_power NUMBER NULL, reactive_power NUMBER NULL, voltage_angle NUMBER NULL, voltage_magnitude NUMBER NULL, p_max NUMBER NULL, p_min NUMBER NULL, r NUMBER NULL, r0 NUMBER NULL, rn NUMBER NULL, x NUMBER NULL, x0 NUMBER NULL, xn NUMBER NULL, is_external_grid BOOLEAN NOT NULL, r_min NUMBER NULL, rn_min NUMBER NULL, r0_min NUMBER NULL, x_min NUMBER NULL, xn_min NUMBER NULL, x0_min NUMBER NULL, r_max NUMBER NULL, rn_max NUMBER NULL, r0_max NUMBER NULL, x_max NUMBER NULL, xn_max NUMBER NULL, x0_max NUMBER NULL);
CREATE UNIQUE INDEX energy_sources_mrid ON energy_sources (mrid);
CREATE INDEX energy_sources_name ON energy_sources (name);

CREATE TABLE equipment_equipment_containers (equipment_mrid TEXT NOT NULL, equipment_container_mrid TEXT NOT NULL);
CREATE UNIQUE INDEX equipment_equipment_containers_equipment_mrid_equipment_container_mrid ON equipment_equipment_containers (equipment_mrid, equipment_container_mrid);
CREATE INDEX equipment_equipment_containers_equipment_mrid ON equipment_equipment_containers (equipment_mrid);
CREATE INDEX equipment_equipment_containers_equipment_container_mrid ON equipment_equipment_containers (equipment_container_mrid);

CREATE TABLE equipment_operational_restrictions (equipment_mrid TEXT NOT NULL, operational_restriction_mrid TEXT NOT NULL);
CREATE UNIQUE INDEX equipment_operational_restrictions_equipment_mrid_operational_restriction_mrid ON equipment_operational_restrictions (equipment_mrid, operational_restriction_mrid);
CREATE INDEX equipment_operational_restrictions_equipment_mrid ON equipment_operational_restrictions (equipment_mrid);
CREATE INDEX equipment_operational_restrictions_operational_restriction_mrid ON equipment_operational_restrictions (operational_restriction_mrid);

CREATE TABLE equipment_usage_points (equipment_mrid TEXT NOT NULL, usage_point_mrid TEXT NOT NULL);
CREATE UNIQUE INDEX equipment_usage_points_equipment_mrid_usage_point_mrid ON equipment_usage_points (equipment_mrid, usage_point_mrid);
CREATE INDEX equipment_usage_points_equipment_mrid ON equipment_usage_points (equipment_mrid);
CREATE INDEX equipment_usage_points_usage_point_mrid ON equipment_usage_points (usage_point_mrid);

CREATE TABLE equivalent_branches (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL, negative_r12 NUMBER NULL, negative_r21 NUMBER NULL, negative_x12 NUMBER NULL, negative_x21 NUMBER NULL, positive_r12 NUMBER NULL, positive_r21 NUMBER NULL, positive_x12 NUMBER NULL, positive_x21 NUMBER NULL, r NUMBER NULL, r21 NUMBER NULL, x NUMBER NULL, x21 NUMBER NULL, zero_r12 NUMBER NULL, zero_r21 NUMBER NULL, zero_x12 NUMBER NULL, zero_x21 NUMBER NULL);
CREATE UNIQUE INDEX equivalent_branches_mrid ON equivalent_branches (mrid);
CREATE INDEX equivalent_branches_name ON equivalent_branches (name);

CREATE TABLE ev_charging_units (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, power_electronics_connection_mrid TEXT NULL, max_p NUMBER NULL, min_p NUMBER NULL);
CREATE UNIQUE INDEX ev_charging_units_mrid ON ev_charging_units (mrid);
CREATE INDEX ev_charging_units_name ON ev_charging_units (name);
CREATE INDEX ev_charging_units_power_electronics_connection_mrid ON ev_charging_units (power_electronics_connection_mrid);

CREATE TABLE fault_indicators (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, terminal_mrid TEXT NULL);
CREATE UNIQUE INDEX fault_indicators_mrid ON fault_indicators (mrid);
CREATE INDEX fault_indicators_name ON fault_indicators (name);

CREATE TABLE feeders (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normal_head_terminal_mrid TEXT NULL, normal_energizing_substation_mrid TEXT NULL);
CREATE UNIQUE INDEX feeders_mrid ON feeders (mrid);
CREATE INDEX feeders_name ON feeders (name);
CREATE INDEX feeders_normal_energizing_substation_mrid ON feeders (normal_energizing_substation_mrid);

CREATE TABLE fuses (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL, normal_open INTEGER NOT NULL, open INTEGER NOT NULL, rated_current INTEGER NULL, switch_info_mrid TEXT NULL, function_mrid TEXT NULL);
CREATE UNIQUE INDEX fuses_mrid ON fuses (mrid);
CREATE INDEX fuses_name ON fuses (name);

CREATE TABLE geographical_regions (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL);
CREATE UNIQUE INDEX geographical_regions_mrid ON geographical_regions (mrid);
CREATE INDEX geographical_regions_name ON geographical_regions (name);

CREATE TABLE grounds (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL);
CREATE UNIQUE INDEX grounds_mrid ON grounds (mrid);
CREATE INDEX grounds_name ON grounds (name);

CREATE TABLE ground_disconnectors (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL, normal_open INTEGER NOT NULL, open INTEGER NOT NULL, rated_current INTEGER NULL, switch_info_mrid TEXT NULL);
CREATE UNIQUE INDEX ground_disconnectors_mrid ON ground_disconnectors (mrid);
CREATE INDEX ground_disconnectors_name ON ground_disconnectors (name);

CREATE TABLE jumpers (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL, normal_open INTEGER NOT NULL, open INTEGER NOT NULL, rated_current INTEGER NULL, switch_info_mrid TEXT NULL);
CREATE UNIQUE INDEX jumpers_mrid ON jumpers (mrid);
CREATE INDEX jumpers_name ON jumpers (name);

CREATE TABLE junctions (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL);
CREATE UNIQUE INDEX junctions_mrid ON junctions (mrid);
CREATE INDEX junctions_name ON junctions (name);

CREATE TABLE linear_shunt_compensators (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL, control_enabled BOOLEAN, regulating_control_mrid TEXT NULL, shunt_compensator_info_mrid TEXT NULL, grounded BOOLEAN NOT NULL, nom_u INTEGER NULL, phase_connection TEXT NOT NULL, sections NUMBER NULL, b0_per_section NUMBER NULL, b_per_section NUMBER NULL, g0_per_section NUMBER NULL, g_per_section NUMBER NULL);
CREATE UNIQUE INDEX linear_shunt_compensators_mrid ON linear_shunt_compensators (mrid);
CREATE INDEX linear_shunt_compensators_name ON linear_shunt_compensators (name);

CREATE TABLE load_break_switches (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL, normal_open INTEGER NOT NULL, open INTEGER NOT NULL, rated_current INTEGER NULL, switch_info_mrid TEXT NULL, breaking_capacity INTEGER NULL);
CREATE UNIQUE INDEX load_break_switches_mrid ON load_break_switches (mrid);
CREATE INDEX load_break_switches_name ON load_break_switches (name);

CREATE TABLE location_street_addresses (town_name TEXT NULL, state_or_province TEXT NULL, postal_code TEXT NOT NULL, po_box TEXT NULL, building_name TEXT NULL, floor_identification TEXT NULL, name TEXT NULL, number TEXT NULL, suite_number TEXT NULL, type TEXT NULL, display_address TEXT NULL, location_mrid TEXT NOT NULL, address_field TEXT NOT NULL);
CREATE INDEX location_street_addresses_location_mrid ON location_street_addresses (location_mrid);

CREATE TABLE locations (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL);
CREATE UNIQUE INDEX locations_mrid ON locations (mrid);
CREATE INDEX locations_name ON locations (name);

CREATE TABLE loops (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL);
CREATE UNIQUE INDEX loops_mrid ON loops (mrid);
CREATE INDEX loops_name ON loops (name);

CREATE TABLE loops_substations (loop_mrid TEXT NOT NULL, substation_mrid TEXT NOT NULL, relationship TEXT NOT NULL);
CREATE UNIQUE INDEX loops_substations_loop_mrid_substation_mrid ON loops_substations (loop_mrid, substation_mrid);
CREATE INDEX loops_substations_loop_mrid ON loops_substations (loop_mrid);
CREATE INDEX loops_substations_substation_mrid ON loops_substations (substation_mrid);

CREATE TABLE lv_feeders (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normal_head_terminal_mrid TEXT NULL);
CREATE UNIQUE INDEX lv_feeders_mrid ON lv_feeders (mrid);
CREATE INDEX lv_feeders_name ON lv_feeders (name);

CREATE TABLE meters (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, customer_mrid TEXT NULL, service_location_mrid TEXT NULL);
CREATE UNIQUE INDEX meters_mrid ON meters (mrid);
CREATE INDEX meters_name ON meters (name);

CREATE TABLE no_load_tests (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, base_power INTEGER NULL, temperature NUMBER NULL, energised_end_voltage INTEGER NULL, exciting_current NUMBER NULL, exciting_current_zero NUMBER NULL, loss INTEGER NULL, loss_zero INTEGER NULL);
CREATE UNIQUE INDEX no_load_tests_mrid ON no_load_tests (mrid);
CREATE INDEX no_load_tests_name ON no_load_tests (name);

CREATE TABLE open_circuit_tests (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, base_power INTEGER NULL, temperature NUMBER NULL, energised_end_step INTEGER NULL, energised_end_voltage INTEGER NULL, open_end_step INTEGER NULL, open_end_voltage INTEGER NULL, phase_shift NUMBER NULL);
CREATE UNIQUE INDEX open_circuit_tests_mrid ON open_circuit_tests (mrid);
CREATE INDEX open_circuit_tests_name ON open_circuit_tests (name);

CREATE TABLE operational_restrictions (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, title TEXT NOT NULL, created_date_time TEXT NULL, author_name TEXT NOT NULL, type TEXT NOT NULL, status TEXT NOT NULL, comment TEXT NOT NULL);
CREATE UNIQUE INDEX operational_restrictions_mrid ON operational_restrictions (mrid);
CREATE INDEX operational_restrictions_name ON operational_restrictions (name);

CREATE TABLE overhead_wire_info (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, rated_current NUMBER NULL, material TEXT NOT NULL);
CREATE UNIQUE INDEX overhead_wire_info_mrid ON overhead_wire_info (mrid);
CREATE INDEX overhead_wire_info_name ON overhead_wire_info (name);

CREATE TABLE per_length_sequence_impedances (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, r NUMBER NULL, x NUMBER NULL, r0 NUMBER NULL, x0 NUMBER NULL, bch NUMBER NULL, gch NUMBER NULL, b0ch NUMBER NULL, g0ch NUMBER NULL);
CREATE UNIQUE INDEX per_length_sequence_impedances_mrid ON per_length_sequence_impedances (mrid);
CREATE INDEX per_length_sequence_impedances_name ON per_length_sequence_impedances (name);

CREATE TABLE photo_voltaic_unit (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, power_electronics_connection_mrid TEXT NULL, max_p NUMBER NULL, min_p NUMBER NULL);
CREATE UNIQUE INDEX photo_voltaic_unit_mrid ON photo_voltaic_unit (mrid);
CREATE INDEX photo_voltaic_unit_name ON photo_voltaic_unit (name);
CREATE INDEX photo_voltaic_unit_power_electronics_connection_mrid ON photo_voltaic_unit (power_electronics_connection_mrid);

CREATE TABLE poles (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, classification TEXT NOT NULL);
CREATE UNIQUE INDEX poles_mrid ON poles (mrid);
CREATE INDEX poles_name ON poles (name);

CREATE TABLE position_points (location_mrid TEXT NOT NULL, sequence_number INTEGER NOT NULL, x_position NUMBER NOT NULL, y_position NUMBER NOT NULL);
CREATE UNIQUE INDEX position_points_location_mrid_sequence_number ON position_points (location_mrid, sequence_number);

CREATE TABLE potential_transformer_info (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, accuracy_class TEXT NULL, nominal_ratio_denominator NUMBER NULL, nominal_ratio_numerator NUMBER NULL, primary_ratio NUMBER NULL, pt_class TEXT NULL, rated_voltage INTEGER NULL, secondary_ratio NUMBER NULL);
CREATE UNIQUE INDEX potential_transformer_info_mrid ON potential_transformer_info (mrid);
CREATE INDEX potential_transformer_info_name ON potential_transformer_info (name);

CREATE TABLE potential_transformers (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, terminal_mrid TEXT NULL, potential_transformer_info_mrid TEXT NULL, type TEXT NOT NULL);
CREATE UNIQUE INDEX potential_transformers_mrid ON potential_transformers (mrid);
CREATE INDEX potential_transformers_name ON potential_transformers (name);

CREATE TABLE power_electronics_connection (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL, control_enabled BOOLEAN, regulating_control_mrid TEXT NULL, max_i_fault NUMBER NULL, max_q NUMBER NULL, min_q NUMBER NULL, p NUMBER NULL, q NUMBER NULL, rated_s NUMBER NULL, rated_u NUMBER NULL, inverter_standard TEXT NULL, sustain_op_overvolt_limit INTEGER NULL, stop_at_over_freq NUMBER NULL, stop_at_under_freq NUMBER NULL, inv_volt_watt_resp_mode BOOLEAN NULL, inv_watt_resp_v1 INTEGER NULL, inv_watt_resp_v2 INTEGER NULL, inv_watt_resp_v3 INTEGER NULL, inv_watt_resp_v4 INTEGER NULL, inv_watt_resp_p_at_v1 NUMBER NULL, inv_watt_resp_p_at_v2 NUMBER NULL, inv_watt_resp_p_at_v3 NUMBER NULL, inv_watt_resp_p_at_v4 NUMBER NULL, inv_volt_var_resp_mode BOOLEAN NULL, inv_var_resp_v1 NUMBER NULL, inv_var_resp_v2 NUMBER NULL, inv_var_resp_v3 NUMBER NULL, inv_var_resp_v4 NUMBER NULL, inv_var_resp_q_at_v1 NUMBER NULL, inv_var_resp_q_at_v2 NUMBER NULL, inv_var_resp_q_at_v3 NUMBER NULL, inv_var_resp_q_at_v4 NUMBER NULL, inv_reactive_power_mode BOOLEAN NULL, inv_fix_reactive_power NUMBER NULL);
CREATE UNIQUE INDEX power_electronics_connection_mrid ON power_electronics_connection (mrid);
CREATE INDEX power_electronics_connection_name ON power_electronics_connection (name);

CREATE TABLE power_electronics_connection_phase (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, power_electronics_connection_mrid TEXT NULL, p NUMBER NULL, phase TEXT NOT NULL, q NUMBER NULL);
CREATE UNIQUE INDEX power_electronics_connection_phase_mrid ON power_electronics_connection_phase (mrid);
CREATE INDEX power_electronics_connection_phase_name ON power_electronics_connection_phase (name);
CREATE INDEX power_electronics_connection_phase_power_electronics_connection_mrid ON power_electronics_connection_phase (power_electronics_connection_mrid);

CREATE TABLE power_electronics_wind_unit (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, power_electronics_connection_mrid TEXT NULL, max_p NUMBER NULL, min_p NUMBER NULL);
CREATE UNIQUE INDEX power_electronics_wind_unit_mrid ON power_electronics_wind_unit (mrid);
CREATE INDEX power_electronics_wind_unit_name ON power_electronics_wind_unit (name);
CREATE INDEX power_electronics_wind_unit_power_electronics_connection_mrid ON power_electronics_wind_unit (power_electronics_connection_mrid);

CREATE TABLE power_transformer_ends (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, end_number INTEGER NOT NULL, terminal_mrid TEXT NULL, base_voltage_mrid TEXT NULL, grounded BOOLEAN NOT NULL, r_ground NUMBER NULL, x_ground NUMBER NULL, star_impedance_mrid TEXT NULL, power_transformer_mrid TEXT NULL, connection_kind TEXT NOT NULL, phase_angle_clock INTEGER NULL, b NUMBER NULL, b0 NUMBER NULL, g NUMBER NULL, g0 NUMBER NULL, R NUMBER NULL, R0 NUMBER NULL, rated_u INTEGER NULL, X NUMBER NULL, X0 NUMBER NULL);
CREATE UNIQUE INDEX power_transformer_ends_mrid ON power_transformer_ends (mrid);
CREATE UNIQUE INDEX power_transformer_ends_power_transformer_mrid_end_number ON power_transformer_ends (power_transformer_mrid, end_number);
CREATE INDEX power_transformer_ends_name ON power_transformer_ends (name);
CREATE INDEX power_transformer_ends_star_impedance_mrid ON power_transformer_ends (star_impedance_mrid);
CREATE INDEX power_transformer_ends_power_transformer_mrid ON power_transformer_ends (power_transformer_mrid);

CREATE TABLE power_transformer_end_ratings (power_transformer_end_mrid TEXT NULL, cooling_type TEXT NOT NULL, rated_s INTEGER NOT NULL);
CREATE UNIQUE INDEX power_transformer_end_ratings_power_transformer_end_mrid_cooling_type ON power_transformer_end_ratings (power_transformer_end_mrid, cooling_type);
CREATE INDEX power_transformer_end_ratings_power_transformer_end_mrid ON power_transformer_end_ratings (power_transformer_end_mrid);

CREATE TABLE power_transformer_info (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL);
CREATE UNIQUE INDEX power_transformer_info_mrid ON power_transformer_info (mrid);
CREATE INDEX power_transformer_info_name ON power_transformer_info (name);

CREATE TABLE power_transformers (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL, vector_group TEXT NOT NULL, transformer_utilisation NUMBER NULL, construction_kind TEXT NOT NULL, function TEXT NOT NULL, power_transformer_info_mrid TEXT NULL);
CREATE UNIQUE INDEX power_transformers_mrid ON power_transformers (mrid);
CREATE INDEX power_transformers_name ON power_transformers (name);

CREATE TABLE protection_relay_function_thresholds (protection_relay_function_mrid TEXT NOT NULL, sequence_number INTEGER NOT NULL, unit_symbol TEXT NOT NULL, value NUMBER NOT NULL, name TEXT NULL);
CREATE UNIQUE INDEX protection_relay_function_thresholds_protection_relay_function_mrid_sequence_number ON protection_relay_function_thresholds (protection_relay_function_mrid, sequence_number);
CREATE INDEX protection_relay_function_thresholds_protection_relay_function_mrid ON protection_relay_function_thresholds (protection_relay_function_mrid);

CREATE TABLE protection_relay_function_time_limits (protection_relay_function_mrid TEXT NOT NULL, sequence_number INTEGER NOT NULL, time_limit NUMBER NOT NULL);
CREATE UNIQUE INDEX protection_relay_function_time_limits_protection_relay_function_mrid_sequence_number ON protection_relay_function_time_limits (protection_relay_function_mrid, sequence_number);
CREATE INDEX protection_relay_function_time_limits_protection_relay_function_mrid ON protection_relay_function_time_limits (protection_relay_function_mrid);

CREATE TABLE protection_relay_functions_protected_switches (protection_relay_function_mrid TEXT NOT NULL, protected_switch_mrid TEXT NOT NULL);
CREATE UNIQUE INDEX protection_relay_functions_protected_switches_protection_relay_function_mrid_protected_switch_mrid ON protection_relay_functions_protected_switches (protection_relay_function_mrid, protected_switch_mrid);
CREATE INDEX protection_relay_functions_protected_switches_protection_relay_function_mrid ON protection_relay_functions_protected_switches (protection_relay_function_mrid);
CREATE INDEX protection_relay_functions_protected_switches_protected_switch_mrid ON protection_relay_functions_protected_switches (protected_switch_mrid);

CREATE TABLE protection_relay_functions_sensors (protection_relay_function_mrid TEXT NOT NULL, sensor_mrid TEXT NOT NULL);
CREATE UNIQUE INDEX protection_relay_functions_sensors_protection_relay_function_mrid_sensor_mrid ON protection_relay_functions_sensors (protection_relay_function_mrid, sensor_mrid);
CREATE INDEX protection_relay_functions_sensors_protection_relay_function_mrid ON protection_relay_functions_sensors (protection_relay_function_mrid);
CREATE INDEX protection_relay_functions_sensors_sensor_mrid ON protection_relay_functions_sensors (sensor_mrid);

CREATE TABLE protection_relay_schemes (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, system_mrid TEXT NULL);
CREATE UNIQUE INDEX protection_relay_schemes_mrid ON protection_relay_schemes (mrid);
CREATE INDEX protection_relay_schemes_name ON protection_relay_schemes (name);

CREATE TABLE protection_relay_schemes_protection_relay_functions (protection_relay_scheme_mrid TEXT NOT NULL, protection_relay_function_mrid TEXT NOT NULL);
CREATE UNIQUE INDEX protection_relay_schemes_protection_relay_functions_protection_relay_scheme_mrid_protection_relay_function_mrid ON protection_relay_schemes_protection_relay_functions (protection_relay_scheme_mrid, protection_relay_function_mrid);
CREATE INDEX protection_relay_schemes_protection_relay_functions_protection_relay_scheme_mrid ON protection_relay_schemes_protection_relay_functions (protection_relay_scheme_mrid);
CREATE INDEX protection_relay_schemes_protection_relay_functions_protection_relay_function_mrid ON protection_relay_schemes_protection_relay_functions (protection_relay_function_mrid);

CREATE TABLE protection_relay_systems (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, protection_kind TEXT NOT NULL);
CREATE UNIQUE INDEX protection_relay_systems_mrid ON protection_relay_systems (mrid);
CREATE INDEX protection_relay_systems_name ON protection_relay_systems (name);

CREATE TABLE ratio_tap_changers (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, control_enabled BOOLEAN NOT NULL, high_step INTEGER NULL, low_step INTEGER NULL, neutral_step INTEGER NULL, neutral_u INTEGER NULL, normal_step INTEGER NULL, step NUMBER NULL, tap_changer_control_mrid TEXT NULL, transformer_end_mrid TEXT NULL, step_voltage_increment NUMBER NULL);
CREATE UNIQUE INDEX ratio_tap_changers_mrid ON ratio_tap_changers (mrid);
CREATE UNIQUE INDEX ratio_tap_changers_transformer_end_mrid ON ratio_tap_changers (transformer_end_mrid);
CREATE INDEX ratio_tap_changers_name ON ratio_tap_changers (name);

CREATE TABLE reclosers (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL, normal_open INTEGER NOT NULL, open INTEGER NOT NULL, rated_current INTEGER NULL, switch_info_mrid TEXT NULL, breaking_capacity INTEGER NULL);
CREATE UNIQUE INDEX reclosers_mrid ON reclosers (mrid);
CREATE INDEX reclosers_name ON reclosers (name);

CREATE TABLE reclose_delays (relay_info_mrid TEXT NOT NULL, reclose_delay NUMBER NOT NULL, sequence_number INTEGER NOT NULL);
CREATE UNIQUE INDEX reclose_delays_relay_info_mrid_sequence_number ON reclose_delays (relay_info_mrid, sequence_number);
CREATE INDEX reclose_delays_relay_info_mrid ON reclose_delays (relay_info_mrid);

CREATE TABLE relay_info (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, curve_setting TEXT NULL, reclose_fast BOOLEAN NULL);
CREATE UNIQUE INDEX relay_info_mrid ON relay_info (mrid);
CREATE INDEX relay_info_name ON relay_info (name);

CREATE TABLE remote_controls (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, control_mrid TEXT NULL);
CREATE UNIQUE INDEX remote_controls_mrid ON remote_controls (mrid);
CREATE INDEX remote_controls_name ON remote_controls (name);

CREATE TABLE remote_sources (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, measurement_mrid TEXT NULL);
CREATE UNIQUE INDEX remote_sources_mrid ON remote_sources (mrid);
CREATE INDEX remote_sources_name ON remote_sources (name);

CREATE TABLE series_compensators (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, normally_in_service BOOLEAN, in_service BOOLEAN, commissioned_date TEXT NULL, base_voltage_mrid TEXT NULL, r NUMBER NULL, r0 NUMBER NULL, x NUMBER NULL, x0 NUMBER NULL, varistor_rated_current INTEGER NULL, varistor_voltage_threshold INTEGER NULL);
CREATE UNIQUE INDEX series_compensators_mrid ON series_compensators (mrid);
CREATE INDEX series_compensators_name ON series_compensators (name);

CREATE TABLE short_circuit_tests (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, base_power INTEGER NULL, temperature NUMBER NULL, current NUMBER NULL, energised_end_step INTEGER NULL, grounded_end_step INTEGER NULL, leakage_impedance NUMBER NULL, leakage_impedance_zero NUMBER NULL, loss INTEGER NULL, loss_zero INTEGER NULL, power INTEGER NULL, voltage NUMBER NULL, voltage_ohmic_part NUMBER NULL);
CREATE UNIQUE INDEX short_circuit_tests_mrid ON short_circuit_tests (mrid);
CREATE INDEX short_circuit_tests_name ON short_circuit_tests (name);

CREATE TABLE shunt_compensator_info (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, max_power_loss INTEGER NULL, rated_current INTEGER NULL, rated_reactive_power INTEGER NULL, rated_voltage INTEGER NULL);
CREATE UNIQUE INDEX shunt_compensator_info_mrid ON shunt_compensator_info (mrid);
CREATE INDEX shunt_compensator_info_name ON shunt_compensator_info (name);

CREATE TABLE sites (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL);
CREATE UNIQUE INDEX sites_mrid ON sites (mrid);
CREATE INDEX sites_name ON sites (name);

CREATE TABLE streetlights (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, pole_mrid TEXT NULL, lamp_kind TEXT NOT NULL, light_rating NUMBER NULL);
CREATE UNIQUE INDEX streetlights_mrid ON streetlights (mrid);
CREATE INDEX streetlights_name ON streetlights (name);

CREATE TABLE sub_geographical_regions (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, geographical_region_mrid TEXT NULL);
CREATE UNIQUE INDEX sub_geographical_regions_mrid ON sub_geographical_regions (mrid);
CREATE INDEX sub_geographical_regions_name ON sub_geographical_regions (name);
CREATE INDEX sub_geographical_regions_geographical_region_mrid ON sub_geographical_regions (geographical_region_mrid);

CREATE TABLE substations (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, sub_geographical_region_mrid TEXT NULL);
CREATE UNIQUE INDEX substations_mrid ON substations (mrid);
CREATE INDEX substations_name ON substations (name);
CREATE INDEX substations_sub_geographical_region_mrid ON substations (sub_geographical_region_mrid);

CREATE TABLE switch_info (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, rated_interrupting_time NUMBER NULL);
CREATE UNIQUE INDEX switch_info_mrid ON switch_info (mrid);
CREATE INDEX switch_info_name ON switch_info (name);

CREATE TABLE tap_changer_controls (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, discrete BOOLEAN NULL, mode TEXT NOT NULL, monitored_phase TEXT NOT NULL, target_deadband NUMBER NULL, target_value NUMBER NULL, enabled BOOLEAN NULL, max_allowed_target_value NUMBER NULL, min_allowed_target_value NUMBER NULL, rated_current NUMBER NULL, terminal_mrid TEXT NULL, limit_voltage NUMBER NULL, line_drop_compensation BOOLEAN NULL, line_drop_r NUMBER NULL, line_drop_x NUMBER NULL, reverse_line_drop_r NUMBER NULL, reverse_line_drop_x NUMBER NULL, forward_ldc_blocking BOOLEAN NULL, time_delay NUMBER NULL, co_generation_enabled BOOLEAN NULL);
CREATE UNIQUE INDEX tap_changer_controls_mrid ON tap_changer_controls (mrid);
CREATE INDEX tap_changer_controls_name ON tap_changer_controls (name);

CREATE TABLE terminals (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, conducting_equipment_mrid TEXT NULL, sequence_number INTEGER NOT NULL, connectivity_node_mrid TEXT NULL, phases TEXT NOT NULL);
CREATE UNIQUE INDEX terminals_mrid ON terminals (mrid);
CREATE UNIQUE INDEX terminals_conducting_equipment_mrid_sequence_number ON terminals (conducting_equipment_mrid, sequence_number);
CREATE INDEX terminals_name ON terminals (name);
CREATE INDEX terminals_connectivity_node_mrid ON terminals (connectivity_node_mrid);

CREATE TABLE transformer_end_info (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, connection_kind TEXT NOT NULL, emergency_s INTEGER NULL, end_number INTEGER NOT NULL, insulation_u INTEGER NULL, phase_angle_clock INTEGER NULL, r NUMBER NULL, rated_s INTEGER NULL, rated_u INTEGER NULL, short_term_s INTEGER NULL, transformer_tank_info_mrid TEXT NULL, energised_end_no_load_tests TEXT NULL, energised_end_short_circuit_tests TEXT NULL, grounded_end_short_circuit_tests TEXT NULL, open_end_open_circuit_tests TEXT NULL, energised_end_open_circuit_tests TEXT NULL);
CREATE UNIQUE INDEX transformer_end_info_mrid ON transformer_end_info (mrid);
CREATE INDEX transformer_end_info_name ON transformer_end_info (name);
CREATE INDEX transformer_end_info_transformer_tank_info_mrid ON transformer_end_info (transformer_tank_info_mrid);
CREATE INDEX transformer_end_info_energised_end_no_load_tests ON transformer_end_info (energised_end_no_load_tests);
CREATE INDEX transformer_end_info_energised_end_short_circuit_tests ON transformer_end_info (energised_end_short_circuit_tests);
CREATE INDEX transformer_end_info_grounded_end_short_circuit_tests ON transformer_end_info (grounded_end_short_circuit_tests);
CREATE INDEX transformer_end_info_open_end_open_circuit_tests ON transformer_end_info (open_end_open_circuit_tests);
CREATE INDEX transformer_end_info_energised_end_open_circuit_tests ON transformer_end_info (energised_end_open_circuit_tests);

CREATE TABLE transformer_star_impedance (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, R NUMBER NULL, R0 NUMBER NULL, X NUMBER NULL, X0 NUMBER NULL, transformer_end_info_mrid TEXT NULL);
CREATE UNIQUE INDEX transformer_star_impedance_mrid ON transformer_star_impedance (mrid);
CREATE UNIQUE INDEX transformer_star_impedance_transformer_end_info_mrid ON transformer_star_impedance (transformer_end_info_mrid);
CREATE INDEX transformer_star_impedance_name ON transformer_star_impedance (name);

CREATE TABLE transformer_tank_info (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, power_transformer_info_mrid TEXT NULL);
CREATE UNIQUE INDEX transformer_tank_info_mrid ON transformer_tank_info (mrid);
CREATE INDEX transformer_tank_info_name ON transformer_tank_info (name);
CREATE INDEX transformer_tank_info_power_transformer_info_mrid ON transformer_tank_info (power_transformer_info_mrid);

CREATE TABLE usage_points (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, is_virtual BOOLEAN, connection_category TEXT NULL, rated_power INTEGER NULL, approved_inverter_capacity INTEGER NULL);
CREATE UNIQUE INDEX usage_points_mrid ON usage_points (mrid);
CREATE INDEX usage_points_name ON usage_points (name);

CREATE TABLE usage_points_end_devices (usage_point_mrid TEXT NOT NULL, end_device_mrid TEXT NOT NULL);
CREATE UNIQUE INDEX usage_points_end_devices_usage_point_mrid_end_device_mrid ON usage_points_end_devices (usage_point_mrid, end_device_mrid);
CREATE INDEX usage_points_end_devices_usage_point_mrid ON usage_points_end_devices (usage_point_mrid);
CREATE INDEX usage_points_end_devices_end_device_mrid ON usage_points_end_devices (end_device_mrid);

CREATE TABLE voltage_relays (mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, model TEXT NULL, reclosing BOOLEAN NULL, relay_delay_time NUMBER NULL, protection_kind TEXT NOT NULL, directable BOOLEAN NULL, power_direction TEXT NOT NULL, relay_info_mrid TEXT NULL);
CREATE UNIQUE INDEX voltage_relays_mrid ON voltage_relays (mrid);
CREATE INDEX voltage_relays_name ON voltage_relays (name);
