/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim

import com.zepben.evolve.cim.iec61968.common.Document
import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.common.OrganisationRole
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.core.Name
import com.zepben.evolve.cim.iec61970.base.core.NameType
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.common.TableDocuments
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.common.TableOrganisationRoles
import com.zepben.evolve.database.sqlite.cim.tables.iec61968.common.TableOrganisations
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.core.TableNameTypes
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.core.TableNames
import com.zepben.evolve.database.sql.BaseEntryWriter
import com.zepben.evolve.database.sql.extensions.setInstant
import com.zepben.evolve.database.sql.extensions.setNullableString
import java.sql.PreparedStatement
import java.sql.SQLException

/**
 * A base class for writing CIM objects to a database.
 *
 * @property databaseTables The tables that are available in the database.
 */
abstract class CimWriter(
    protected open val databaseTables: CimDatabaseTables
) : BaseEntryWriter() {

    // ###################
    // # IEC61968 Common #
    // ###################

    /**
     * Write the [Document] fields to [TableDocuments].
     *
     * @param table The database table to write the [Document] fields to.
     * @param insert The [PreparedStatement] to bind the field values to.
     * @param document The [Document] instance to write to the database.
     * @param description A readable version of the type of object being written for logging purposes.
     *
     * @return true if the [Document] was successfully written to the database, otherwise false.
     * @throws SQLException For any errors encountered writing to the database.
     */
    @Throws(SQLException::class)
    protected fun writeDocument(table: TableDocuments, insert: PreparedStatement, document: Document, description: String): Boolean {
        insert.setNullableString(table.TITLE.queryIndex, document.title)
        insert.setInstant(table.CREATED_DATE_TIME.queryIndex, document.createdDateTime)
        insert.setNullableString(table.AUTHOR_NAME.queryIndex, document.authorName)
        insert.setNullableString(table.TYPE.queryIndex, document.type)
        insert.setNullableString(table.STATUS.queryIndex, document.status)
        insert.setNullableString(table.COMMENT.queryIndex, document.comment)

        return writeIdentifiedObject(table, insert, document, description)
    }

    /**
     * Write the [Organisation] fields to [TableOrganisations].
     *
     * @param organisation The [Organisation] instance to write to the database.
     *
     * @return true if the [Organisation] was successfully written to the database, otherwise false.
     * @throws SQLException For any errors encountered writing to the database.
     */
    @Throws(SQLException::class)
    fun write(organisation: Organisation): Boolean {
        val table = databaseTables.getTable<TableOrganisations>()
        val insert = databaseTables.getInsert<TableOrganisations>()

        return writeIdentifiedObject(table, insert, organisation, "organisation")
    }

    /**
     * Write the [NameType] fields to [TableNameTypes].
     *
     * @param nameType The [NameType] instance to write to the database.
     *
     * @return true if the [NameType] was successfully written to the database, otherwise false.
     * @throws SQLException For any errors encountered writing to the database.
     */
    @Throws(SQLException::class)
    fun write(nameType: NameType): Boolean {
        val table = databaseTables.getTable<TableNameTypes>()
        val insert = databaseTables.getInsert<TableNameTypes>()

        return writeNameType(table, insert, nameType)
    }

    /**
     * Write the [Name] fields to [TableNames].
     *
     * @param name The [Name] instance to write to the database.
     *
     * @return true if the [Name] was successfully written to the database, otherwise false.
     * @throws SQLException For any errors encountered writing to the database.
     */
    @Throws(SQLException::class)
    fun write(name: Name): Boolean {
        val table = databaseTables.getTable<TableNames>()
        val insert = databaseTables.getInsert<TableNames>()

        return writeName(table, insert, name)
    }

    /**
     * Write the [OrganisationRole] fields to [TableOrganisationRoles].
     *
     * @param table The database table to write the [OrganisationRole] fields to.
     * @param insert The [PreparedStatement] to bind the field values to.
     * @param organisationRole The [OrganisationRole] instance to write to the database.
     * @param description A readable version of the type of object being written for logging purposes.
     *
     * @return true if the [OrganisationRole] was successfully written to the database, otherwise false.
     * @throws SQLException For any errors encountered writing to the database.
     */
    @Throws(SQLException::class)
    protected fun writeOrganisationRole(
        table: TableOrganisationRoles,
        insert: PreparedStatement,
        organisationRole: OrganisationRole,
        description: String
    ): Boolean {
        insert.setNullableString(table.ORGANISATION_MRID.queryIndex, organisationRole.organisation?.mRID)

        return writeIdentifiedObject(table, insert, organisationRole, description)
    }

    // ######################
    // # IEC61970 Base Core #
    // ######################

    /**
     * Write the [IdentifiedObject] fields to [TableIdentifiedObjects].
     *
     * @param table The database table to write the [IdentifiedObject] fields to.
     * @param insert The [PreparedStatement] to bind the field values to.
     * @param identifiedObject The [IdentifiedObject] instance to write to the database.
     * @param description A readable version of the type of object being written for logging purposes.
     *
     * @return true if the [IdentifiedObject] was successfully written to the database, otherwise false.
     * @throws SQLException For any errors encountered writing to the database.
     */
    @Throws(SQLException::class)
    protected fun writeIdentifiedObject(
        table: TableIdentifiedObjects,
        insert: PreparedStatement,
        identifiedObject: IdentifiedObject,
        description: String
    ): Boolean {
        insert.setString(table.MRID.queryIndex, identifiedObject.mRID)
        insert.setString(table.NAME.queryIndex, identifiedObject.name)
        insert.setString(table.DESCRIPTION.queryIndex, identifiedObject.description)
        insert.setInt(table.NUM_DIAGRAM_OBJECTS.queryIndex, identifiedObject.numDiagramObjects)

        return insert.tryExecuteSingleUpdate(description)
    }

    @Throws(SQLException::class)
    private fun writeNameType(table: TableNameTypes, insert: PreparedStatement, nameType: NameType): Boolean {
        insert.setString(table.NAME.queryIndex, nameType.name)
        insert.setNullableString(table.DESCRIPTION.queryIndex, nameType.description)

        return insert.tryExecuteSingleUpdate("name type")
    }

    @Throws(SQLException::class)
    private fun writeName(table: TableNames, insert: PreparedStatement, name: Name): Boolean {
        insert.setString(table.NAME.queryIndex, name.name)
        insert.setString(table.NAME_TYPE_NAME.queryIndex, name.type.name)
        insert.setString(table.IDENTIFIED_OBJECT_MRID.queryIndex, name.identifiedObject.mRID)

        return insert.tryExecuteSingleUpdate("name")
    }

}
