/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.changeset

import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.*
import com.zepben.ewb.database.sql.cim.CimReader
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.*
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
        val changeSetMRID = setIdentifier(resultSet.getString(table.MRID.queryIndex))
        return service.addOrThrow(ChangeSet(changeSetMRID).also {
            readDataSet(it, table, resultSet)
        })

    }

    private fun readDataSet(
        dataSet: DataSet,
        table: TableDataSets,
        resultSet: ResultSet
    ): Boolean {
        dataSet.apply {
            name = resultSet.getString(table.NAME.queryIndex)
            description = resultSet.getString(table.DESCRIPTION.queryIndex)
        }

        return true
    }

    fun read(service: VariantService, table: TableObjectCreations, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val changeSetMRID = resultSet.getString(table.CHANGE_SET_MRID.queryIndex)
        val targetObj = resultSet.getString(table.TARGET_OBJECT_MRID.queryIndex)
        setIdentifier("$changeSetMRID-$targetObj")
        val changeSet = service.getOrThrow<ChangeSet>(mRID = changeSetMRID, typeNameAndMRID = "ObjectCreation $targetObj")
        val obj = ObjectCreation(changeSet, targetObj)
        changeSet.addMember(obj)

        return true
    }

    fun read(service: VariantService, table: TableObjectDeletions, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val changeSetMRID = resultSet.getString(table.CHANGE_SET_MRID.queryIndex)
        val targetObj = resultSet.getString(table.TARGET_OBJECT_MRID.queryIndex)
        setIdentifier("$changeSetMRID-$targetObj")
        val changeSet = service.getOrThrow<ChangeSet>(mRID = changeSetMRID, typeNameAndMRID = "ObjectDeletion $targetObj")
        val obj = ObjectDeletion(changeSet, targetObj)
        changeSet.addMember(obj)

        return true
    }

    fun read(service: VariantService, table: TableObjectModifications, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val changeSetMRID = resultSet.getString(table.CHANGE_SET_MRID.queryIndex)
        val targetObj = resultSet.getString(table.TARGET_OBJECT_MRID.queryIndex)
        val originalObj = resultSet.getString(table.OBJECT_REVERSE_MODIFICATION_TARGET_OBJECT_MRID.queryIndex)
        setIdentifier("$changeSetMRID-$targetObj")
        val changeSet = service.getOrThrow<ChangeSet>(mRID = changeSetMRID, typeNameAndMRID = "ObjectModification $targetObj")
        val obj = ObjectModification(changeSet, targetObj, originalObj)
        changeSet.addMember(obj)
        changeSet.addMember(obj.objectReverseModification)

        return true
    }

}
