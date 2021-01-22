/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade

internal fun changeSet25() = ChangeSet(25) {
    listOf(
        "CREATE TABLE power_electronics_connection(" +
            "mrid TEXT NOT NULL," +
            "max_i_fault NUMBER NOT NULL," +
            "max_q NUMBER NOT NULL," +
            "min_q NUMBER NOT NULL," +
            "p NUMBER NOT NULL," +
            "q NUMBER NOT NULL," +
            "rated_s NUMBER NOT NULL," +
            "rated_u NUMBER NOT NULL," +
            "control_enabled BOOLEAN," +
            "base_voltage_mrid NUMBER NULL," +
            "normally_in_service BOOLEAN," +
            "in_service BOOLEAN," +
            "location_mrid TEXT NULL," +
            "num_controls INTEGER NOT NULL," +
            "name TEXT NOT NULL," +
            "description TEXT NOT NULL," +
            "num_diagram_objects INTEGER NOT_NULL" +
            ")",
        "CREATE TABLE power_electronics_connection_phase(" +
            "mrid TEXT NOT NULL," +
            "power_electronics_connection_mrid TEXT NULL," +
            "p NUMBER NOT NULL," +
            "phase TEXT NOT NULL," +
            "q NUMBER NOT NULL," +
            "location_mrid TEXT NULL," +
            "num_controls INTEGER NOT NULL," +
            "name TEXT NOT NULL," +
            "description TEXT NOT NULL," +
            "num_diagram_objects INTEGER NOT_NULL" +
            ")",
        "CREATE TABLE battery_unit(" +
            "mrid TEXT NOT NULL," +
            "battery_state TEXT NOT NULL," +
            "rated_e NUMBER NOT NULL," +
            "stored_e NUMBER NOT NULL," +
            "power_electronics_connection_mrid TEXT NULL," +
            "max_p NUMBER NOT NULL," +
            "min_p NUMBER NOT NULL," +
            "normally_in_service BOOLEAN," +
            "in_service BOOLEAN," +
            "location_mrid TEXT NULL," +
            "num_controls INTEGER NOT NULL," +
            "name TEXT NOT NULL," +
            "description TEXT NOT NULL," +
            "num_diagram_objects INTEGER NOT_NULL" +
            ")",
        "CREATE TABLE photo_voltaic_unit(" +
            "mrid TEXT NOT NULL," +
            "power_electronics_connection_mrid TEXT NULL," +
            "max_p NUMBER NOT NULL," +
            "min_p NUMBER NOT NULL," +
            "normally_in_service BOOLEAN," +
            "in_service BOOLEAN," +
            "location_mrid TEXT NULL," +
            "num_controls INTEGER NOT NULL," +
            "name TEXT NOT NULL," +
            "description TEXT NOT NULL," +
            "num_diagram_objects INTEGER NOT_NULL" +
            ")",
        "CREATE TABLE power_electronics_wind_unit(" +
            "mrid TEXT NOT NULL," +
            "power_electronics_connection_mrid TEXT NULL," +
            "max_p NUMBER NOT NULL," +
            "min_p NUMBER NOT NULL," +
            "normally_in_service BOOLEAN," +
            "in_service BOOLEAN," +
            "location_mrid TEXT NULL," +
            "num_controls INTEGER NOT NULL," +
            "name TEXT NOT NULL," +
            "description TEXT NOT NULL," +
            "num_diagram_objects INTEGER NOT_NULL" +
            ")",
    )
}
