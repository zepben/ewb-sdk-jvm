/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.cim.iec61968.common.Document
import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.common.OrganisationRole
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.core.NameType
import com.zepben.evolve.database.sqlite.extensions.getInstant
import com.zepben.evolve.database.sqlite.extensions.getNullableString
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableDocuments
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableOrganisationRoles
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableOrganisations
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableNameTypes
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableNames
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.common.extensions.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException

/**
 * A base class for reading CIM objects from a database.
 *
 * @param baseService The [BaseService] used to store any items read from the database.
 */
abstract class BaseCIMReader(
    private val baseService: BaseService
) {

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

    fun load(table: TableNameTypes, resultSet: ResultSet, setLastNameType: (String) -> String): Boolean {
        val nameType = NameType(setLastNameType(resultSet.getString(table.NAME.queryIndex))).apply {
            description = resultSet.getString(table.DESCRIPTION.queryIndex)
        }

        return baseService.addOrThrow(nameType)
    }

    fun load(table: TableNames, resultSet: ResultSet, setLastName: (String) -> String): Boolean {
        val nameTypeName = resultSet.getString(table.NAME_TYPE_NAME.queryIndex)
        val nameName = resultSet.getString(table.NAME.queryIndex)
        setLastName("$nameTypeName:$nameName")

        val nameType = baseService.getNameTypeOrThrow(nameTypeName)
        // Because each service type loads all the name types, but not all services hold all identified objects, there can
        // be records in the names table that only apply to certain services. We attempt to find the IdentifiedObject on this
        // service and add a name for it if it exists, but ignore if it doesn't. Note that this can potentially lead to there being
        // a name record that never gets used because that identified object doesn't exist in any service, and currently we
        // don't check or warn about that.
        baseService.get<IdentifiedObject>(resultSet.getString(table.IDENTIFIED_OBJECT_MRID.queryIndex))?.addName(nameType, nameName)

        return true
    }

    @Throws(SQLException::class)
    protected fun loadOrganisationRole(organisationRole: OrganisationRole, table: TableOrganisationRoles, resultSet: ResultSet): Boolean {
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
    protected fun loadIdentifiedObject(identifiedObject: IdentifiedObject, table: TableIdentifiedObjects, resultSet: ResultSet): Boolean {
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
                "Failed to load ${identifiedObject.typeNameAndMRID()}. Unable to add to service '$name': duplicate MRID (${duplicate?.typeNameAndMRID()})"
            )
        }
    }

    private fun BaseService.addOrThrow(nameType: NameType): Boolean {
        return if (addNameType(nameType))
            true
        else
            throw DuplicateNameTypeException("Failed to load NameType ${nameType.name}. Unable to add to service '$name': duplicate NameType")
    }

}
