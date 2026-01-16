/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim

import com.zepben.ewb.cim.iec61968.common.Document
import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61968.common.OrganisationRole
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.Name
import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.ewb.database.sql.cim.tables.iec61968.common.TableDocuments
import com.zepben.ewb.database.sql.cim.tables.iec61968.common.TableOrganisationRoles
import com.zepben.ewb.database.sql.cim.tables.iec61968.common.TableOrganisations
import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableNameTypes
import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableNames
import com.zepben.ewb.database.sql.common.DuplicateMRIDException
import com.zepben.ewb.database.sql.common.DuplicateNameTypeException
import com.zepben.ewb.database.sql.extensions.getInstant
import com.zepben.ewb.database.sql.extensions.getNullableInt
import com.zepben.ewb.database.sql.extensions.getNullableString
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.exceptions.UnsupportedIdentifiedObjectException
import com.zepben.ewb.services.common.extensions.ensureGet
import com.zepben.ewb.services.common.extensions.getNameTypeOrThrow
import com.zepben.ewb.services.common.extensions.getOrThrow
import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException

/**
 * A base class for reading CIM objects from a database.
 *
 * @property logger The [Logger] to use for this reader.
 */
internal abstract class CimReader<TService : BaseService> {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    // ###################
    // # IEC61968 Common #
    // ###################

    /**
     * Populate the [Document] fields from [TableDocuments].
     *
     * @param document The [Document] instance to populate.
     * @param table The database table to read the [Document] fields from.
     * @param resultSet The record in the database table containing the fields for this [Document].
     *
     * @return true if the [Document] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    protected fun readDocument(document: Document, table: TableDocuments, resultSet: ResultSet): Boolean {
        document.apply {
            title = resultSet.getNullableString(table.TITLE.queryIndex)
            createdDateTime = resultSet.getInstant(table.CREATED_DATE_TIME.queryIndex)
            authorName = resultSet.getNullableString(table.AUTHOR_NAME.queryIndex)
            type = resultSet.getNullableString(table.TYPE.queryIndex)
            status = resultSet.getNullableString(table.STATUS.queryIndex)
            comment = resultSet.getNullableString(table.COMMENT.queryIndex)
        }

        return readIdentifiedObject(document, table, resultSet)
    }

    /**
     * Create an [Organisation] and populate its fields from [TableOrganisations].
     *
     * @param service The [TService] used to store any items read from the database.
     * @param table The database table to read the [Organisation] fields from.
     * @param resultSet The record in the database table containing the fields for this [Organisation].
     * @param setIdentifier A callback to register the mRID of this [Organisation] for logging purposes.
     *
     * @return true if the [Organisation] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: TService, table: TableOrganisations, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val organisation = Organisation(setIdentifier(resultSet.getString(table.MRID.queryIndex)))

        return readIdentifiedObject(organisation, table, resultSet) && service.addOrThrow(organisation)
    }

    /**
     * Populate the [OrganisationRole] fields from [TableOrganisationRoles].
     *
     * @param service The [TService] used to store any items read from the database.
     * @param organisationRole The [OrganisationRole] instance to populate.
     * @param table The database table to read the [OrganisationRole] fields from.
     * @param resultSet The record in the database table containing the fields for this [OrganisationRole].
     *
     * @return true if the [OrganisationRole] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    protected fun readOrganisationRole(service: TService, organisationRole: OrganisationRole, table: TableOrganisationRoles, resultSet: ResultSet): Boolean {
        organisationRole.apply {
            organisation = service.ensureGet(
                resultSet.getNullableString(table.ORGANISATION_MRID.queryIndex),
                typeNameAndMRID()
            )
        }

        return readIdentifiedObject(organisationRole, table, resultSet)
    }

    // ######################
    // # IEC61970 Base Core #
    // ######################

    /**
     * Populate the [IdentifiedObject] fields from [TableIdentifiedObjects].
     *
     * @param identifiedObject The [IdentifiedObject] instance to populate.
     * @param table The database table to read the [IdentifiedObject] fields from.
     * @param resultSet The record in the database table containing the fields for this [IdentifiedObject].
     *
     * @return true if the [IdentifiedObject] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    protected fun readIdentifiedObject(identifiedObject: IdentifiedObject, table: TableIdentifiedObjects, resultSet: ResultSet): Boolean {
        identifiedObject.apply {
            name = resultSet.getNullableString(table.NAME.queryIndex)
            description = resultSet.getNullableString(table.DESCRIPTION.queryIndex)
            numDiagramObjects = resultSet.getNullableInt(table.NUM_DIAGRAM_OBJECTS.queryIndex)
        }

        return true
    }

    /**
     * Create a [Name] and populate its fields from [TableNames].
     *
     * @param service The [TService] used to store any items read from the database.
     * @param table The database table to read the [Name] fields from.
     * @param resultSet The record in the database table containing the fields for this [Name].
     * @param setLastName A callback to register the name of this [Name] for logging purposes.
     *
     * @return true if the [Name] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: TService, table: TableNames, resultSet: ResultSet, setLastName: (String) -> String): Boolean {
        val nameTypeName = resultSet.getString(table.NAME_TYPE_NAME.queryIndex)
        val nameName = resultSet.getString(table.NAME.queryIndex)
        setLastName("$nameTypeName:$nameName")

        val nameType = service.getNameTypeOrThrow(nameTypeName)
        service.getOrThrow<IdentifiedObject>(resultSet.getString(table.IDENTIFIED_OBJECT_MRID.queryIndex), "Name $nameName [$nameTypeName]")
            .addName(nameType, nameName)

        return true
    }

    /**
     * Create a [NameType] and populate its fields from [TableNameTypes].
     *
     * @param service The [TService] used to store any items read from the database.
     * @param table The database table to read the [NameType] fields from.
     * @param resultSet The record in the database table containing the fields for this [NameType].
     * @param setLastNameType A callback to register the name of this [NameType] for logging purposes.
     *
     * @return true if the [NameType] was successfully read from the database and added to the service.
     * @throws SQLException For any errors encountered reading from the database.
     */
    @Throws(SQLException::class)
    fun read(service: TService, table: TableNameTypes, resultSet: ResultSet, setLastNameType: (String) -> String): Boolean {
        val nameType = NameType(setLastNameType(resultSet.getString(table.NAME.queryIndex))).apply {
            description = resultSet.getString(table.DESCRIPTION.queryIndex)
        }

        return service.addOrThrow(nameType)
    }

    // #############
    // # End Model #
    // #############

    /**
     * Try and add the [identifiedObject] to the [BaseService], and throw an [Exception] if unsuccessful.
     *
     * @receiver The [BaseService] to search.
     * @param identifiedObject The [IdentifiedObject] to add to the [BaseService].
     *
     * @return true in all instances, otherwise it throws.
     * @throws DuplicateMRIDException If the [IdentifiedObject.mRID] has already been used.
     * @throws UnsupportedIdentifiedObjectException If the [IdentifiedObject] is not supported by the [BaseService]. This is an indication of an internal coding
     *   issue, rather than a problem with the data being read, and in a correctly configured system will never occur.
     */
    @Throws(DuplicateMRIDException::class, UnsupportedIdentifiedObjectException::class)
    protected fun BaseService.addOrThrow(identifiedObject: IdentifiedObject): Boolean {
        return if (tryAdd(identifiedObject)) {
            true
        } else {
            val duplicate = get<IdentifiedObject>(identifiedObject.mRID)
            throw DuplicateMRIDException(
                "Failed to read ${identifiedObject.typeNameAndMRID()}. Unable to add to service '$name': duplicate MRID (${duplicate?.typeNameAndMRID()})"
            )
        }
    }

    private fun BaseService.addOrThrow(nameType: NameType): Boolean {
        return if (addNameType(nameType))
            true
        else
            throw DuplicateNameTypeException("Failed to read NameType ${nameType.name}. Unable to add to service '$name': duplicate NameType")
    }

}
