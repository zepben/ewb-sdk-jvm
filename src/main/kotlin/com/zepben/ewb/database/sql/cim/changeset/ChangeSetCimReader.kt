/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
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
import com.zepben.ewb.database.sql.cim.CimReader
import com.zepben.ewb.database.sql.extensions.getNullableString
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.TableChangeSetMembers
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.TableChangeSets
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.TableDataSets
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectCreations
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectDeletions
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectModifications
import com.zepben.ewb.database.sql.common.MRIDLookupException
import com.zepben.ewb.services.common.extensions.getOrThrow
import com.zepben.ewb.services.variant.VariantService
import java.sql.ResultSet
import kotlin.collections.get

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

    private fun readChangeSetMember(
        service: VariantService,
        changeSetMember: ChangeSetMember,
        table: TableChangeSetMembers,
        resultSet: ResultSet,
        setIdentifier: (String) -> String
    ): Boolean {
        changeSetMember.apply {
            val changeSetMRID = setIdentifier(resultSet.getString(table.CHANGE_SET_MRID.queryIndex))
            setChangeSet(service.getOrThrow(changeSetMRID,setIdentifier("$changeSetMRID ObjectCreation")) as ChangeSet)
            targetObjectMRID = resultSet.getString(table.TARGET_OBJECT_MRID.queryIndex)
        }
        return true
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
        ObjectCreation().also {
            readChangeSetMember(service, it, table, resultSet, setIdentifier)
        }
        return true
    }

    fun read(service: VariantService, table: TableObjectDeletions, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        ObjectDeletion().also {
            readChangeSetMember(service, it, table, resultSet, setIdentifier)
        }
        return true
    }

    fun read(service: VariantService, table: TableObjectModifications, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        ObjectModification().also {
            readChangeSetMember(service, it, table, resultSet, setIdentifier)
            resultSet.getNullableString(table.OBJECT_REVERSE_MODIFICATION_TARGET_OBJECT_MRID.queryIndex)?.let { ormMRID ->
                it.setObjectReverseModification(service.getOrThrow(ormMRID, "ObjectModification reverseObjectModification targetObject $ormMRID association"))
            }
        }
        return true
    }

}
