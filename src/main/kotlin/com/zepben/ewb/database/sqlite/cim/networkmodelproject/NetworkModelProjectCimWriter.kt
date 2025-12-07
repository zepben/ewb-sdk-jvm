/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.networkmodelproject

import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSetMember
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.DataSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectCreation
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectDeletion
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectModification
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectReverseModification
import com.zepben.ewb.database.sql.extensions.setNullableString
import com.zepben.ewb.database.sqlite.cim.CimWriter
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableChangeSetChangeSetMembers
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableChangeSetMembers
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableChangeSets
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableDataSets
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectCreations
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectDeletions
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectModifications
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectReverseModifications
import java.sql.PreparedStatement
import java.sql.SQLException

class NetworkModelProjectCimWriter(
    override val databaseTables: NetworkModelProjectDatabaseTables
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
        insert.setString(table.CHANGE_SET_MRID.queryIndex, changeSetMember.changeSet.mRID)
        insert.setString(table.TARGET_OBJECT_MRID.queryIndex, changeSetMember.targetObject.mRID)

        return insert.tryExecuteSingleUpdate(description)

    }

    fun write(changeSet: ChangeSet): Boolean {
        val table = databaseTables.getTable<TableChangeSets>()
        val insert = databaseTables.getInsert<TableChangeSets>()

        changeSet.networkModelProjectStage?.let {
            insert.setNullableString(table.NETWORK_MODEL_PROJECT_STAGE_MRID.queryIndex, it.mRID)
        }

        return writeDataSet(table, insert, changeSet, "change set")
    }

    fun writeDataSet(
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

    fun write(objectCreation: ObjectCreation): Boolean {
        val table = databaseTables.getTable<TableObjectCreations>()
        val insert = databaseTables.getInsert<TableObjectCreations>()

        return writeChangeSetMember(table, insert, objectCreation, "object creation")
    }

    fun write(objectDeletion: ObjectDeletion): Boolean {
        val table = databaseTables.getTable<TableObjectDeletions>()
        val insert = databaseTables.getInsert<TableObjectDeletions>()

        return writeChangeSetMember(table, insert, objectDeletion, "object deletion")
    }

    fun write(objectModification: ObjectModification): Boolean {
        val table = databaseTables.getTable<TableObjectModifications>()
        val insert = databaseTables.getInsert<TableObjectModifications>()

        // FIXME: no mRID, should we introduce an ID, or mix the 2 classes into the same table / GRPC message...
        //insert.setNullableString(table.OBJECT_REVERSE_MODIFICATION_MRID.queryIndex, objectModification.objectReverseModification.mRID)

        return writeChangeSetMember(table, insert, objectModification, "object modification")
    }

    fun write(objectReverseModification: ObjectReverseModification): Boolean {
        val table = databaseTables.getTable<TableObjectReverseModifications>()
        val insert = databaseTables.getInsert<TableObjectReverseModifications>()

        // FIXME: no mRID, should we introduce an ID, or mix the 2 classes into the same table / GRPC message...
        //insert.setNullableString(table.OBJECT_MODIFICATION_MRID.queryIndex, objectReverseModification.objectModification.mRID)

        return writeChangeSetMember(table, insert, objectReverseModification, "object reverse modification")
    }

    private fun writeAssociation(changeSet: ChangeSet, changeSetMember: ChangeSetMember): Boolean {
        val table = databaseTables.getTable<TableChangeSetChangeSetMembers>()
        val insert = databaseTables.getInsert<TableChangeSetChangeSetMembers>()

        insert.setString(table.CHANGE_SET_MRID.queryIndex, changeSet.mRID)
        //insert.setString(table.CHANGE_SET_MEMBER_MRID.queryIndex, changeSetMember.mRID) TODO: no id?!

        return insert.tryExecuteSingleUpdate("change set to change set member association")
    }
}