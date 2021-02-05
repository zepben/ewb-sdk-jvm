/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.writers

import com.zepben.evolve.cim.iec61968.common.Document
import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.common.OrganisationRole
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.core.Name
import com.zepben.evolve.cim.iec61970.base.core.NameType
import com.zepben.evolve.database.sqlite.DatabaseTables
import com.zepben.evolve.database.sqlite.extensions.setInstant
import com.zepben.evolve.database.sqlite.extensions.setNullableString
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableDocuments
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableOrganisationRoles
import com.zepben.evolve.database.sqlite.tables.iec61968.common.TableOrganisations
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableNameTypes
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableNames
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.PreparedStatement
import java.sql.SQLException


abstract class BaseCIMWriter(protected val databaseTables: DatabaseTables) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val failedIds: MutableSet<String> = mutableSetOf()

    /************ IEC61968 COMMON ************/
    @Throws(SQLException::class)
    protected fun saveDocument(table: TableDocuments, insert: PreparedStatement, document: Document, description: String): Boolean {
        insert.setNullableString(table.TITLE.queryIndex, document.title)
        insert.setInstant(table.CREATED_DATE_TIME.queryIndex, document.createdDateTime)
        insert.setNullableString(table.AUTHOR_NAME.queryIndex, document.authorName)
        insert.setNullableString(table.TYPE.queryIndex, document.type)
        insert.setNullableString(table.STATUS.queryIndex, document.status)
        insert.setNullableString(table.COMMENT.queryIndex, document.comment)

        return saveIdentifiedObject(table, insert, document, description)
    }

    fun save(organisation: Organisation): Boolean {
        val table = databaseTables.getTable(TableOrganisations::class.java)
        val insert = databaseTables.getInsert(TableOrganisations::class.java)

        return saveIdentifiedObject(table, insert, organisation, "organisation")
    }

    fun save(nameType: NameType): Boolean {
        val table = databaseTables.getTable(TableNameTypes::class.java)
        val insert = databaseTables.getInsert(TableNameTypes::class.java)

        return saveNameType(table, insert, nameType)
    }

    /**
     * Writes the [Name] object to the table. Name with no associated identified objects will not be written.
     *
     * @param name The name object to be written to the table
     *
     * @return true if the write was successful
     */
    fun save(name: Name): Boolean {
        val table = databaseTables.getTable(TableNames::class.java)
        val insert = databaseTables.getInsert(TableNames::class.java)

        return saveName(table, insert, name)
    }

    private fun saveNameType(table: TableNameTypes, insert: PreparedStatement, nameType: NameType): Boolean {
        insert.setString(table.NAME.queryIndex, nameType.name)
        insert.setNullableString(table.DESCRIPTION.queryIndex, nameType.description)

        return tryExecuteSingleUpdate(insert, nameType.name, "name type")
    }

    private fun saveName(table: TableNames, insert: PreparedStatement, name: Name): Boolean {
        var status = true

        insert.setString(table.NAME.queryIndex, name.name)
        insert.setString(table.NAME_TYPE_NAME.queryIndex, name.type.name)
        insert.setString(table.IDENTIFIED_OBJECT_MRID.queryIndex, name.identifiedObject.mRID)
        status = status and tryExecuteSingleUpdate(insert, name.name, "name")

        return status
    }

    @Throws(SQLException::class)
    protected fun saveOrganisationRole(
        table: TableOrganisationRoles,
        insert: PreparedStatement,
        organisationRole: OrganisationRole,
        description: String
    ): Boolean {
        insert.setNullableString(table.ORGANISATION_MRID.queryIndex, organisationRole.organisation?.mRID)

        return saveIdentifiedObject(table, insert, organisationRole, description)
    }

    /************ IEC61970 CORE ************/
    @Throws(SQLException::class)
    protected fun saveIdentifiedObject(
        table: TableIdentifiedObjects,
        insert: PreparedStatement,
        identifiedObject: IdentifiedObject,
        description: String
    ): Boolean {
        insert.setString(table.MRID.queryIndex, identifiedObject.mRID)
        insert.setString(table.NAME.queryIndex, identifiedObject.name)
        insert.setString(table.DESCRIPTION.queryIndex, identifiedObject.description)
        insert.setInt(table.NUM_DIAGRAM_OBJECTS.queryIndex, identifiedObject.numDiagramObjects)

        return tryExecuteSingleUpdate(insert, identifiedObject.mRID, description)
    }

    /************ HELPERS ************/
    @Throws(SQLException::class)
    protected fun tryExecuteSingleUpdate(query: PreparedStatement, id: String, description: String): Boolean =
        WriteValidator.tryExecuteSingleUpdate(query) {
            failedIds.add(id)
            WriteValidator.logFailure(logger, query, description)
        }

}
