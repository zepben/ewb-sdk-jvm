/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.changeset

import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.*
import com.zepben.ewb.database.sql.cim.CimReader
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.*
import com.zepben.ewb.database.sql.extensions.getNullableString
import com.zepben.ewb.services.common.extensions.ensureGet
import com.zepben.ewb.services.common.extensions.getOrThrow
import com.zepben.ewb.services.variant.VariantService
import java.sql.ResultSet

/**
 * A class for reading the [VariantService] tables from the database.
 */
internal class ChangeSetCimReader : CimReader<VariantService>(), AutoCloseable{

    override fun close() {
    }

    // ###################################
    // # IEC61970 Part303 GenericDataSet #
    // ###################################

    fun read(service: VariantService, table: TableChangeSets, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val changeSet = ChangeSet(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            networkModelProjectStage =
                service.ensureGet<NetworkModelProjectStage>(resultSet.getNullableString(table.NETWORK_MODEL_PROJECT_STAGE_MRID.queryIndex), typeNameAndMRID())
        }

        return readDataSet(changeSet, table, resultSet) && service.addOrThrow(changeSet)

    }

    private fun readDataSet(dataSet: DataSet, table: TableDataSets, resultSet: ResultSet): Boolean {
        dataSet.apply {
            name = resultSet.getNullableString(table.NAME.queryIndex)
            description = resultSet.getNullableString(table.DESCRIPTION.queryIndex)
        }

        return true
    }

    fun readChangeSetMember(service: VariantService, table: TableChangeSetMembers, resultSet: ResultSet, setIdentifier: (String) -> String, typeNameAndMRID: String, creator: (ChangeSet, String) -> ChangeSetMember): Boolean {
        val changeSetMRID = resultSet.getString(table.CHANGE_SET_MRID.queryIndex)
        setIdentifier("$changeSetMRID-to-UNKNOWN")
        val targetObj = resultSet.getString(table.TARGET_OBJECT_MRID.queryIndex)
        val id = setIdentifier("$changeSetMRID-to-$targetObj")

        val changeSet = service.getOrThrow<ChangeSet>(mRID = changeSetMRID, typeNameAndMRID = "$typeNameAndMRID $id")
        val obj = creator(changeSet, targetObj)
        changeSet.addMember(obj)

        return true
    }

    fun read(service: VariantService, table: TableObjectCreations, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        return readChangeSetMember(service, table, resultSet, setIdentifier, "ObjectCreation") { changeSet, targetObjMRID ->
            ObjectCreation(changeSet, targetObjMRID)
        }
    }

    fun read(service: VariantService, table: TableObjectDeletions, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        return readChangeSetMember(service, table, resultSet, setIdentifier, "ObjectDeletion") { changeSet, targetObjMRID ->
            ObjectCreation(changeSet, targetObjMRID)
        }
    }

    fun read(service: VariantService, table: TableObjectModifications, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val originalObj = resultSet.getString(table.OBJECT_REVERSE_MODIFICATION_TARGET_OBJECT_MRID.queryIndex)
        setIdentifier(originalObj)

        return readChangeSetMember(service, table, resultSet, setIdentifier, "ObjectModification") { changeSet, targetObjMRID ->
            ObjectModification(changeSet, targetObjMRID, originalObj).also {
                changeSet.addMember(it.objectReverseModification)
            }
        }
    }

}
