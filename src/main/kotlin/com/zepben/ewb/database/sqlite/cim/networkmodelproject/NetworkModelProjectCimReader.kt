/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.networkmodelproject

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.wires.AcLineSegment
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSetMember
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.DataSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectCreation
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectDeletion
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectModification
import com.zepben.ewb.database.sql.extensions.getNullableString
import com.zepben.ewb.database.sqlite.cim.CimReader
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableChangeSetMembers
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableChangeSets
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableDataSets
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectCreations
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectDeletions
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectModifications
import com.zepben.ewb.database.sqlite.common.MRIDLookupException
import com.zepben.ewb.services.common.extensions.getOrThrow
import com.zepben.ewb.services.networkmodelproject.NetworkModelProjectService
import java.sql.ResultSet
import kotlin.collections.get

internal class NetworkModelProjectCimReader : CimReader<NetworkModelProjectService>(), AutoCloseable{

    private val changeSetById = mutableMapOf<String, ChangeSet>()

    override fun close() {
        changeSetById.clear()
    }

    // ###################################
    // # IEC61970 Part303 GenericDataSet #
    // ###################################

    fun read(service: NetworkModelProjectService, table: TableChangeSets, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val changeSetMRID = setIdentifier(resultSet.getString(table.MRID.queryIndex))
        val changeSet = ChangeSet(changeSetMRID).also { changeSetById[it.mRID] = it }

        return readDataSet(changeSet, table, resultSet)
    }

    private fun <T: ChangeSetMember>readChangeSetMember(
        service: NetworkModelProjectService,
        changeSetMemberClass: (ChangeSet, IdentifiedObject) -> T,
        table: TableChangeSetMembers,
        resultSet: ResultSet,
        setIdentifier: (String) -> String
    ): T {
        val changeSetMRID = setIdentifier(resultSet.getString(table.CHANGE_SET_MRID.queryIndex))
        changeSetById.getOrThrow(changeSetMRID, setIdentifier("$changeSetMRID ObjectCreation")).also {
            return changeSetMemberClass(
                it, service.getOrThrow(
                    resultSet.getString(table.TARGET_OBJECT_MRID.queryIndex),
                    "${changeSetMemberClass.javaClass.simpleName} to IdentifiedObject association"
                )
            )
        }
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

    fun read(service: NetworkModelProjectService, table: TableObjectCreations, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        readChangeSetMember(service, ::ObjectCreation, table, resultSet, setIdentifier)
        return true
    }

    fun read(service: NetworkModelProjectService, table: TableObjectDeletions, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        readChangeSetMember(service, ::ObjectDeletion, table, resultSet, setIdentifier)
        return true
    }

    fun read(service: NetworkModelProjectService, table: TableObjectModifications, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        readChangeSetMember(service, ::ObjectModification, table, resultSet, setIdentifier).apply {
            resultSet.getNullableString(table.OBJECT_REVERSE_MODIFICATION_TARGET_OBJECT_MRID.queryIndex)?.let {
                setObjectReverseModification(service.getOrThrow(it, "ObjectModification reverseObjectModification targetObject $it association"))
            }
        }
        return true
    }

    @Throws(MRIDLookupException::class)
    private fun Map<String, ChangeSet>.getOrThrow(mRID: String?, typeNameAndMRID: String): ChangeSet {
        return get(mRID) ?: throw MRIDLookupException("Failed to find ChangeSet with mRID $mRID for $typeNameAndMRID")
    }

}