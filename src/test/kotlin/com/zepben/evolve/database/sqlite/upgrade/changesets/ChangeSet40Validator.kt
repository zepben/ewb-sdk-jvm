/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade.changesets


import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import java.sql.Statement

object ChangeSet40Validator : ChangeSetValidator {

    override fun setUpStatements(): List<String> = listOf(
        /************** insert into these tables to show the presence of power_electronics_connection_mrid pre-change ************/
        """
        INSERT INTO battery_unit (
            mrid, name, description, num_diagram_objects, num_controls, power_electronics_connection_mrid, battery_state
        ) VALUES
            ('id', 'name', 'desc', 1, 2, 'pecmrid1', '0')
        """,
        """
        INSERT INTO photo_voltaic_unit (
            mrid, name, description, num_diagram_objects, num_controls, power_electronics_connection_mrid
        ) VALUES
            ('id', 'name', 'desc', 1, 2, 'pecmrid1')
        """,
        """
        INSERT INTO power_electronics_connection_phase (
            mrid, name, description, num_diagram_objects, num_controls, power_electronics_connection_mrid, phase
        ) VALUES
            ('id', 'name', 'desc', 1, 2, 'pecmrid1', 'XY')
        """,
        """
        INSERT INTO power_electronics_wind_unit (
            mrid, name, description, num_diagram_objects, num_controls, power_electronics_connection_mrid
        ) VALUES
            ('id', 'name', 'desc', 1, 2, 'pecmrid1')
        """,
    )


    override fun populateStatements(): List<String> = listOf(
        /************** insert into power_electronics_connections_power_electronics_connection_phases ************/
        """
        INSERT INTO power_electronics_connections_power_electronics_connection_phases (
            power_electronics_connection_mrid, power_electronics_connection_phase_mrid
        ) VALUES
            ('pecmrid1', 'pecpmrid1'),
            ('pecmrid1', 'pecpmrid2'),
            ('pecmrid2', 'pecpmrid1')
        """,

        /************** insert into power_electronics_connections_power_electronics_units ************/
        """
        INSERT INTO power_electronics_connections_power_electronics_units (
            power_electronics_connection_mrid, power_electronics_unit_mrid
        ) VALUES
            ('pecmrid1', 'peumrid1'),
            ('pecmrid1', 'peumrid2'),
            ('pecmrid2', 'peumrid1')
        """,
    )

    override fun validate(statement: Statement) {
        /************** Check that power_electronics_connection_mrid is no longer in these tables  ************/
        statement.executeQuery("SELECT * FROM battery_unit LIMIT 0").use { rs ->
            assertThat(rs.metaData.columnCount, equalTo(13))
            assertThat(rs.metaData.getColumnLabel(1), equalTo("mrid"))
            assertThat(rs.metaData.getColumnLabel(2), equalTo("name"))
            assertThat(rs.metaData.getColumnLabel(3), equalTo("description"))
            assertThat(rs.metaData.getColumnLabel(4), equalTo("num_diagram_objects"))
            assertThat(rs.metaData.getColumnLabel(5), equalTo("location_mrid"))
            assertThat(rs.metaData.getColumnLabel(6), equalTo("num_controls"))
            assertThat(rs.metaData.getColumnLabel(7), equalTo("normally_in_service"))
            assertThat(rs.metaData.getColumnLabel(8), equalTo("in_service"))
            assertThat(rs.metaData.getColumnLabel(9), equalTo("max_p"))
            assertThat(rs.metaData.getColumnLabel(10), equalTo("min_p"))
            assertThat(rs.metaData.getColumnLabel(11), equalTo("battery_state"))
            assertThat(rs.metaData.getColumnLabel(12), equalTo("rated_e"))
            assertThat(rs.metaData.getColumnLabel(13), equalTo("stored_e"))
        }

        statement.executeQuery("SELECT * FROM photo_voltaic_unit LIMIT 0").use { rs ->
            print('a')
            assertThat(rs.metaData.columnCount, equalTo(10))
            assertThat(rs.metaData.getColumnLabel(1), equalTo("mrid"))
            assertThat(rs.metaData.getColumnLabel(2), equalTo("name"))
            assertThat(rs.metaData.getColumnLabel(3), equalTo("description"))
            assertThat(rs.metaData.getColumnLabel(4), equalTo("num_diagram_objects"))
            assertThat(rs.metaData.getColumnLabel(5), equalTo("location_mrid"))
            assertThat(rs.metaData.getColumnLabel(6), equalTo("num_controls"))
            assertThat(rs.metaData.getColumnLabel(7), equalTo("normally_in_service"))
            assertThat(rs.metaData.getColumnLabel(8), equalTo("in_service"))
            assertThat(rs.metaData.getColumnLabel(9), equalTo("max_p"))
            assertThat(rs.metaData.getColumnLabel(10), equalTo("min_p"))
        }

        statement.executeQuery("SELECT * FROM power_electronics_connection_phase LIMIT 0").use { rs ->
            print('a')
            assertThat(rs.metaData.columnCount, equalTo(9))
            assertThat(rs.metaData.getColumnLabel(1), equalTo("mrid"))
            assertThat(rs.metaData.getColumnLabel(2), equalTo("name"))
            assertThat(rs.metaData.getColumnLabel(3), equalTo("description"))
            assertThat(rs.metaData.getColumnLabel(4), equalTo("num_diagram_objects"))
            assertThat(rs.metaData.getColumnLabel(5), equalTo("location_mrid"))
            assertThat(rs.metaData.getColumnLabel(6), equalTo("num_controls"))
            assertThat(rs.metaData.getColumnLabel(7), equalTo("p"))
            assertThat(rs.metaData.getColumnLabel(8), equalTo("phase"))
            assertThat(rs.metaData.getColumnLabel(9), equalTo("q"))
        }

        statement.executeQuery("SELECT * FROM power_electronics_wind_unit LIMIT 0").use { rs ->
            print('a')
            assertThat(rs.metaData.columnCount, equalTo(10))
            assertThat(rs.metaData.getColumnLabel(1), equalTo("mrid"))
            assertThat(rs.metaData.getColumnLabel(2), equalTo("name"))
            assertThat(rs.metaData.getColumnLabel(3), equalTo("description"))
            assertThat(rs.metaData.getColumnLabel(4), equalTo("num_diagram_objects"))
            assertThat(rs.metaData.getColumnLabel(5), equalTo("location_mrid"))
            assertThat(rs.metaData.getColumnLabel(6), equalTo("num_controls"))
            assertThat(rs.metaData.getColumnLabel(7), equalTo("normally_in_service"))
            assertThat(rs.metaData.getColumnLabel(8), equalTo("in_service"))
            assertThat(rs.metaData.getColumnLabel(9), equalTo("max_p"))
            assertThat(rs.metaData.getColumnLabel(10), equalTo("min_p"))
        }

        /************** Ensure index was recreated  ************/
        statement.executeQuery("pragma index_info('power_electronics_connections_power_electronics_connection_phases_pecmrid_pecpmrid')").use { rs ->
            assertThat(rs.next(), equalTo(true))
        }

        statement.executeQuery("pragma index_info('power_electronics_connections_power_electronics_connection_phases_pecmrid')").use { rs ->
            assertThat(rs.next(), equalTo(true))
        }

        statement.executeQuery("pragma index_info('power_electronics_connections_power_electronics_connection_phases_pecpmrid')").use { rs ->
            assertThat(rs.next(), equalTo(true))
        }

        statement.executeQuery("pragma index_info('power_electronics_connections_power_electronics_units_pecmrid_peumrid')").use { rs ->
            assertThat(rs.next(), equalTo(true))
        }

        statement.executeQuery("pragma index_info('power_electronics_connections_power_electronics_units_pecmrid')").use { rs ->
            assertThat(rs.next(), equalTo(true))
        }

        statement.executeQuery("pragma index_info('power_electronics_connections_power_electronics_units_peumrid')").use { rs ->
            assertThat(rs.next(), equalTo(true))
        }

        /************** Ensure entries are inserted into new tables  ************/
        validateRows(statement, "select * from power_electronics_connections_power_electronics_connection_phases",
            { rs ->
                assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("pecmrid1"))
                assertThat(rs.getString("power_electronics_connection_phase_mrid"), equalTo("pecpmrid1"))
            }, { rs ->
                assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("pecmrid1"))
                assertThat(rs.getString("power_electronics_connection_phase_mrid"), equalTo("pecpmrid2"))
            }, { rs ->
                assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("pecmrid2"))
                assertThat(rs.getString("power_electronics_connection_phase_mrid"), equalTo("pecpmrid1"))
            }
        )

        validateRows(statement, "select * from power_electronics_connections_power_electronics_units",
            { rs ->
                assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("pecmrid1"))
                assertThat(rs.getString("power_electronics_unit_mrid"), equalTo("peumrid1"))
            }, { rs ->
                assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("pecmrid1"))
                assertThat(rs.getString("power_electronics_unit_mrid"), equalTo("peumrid2"))
            }, { rs ->
                assertThat(rs.getString("power_electronics_connection_mrid"), equalTo("pecmrid2"))
                assertThat(rs.getString("power_electronics_unit_mrid"), equalTo("peumrid1"))
            }
        )
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM power_electronics_connection_phase",
        "DELETE FROM battery_unit",
        "DELETE FROM photo_voltaic_unit",
        "DELETE FROM power_electronics_wind_unit",
        "DELETE FROM power_electronics_connections_power_electronics_connection_phases",
        "DELETE FROM power_electronics_connections_power_electronics_units",
    )

}
