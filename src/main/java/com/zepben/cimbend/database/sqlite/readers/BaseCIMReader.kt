/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.database.sqlite.readers

import com.zepben.cimbend.cim.iec61968.common.Document
import com.zepben.cimbend.cim.iec61968.common.Organisation
import com.zepben.cimbend.cim.iec61968.common.OrganisationRole
import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.common.BaseService
import com.zepben.cimbend.common.extensions.emptyIfNull
import com.zepben.cimbend.common.extensions.ensureGet
import com.zepben.cimbend.common.extensions.internEmpty
import com.zepben.cimbend.common.extensions.typeNameAndMRID
import com.zepben.cimbend.database.DuplicateMRIDException
import com.zepben.cimbend.database.sqlite.extensions.getInstant
import com.zepben.cimbend.database.sqlite.extensions.getNullableString
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.TableDocuments
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.TableOrganisationRoles
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.TableOrganisations
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException

abstract class BaseCIMReader(private val baseService: BaseService) {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    /************ IEC61968 COMMON ************/
    @Throws(SQLException::class)
    protected fun loadDocument(document: Document, table: TableDocuments, resultSet: ResultSet): Boolean {
        document.apply {
            title = resultSet.getString(table.TITLE.queryIndex).emptyIfNull().internEmpty()
            createdDateTime = resultSet.getInstant(table.CREATED_DATE_TIME.queryIndex)
            authorName = resultSet.getString(table.AUTHOR_NAME.queryIndex).emptyIfNull().internEmpty()
            type = resultSet.getString(table.TYPE.queryIndex).emptyIfNull().internEmpty()
            status = resultSet.getString(table.STATUS.queryIndex).emptyIfNull().internEmpty()
            comment = resultSet.getString(table.COMMENT.queryIndex).emptyIfNull().internEmpty()
        }

        return loadIdentifiedObject(document, table, resultSet)
    }

    fun load(table: TableOrganisations, resultSet: ResultSet, setLastMRID: (String) -> String): Boolean {
        val organisation = Organisation(setLastMRID(resultSet.getString(table.MRID.queryIndex)))

        return loadIdentifiedObject(organisation, table, resultSet) && baseService.addOrThrow(organisation)
    }

    @Throws(SQLException::class)
    protected fun loadOrganisationRole(
        organisationRole: OrganisationRole,
        table: TableOrganisationRoles,
        resultSet: ResultSet
    ): Boolean {
        organisationRole.apply {
            organisation = baseService.ensureGet(
                resultSet.getNullableString(table.ORGANISATION_MRID.queryIndex),
                typeNameAndMRID()
            )
        }

        return loadIdentifiedObject(organisationRole, table, resultSet)
    }

    /************ IEC61970 CORE ************/
    @Throws(SQLException::class)
    protected fun loadIdentifiedObject(
        identifiedObject: IdentifiedObject,
        table: TableIdentifiedObjects,
        resultSet: ResultSet
    ): Boolean {
        identifiedObject.apply {
            name = resultSet.getString(table.NAME.queryIndex).emptyIfNull().internEmpty()
            description = resultSet.getString(table.DESCRIPTION.queryIndex).emptyIfNull().internEmpty()
            numDiagramObjects = resultSet.getInt(table.NUM_DIAGRAM_OBJECTS.queryIndex)
        }

        return true
    }

    protected fun BaseService.addOrThrow(identifiedObject: IdentifiedObject): Boolean {
        return if (tryAdd(identifiedObject)) {
            true
        } else {
            val duplicate = get<IdentifiedObject>(identifiedObject.mRID)
            throw DuplicateMRIDException(
                "Failed to load ${identifiedObject.typeNameAndMRID()}. " +
                        "Unable to add to service '$name': duplicate MRID (${duplicate?.typeNameAndMRID()})"
            )
        }
    }

}
