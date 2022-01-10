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
        """
        INSERT INTO power_transformers (
            mrid, name, description, num_controls, vector_group
        ) VALUES ( 
            'unk', '', '', 0, 'unknown'
        )
        """,
        """
        INSERT INTO power_transformers (
            mrid, name, description, num_controls, vector_group
        ) VALUES ( 
            'dist', '', '', 0, 'unknown'
        )
        """,
        """
        INSERT INTO power_transformers (
            mrid, name, description, num_controls, vector_group
        ) VALUES ( 
            'r_reg', '', '', 0, 'unknown'
        )
        """,
        """
        INSERT INTO power_transformers (
            mrid, name, description, num_controls, vector_group
        ) VALUES ( 
            'nr_reg', '', '', 0, 'unknown'
        )
        """,
        """
        INSERT INTO power_transformers (
            mrid, name, description, num_controls, vector_group
        ) VALUES ( 
            'iso', '', '', 0, 'unknown'
        )
        """,
        """
        INSERT INTO power_transformers (
            mrid, name, description, num_controls, vector_group
        ) VALUES ( 
            'zone', '', '', 0, 'unknown'
        )
        """
    )

    @Suppress("ObjectPropertyName")
    private val `insert diagram objects` = arrayOf(
        """
        INSERT INTO diagram_objects (
            mrid, name, description, num_diagram_objects, identified_object_mrid, diagram_mrid, style, rotation
        ) VALUES ( 
            'dist-do', '', '', 0, 'dist', 'd', 'DIST_TRANSFORMER', 0
        )
        """,
        """
        INSERT INTO diagram_objects (
            mrid, name, description, num_diagram_objects, identified_object_mrid, diagram_mrid, style, rotation
        ) VALUES ( 
            'r_reg-do', '', '', 0, 'r_reg', 'd', 'REVERSIBLE_REGULATOR', 0
        )
        """,
        """
        INSERT INTO diagram_objects (
            mrid, name, description, num_diagram_objects, identified_object_mrid, diagram_mrid, style, rotation
        ) VALUES ( 
            'nr_reg-do', '', '', 0, 'nr_reg', 'd', 'NON_REVERSIBLE_REGULATOR', 0
        )
        """,
        """
        INSERT INTO diagram_objects (
            mrid, name, description, num_diagram_objects, identified_object_mrid, diagram_mrid, style, rotation
        ) VALUES ( 
            'iso-do', '', '', 0, 'iso', 'd', 'ISO_TRANSFORMER', 0
        )
        """,
        """
        INSERT INTO diagram_objects (
            mrid, name, description, num_diagram_objects, identified_object_mrid, diagram_mrid, style, rotation
        ) VALUES ( 
            'zone-do', '', '', 0, 'zone', 'd', 'ZONE_TRANSFORMER', 0
        )
        """
    )

    @Suppress("ObjectPropertyName")
    private val `insert new power transformers` = arrayOf(
        """
        INSERT INTO power_transformers (
            mrid, name, description, num_controls, vector_group, construction_kind, function
        ) VALUES ( 
            'new1', '', '', 0, 'unknown', 'overhead', 'autotransformer'
        )
        """,
        """
        INSERT INTO power_transformers (
            mrid, name, description, num_controls, vector_group, construction_kind, function
        ) VALUES ( 
            'new2', '', '', 0, 'unknown', 'underground', 'secondaryTransformer'
        )
        """
    )

}
