/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.database.sqlite.writers

import com.zepben.cimbend.cim.iec61968.common.Document
import com.zepben.cimbend.cim.iec61968.common.Organisation
import com.zepben.cimbend.cim.iec61968.common.OrganisationRole
import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.database.sqlite.DatabaseTables
import com.zepben.cimbend.database.sqlite.extensions.*
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.TableDocuments
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.TableOrganisationRoles
import com.zepben.cimbend.database.sqlite.tables.iec61968.common.TableOrganisations
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects
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
        insert.setNullableString(table.TITLE.queryIndex(), document.title)
        insert.setInstant(table.CREATED_DATE_TIME.queryIndex(), document.createdDateTime)
        insert.setNullableString(table.AUTHOR_NAME.queryIndex(), document.authorName)
        insert.setNullableString(table.TYPE.queryIndex(), document.type)
        insert.setNullableString(table.STATUS.queryIndex(), document.status)
        insert.setNullableString(table.COMMENT.queryIndex(), document.comment)

        return saveIdentifiedObject(table, insert, document, description)
    }

    fun save(organisation: Organisation): Boolean {
        val table = databaseTables.getTable(TableOrganisations::class.java)
        val insert = databaseTables.getInsert(TableOrganisations::class.java)

        return saveIdentifiedObject(table, insert, organisation, "organisation")
    }

    @Throws(SQLException::class)
    protected fun saveOrganisationRole(
        table: TableOrganisationRoles,
        insert: PreparedStatement,
        organisationRole: OrganisationRole,
        description: String
    ): Boolean {
        insert.setNullableString(table.ORGANISATION_MRID.queryIndex(), organisationRole.organisation?.mRID)

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
        insert.setString(table.MRID.queryIndex(), identifiedObject.mRID)
        insert.setString(table.NAME.queryIndex(), identifiedObject.name)
        insert.setString(table.DESCRIPTION.queryIndex(), identifiedObject.description)
        insert.setInt(table.NUM_DIAGRAM_OBJECTS.queryIndex(), identifiedObject.numDiagramObjects)

        return tryExecuteSingleUpdate(insert, identifiedObject.mRID, "Failed to save $description.")
    }

    /************ HELPERS ************/
    @Throws(SQLException::class)
    protected fun tryExecuteSingleUpdate(query: PreparedStatement, id: String, errorMessage: String): Boolean {
        if (query.executeSingleUpdate())
            return true

        failedIds.add(id)
        logger.warn(
            "$errorMessage\n" +
                "SQL: ${query.sql()}\n" +
                "Fields: ${query.parameters()}"
        )

        return false
    }
}
