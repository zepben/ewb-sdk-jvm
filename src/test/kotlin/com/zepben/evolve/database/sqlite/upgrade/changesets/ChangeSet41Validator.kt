/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade.changesets

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import java.sql.ResultSet
import java.sql.Statement

object ChangeSet41Validator : ChangeSetValidator {
    override fun setUpStatements(): List<String> = listOf(
        insertExistingLocationStreetAddress("loc1")
    )

    override fun populateStatements(): List<String> = listOf(
        insertNewLocationStreetAddress("loc2", "Empire State Building", "23", "Fifth", "350", "15", "Avenue", "350 Fifth Avenue", "1234"),
        insertNewLocationStreetAddress("loc3", "Sydney Opera House", "G", "Bennelong", "", "", "Point", "Bennelong Point 2000", "5678")
    )

    override fun validate(statement: Statement) {
        validateRows(
            statement,
            "SELECT * FROM location_street_addresses",
            validateLocationStreetAddress("loc1", null, null, null, null, null, null, null, null),
            validateLocationStreetAddress("loc2", "Empire State Building", "23", "Fifth", "350", "15", "Avenue", "350 Fifth Avenue", "1234"),
            validateLocationStreetAddress("loc3", "Sydney Opera House", "G", "Bennelong", "", "", "Point", "Bennelong Point 2000", "5678")
        )
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM location_street_addresses"
    )

    private fun validateLocationStreetAddress(
        mrid: String,
        buildingName: String?,
        floorIdentification: String?,
        streetName: String?,
        number: String?,
        suiteNumber: String?,
        type: String?,
        displayAddress: String?,
        poBox: String?
    ): (ResultSet) -> Unit = { rs ->
        assertThat(rs.getString("location_mrid"), equalTo(mrid))
        assertThat(rs.getString("building_name"), equalTo(buildingName))
        assertThat(rs.getString("floor_identification"), equalTo(floorIdentification))
        assertThat(rs.getString("name"), equalTo(streetName))
        assertThat(rs.getString("number"), equalTo(number))
        assertThat(rs.getString("suite_number"), equalTo(suiteNumber))
        assertThat(rs.getString("type"), equalTo(type))
        assertThat(rs.getString("display_address"), equalTo(displayAddress))
        assertThat(rs.getString("po_box"), equalTo(poBox))
    }

    private fun insertExistingLocationStreetAddress(mrid: String) =
        """
        INSERT INTO location_street_addresses (
            location_mrid, address_field, postal_code, town_name, state_or_province
        ) VALUES ( 
            '$mrid', '', '', '', ''
        )
        """

    private fun insertNewLocationStreetAddress(
        mrid: String,
        buildingName: String,
        floorIdentification: String,
        streetName: String,
        number: String,
        suiteNumber: String,
        type: String,
        displayAddress: String,
        poBox: String
    ) =
        """
        INSERT INTO location_street_addresses (
            location_mrid, address_field, postal_code, town_name, state_or_province,
            building_name, floor_identification, name, number, suite_number, type, display_address, po_box
        ) VALUES ( 
            '$mrid', '', '', '', '',
            '$buildingName', '$floorIdentification', '$streetName', '$number', '$suiteNumber', '$type', '$displayAddress', '$poBox'
        )
        """

}
