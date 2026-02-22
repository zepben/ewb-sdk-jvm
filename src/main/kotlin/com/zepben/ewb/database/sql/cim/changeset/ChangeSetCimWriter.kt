/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.changeset

import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.*
import com.zepben.ewb.database.sql.cim.CimWriter
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.*
import com.zepben.ewb.database.sql.extensions.setNullableString
import com.zepben.ewb.services.variant.VariantService
import java.sql.PreparedStatement
import java.sql.SQLException

/**
 * A class for writing the [VariantService] tables to the database.
 *
 * @property databaseTables The tables available in the database.
 */
class ChangeSetCimWriter(
    override val databaseTables: ChangeSetDatabaseTables
) : CimWriter(databaseTables) {

    // ###################################
    // # IEC61970 Part303 GenericDataSet #
    // ###################################

    /**
     * Write the [ChangeSet] fields to [TableChangeSets].
     *
     * @param changeSet The [ChangeSet] instance to write to the database.
     *
     * @return true if the [ChangeSet] was successfully written to the database, otherwise false.
     * @throws SQLException For any errors encountered writing to the database.
     */
    fun write(changeSet: ChangeSet): Boolean {
        val table = databaseTables.getTable<TableChangeSets>()
        val insert = databaseTables.getInsert<TableChangeSets>()

        insert.setNullableString(table.NETWORK_MODEL_PROJECT_STAGE_MRID.queryIndex, changeSet.networkModelProjectStage?.mRID)

        return writeDataSet(table, insert, changeSet, "change set")
    }

    @Throws(SQLException::class)
    private fun writeChangeSetMember(table: TableChangeSetMembers, insert: PreparedStatement, changeSetMember: ChangeSetMember, description: String): Boolean {
        insert.setNullableString(table.CHANGE_SET_MRID.queryIndex, changeSetMember.changeSet.mRID)
        insert.setNullableString(table.TARGET_OBJECT_MRID.queryIndex, changeSetMember.targetObjectMRID)

        return insert.tryExecuteSingleUpdate(description)

    }

    @Suppress("SameParameterValue")  // description - suppressed as maybe we'll need it in the future.
    private fun writeDataSet(
        table: TableDataSets,
        insert: PreparedStatement,
        dataSet: DataSet,
        description: String
    ): Boolean {
        insert.setString(table.MRID.queryIndex, dataSet.mRID)
        insert.setNullableString(table.NAME.queryIndex, dataSet.name)
        insert.setNullableString(table.DESCRIPTION.queryIndex, dataSet.description)

        return insert.tryExecuteSingleUpdate(description)

    }

    /**
     * Write the [ObjectCreation] fields to [TableObjectCreations].
     *
     * @param objectCreation The [ObjectCreation] instance to write to the database.
     *
     * @return true if the [ObjectCreation] was successfully written to the database, otherwise false.
     * @throws SQLException For any errors encountered writing to the database.
     */
    fun write(objectCreation: ObjectCreation): Boolean {
        val table = databaseTables.getTable<TableObjectCreations>()
        val insert = databaseTables.getInsert<TableObjectCreations>()

        return writeChangeSetMember(table, insert, objectCreation, "object creation")
    }

    /**
     * Write the [ObjectDeletion] fields to [TableObjectDeletions].
     *
     * @param objectDeletion The [ObjectDeletion] instance to write to the database.
     *
     * @return true if the [ObjectDeletion] was successfully written to the database, otherwise false.
     * @throws SQLException For any errors encountered writing to the database.
     */
    fun write(objectDeletion: ObjectDeletion): Boolean {
        val table = databaseTables.getTable<TableObjectDeletions>()
        val insert = databaseTables.getInsert<TableObjectDeletions>()

        return writeChangeSetMember(table, insert, objectDeletion, "object deletion")
    }

    /**
     * Write the [ObjectModification] fields to [TableObjectModifications].
     *
     * @param objectModification The [ObjectModification] instance to write to the database.
     *
     * @return true if the [ObjectModification] was successfully written to the database, otherwise false.
     * @throws SQLException For any errors encountered writing to the database.
     */
    fun write(objectModification: ObjectModification): Boolean {
        val table = databaseTables.getTable<TableObjectModifications>()
        val insert = databaseTables.getInsert<TableObjectModifications>()

        insert.setNullableString(table.OBJECT_REVERSE_MODIFICATION_TARGET_OBJECT_MRID.queryIndex, objectModification.objectReverseModification.mRID)

        return writeChangeSetMember(table, insert, objectModification, "object modification")
    }

    /**
     * Write the [ObjectReverseModification] fields to [TableObjectReverseModifications].
     *
     * @param objectModification The [ObjectReverseModification] instance to write to the database.
     *
     * @return true if the [ObjectReverseModification] was successfully written to the database, otherwise false.
     * @throws SQLException For any errors encountered writing to the database.
     */
    fun write(objectReverseModification: ObjectReverseModification): Boolean {
        val table = databaseTables.getTable<TableObjectReverseModifications>()
        val insert = databaseTables.getInsert<TableObjectReverseModifications>()

        insert.setNullableString(table.OBJECT_MODIFICATION_MRID.queryIndex, objectReverseModification.objectModification.mRID)

        return writeChangeSetMember(table, insert, objectReverseModification, "object reverse modification")
    }

}
