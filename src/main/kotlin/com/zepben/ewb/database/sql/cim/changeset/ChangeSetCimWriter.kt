/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.changeset

import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSetMember
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.DataSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectCreation
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectDeletion
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectModification
import com.zepben.ewb.database.sql.cim.CimWriter
import com.zepben.ewb.database.sql.extensions.setNullableString
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.TableChangeSetChangeSetMembers
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.TableChangeSetMembers
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.TableChangeSets
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.TableDataSets
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectCreations
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectDeletions
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectModifications
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

    @Throws(SQLException::class)
    private fun writeChangeSetMember(
        table: TableChangeSetMembers,
        insert: PreparedStatement,
        changeSetMember: ChangeSetMember,
        description: String
    ): Boolean {
        insert.setNullableString(table.CHANGE_SET_MRID.queryIndex, changeSetMember.changeSet?.mRID)
        insert.setNullableString(table.TARGET_OBJECT_MRID.queryIndex, changeSetMember.targetObjectMRID)

        return insert.tryExecuteSingleUpdate(description)

    }

    // TODO: docs
    fun write(changeSet: ChangeSet): Boolean {
        val table = databaseTables.getTable<TableChangeSets>()
        val insert = databaseTables.getInsert<TableChangeSets>()

        changeSet.networkModelProjectStage?.let {
            insert.setNullableString(table.NETWORK_MODEL_PROJECT_STAGE_MRID.queryIndex, it.mRID)
        }
        changeSet.changeSetMembers.forEach {
            when (it) {
                is ObjectCreation -> write(it)
                is ObjectDeletion -> write(it)
                is ObjectModification -> write(it)
                else -> throw NotImplementedError()
            }
        }

        return writeDataSet(table, insert, changeSet, "change set")
    }

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

    // TODO: docs
    fun write(objectCreation: ObjectCreation): Boolean {
        val table = databaseTables.getTable<TableObjectCreations>()
        val insert = databaseTables.getInsert<TableObjectCreations>()

        return writeChangeSetMember(table, insert, objectCreation, "object creation")
    }

    // TODO: docs
    fun write(objectDeletion: ObjectDeletion): Boolean {
        val table = databaseTables.getTable<TableObjectDeletions>()
        val insert = databaseTables.getInsert<TableObjectDeletions>()

        return writeChangeSetMember(table, insert, objectDeletion, "object deletion")
    }

    // TODO: docs
    fun write(objectModification: ObjectModification): Boolean {
        val table = databaseTables.getTable<TableObjectModifications>()
        val insert = databaseTables.getInsert<TableObjectModifications>()

        objectModification.objectReverseModification?.let {
            insert.setNullableString(table.OBJECT_REVERSE_MODIFICATION_TARGET_OBJECT_MRID.queryIndex, it.targetObjectMRID)
        }

        return writeChangeSetMember(table, insert, objectModification, "object modification")
    }

    private fun writeAssociation(changeSet: ChangeSet, changeSetMember: ChangeSetMember): Boolean {
        val table = databaseTables.getTable<TableChangeSetChangeSetMembers>()
        val insert = databaseTables.getInsert<TableChangeSetChangeSetMembers>()

        insert.setString(table.CHANGE_SET_MRID.queryIndex, changeSet.mRID)
        //insert.setString(table.CHANGE_SET_MEMBER_MRID.queryIndex, changeSetMember.mRID) TODO: no id?!

        return insert.tryExecuteSingleUpdate("change set to change set member association")
    }
}
