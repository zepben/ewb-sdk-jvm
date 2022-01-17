/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.TransformerConstructionKind
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.TransformerConstructionKind.*
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.TransformerFunctionKind
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.TransformerFunctionKind.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import java.sql.ResultSet
import java.sql.Statement

object ChangeSet39Validator : ChangeSetValidator {

    override fun setUpStatements(): List<String> = listOf(
        *`insert existing power transformers`,
        *`insert diagram objects`
    )

    override fun populateStatements(): List<String> = listOf(
        *`insert new power transformers`
    )

    override fun validate(statement: Statement) {
        validateRows(
            statement,
            "select * from power_transformers",
            validatePowerTransformer("unk", unknown, other),
            validatePowerTransformer("dist", unknown, distributionTransformer),
            validatePowerTransformer("r_reg", unknown, voltageRegulator),
            validatePowerTransformer("nr_reg", unknown, voltageRegulator),
            validatePowerTransformer("iso", unknown, isolationTransformer),
            validatePowerTransformer("zone", unknown, powerTransformer),
            validatePowerTransformer("new1", overhead, autotransformer),
            validatePowerTransformer("new2", underground, secondaryTransformer)
        )
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM power_transformers",
        "DELETE FROM diagram_objects"
    )

    private fun validatePowerTransformer(
        mrid: String,
        constructionKind: TransformerConstructionKind,
        functionKind: TransformerFunctionKind
    ): (ResultSet) -> Unit = { rs ->
        assertThat(rs.getString("mrid"), equalTo(mrid))
        assertThat(rs.getString("construction_kind"), equalTo(constructionKind.name))
        assertThat(rs.getString("function"), equalTo(functionKind.name))
    }

    @Suppress("ObjectPropertyName")
    private val `insert existing power transformers` = arrayOf(
        insertExistingPowerTransformer("unk"),
        insertExistingPowerTransformer("dist"),
        insertExistingPowerTransformer("r_reg"),
        insertExistingPowerTransformer("nr_reg"),
        insertExistingPowerTransformer("iso"),
        insertExistingPowerTransformer("zone")
    )

    @Suppress("ObjectPropertyName")
    private val `insert diagram objects` = arrayOf(
        insertDiagramObject("dist", "DIST_TRANSFORMER"),
        insertDiagramObject("r_reg", "REVERSIBLE_REGULATOR"),
        insertDiagramObject("nr_reg", "NON_REVERSIBLE_REGULATOR"),
        insertDiagramObject("iso", "ISO_TRANSFORMER"),
        insertDiagramObject("zone", "ZONE_TRANSFORMER")
    )

    @Suppress("ObjectPropertyName")
    private val `insert new power transformers` = arrayOf(
        insertNewPowerTransformer("new1", overhead, autotransformer),
        insertNewPowerTransformer("new2", underground, secondaryTransformer)
    )

    private fun insertExistingPowerTransformer(mrid: String) =
        """
        INSERT INTO power_transformers (
            mrid, name, description, num_controls, vector_group
        ) VALUES ( 
            '$mrid', '', '', 0, 'unknown'
        )
        """

    private fun insertNewPowerTransformer(mrid: String, construction: TransformerConstructionKind, function: TransformerFunctionKind) =
        """
        INSERT INTO power_transformers (
            mrid, name, description, num_controls, vector_group, construction_kind, function
        ) VALUES ( 
            '$mrid', '', '', 0, 'unknown', '$construction', '$function'
        )
        """

    private fun insertDiagramObject(mrid: String, style: String) =
        """
        INSERT INTO diagram_objects (
            mrid, name, description, num_diagram_objects, identified_object_mrid, diagram_mrid, style, rotation
        ) VALUES ( 
            '$mrid-do', '', '', 0, '$mrid', 'd', '$style', 0
        )
        """

}
