/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.postgres.cim.networkmodelproject

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.DependencyKind
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.database.postgres.cim.tables.extensions.iec61970.infpart303.networkmodelprojects.TableNetworkModelProjectComponents
import com.zepben.ewb.database.postgres.cim.tables.extensions.iec61970.infpart303.networkmodelprojects.TableNetworkModelProjects
import com.zepben.ewb.database.postgres.cim.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.ewb.database.postgres.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableAnnotatedProjectDependencies
import com.zepben.ewb.database.postgres.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableNetworkModelProjectStageEquipmentContainers
import com.zepben.ewb.database.postgres.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableNetworkModelProjectStages
import com.zepben.ewb.database.sql.extensions.getInstant
import com.zepben.ewb.database.sql.extensions.getNullableInt
import com.zepben.ewb.database.sql.extensions.getNullableString
import com.zepben.ewb.database.sqlite.common.DuplicateMRIDException
import com.zepben.ewb.database.sqlite.common.DuplicateNameTypeException
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.exceptions.UnsupportedIdentifiedObjectException
import com.zepben.ewb.services.common.extensions.ensureGet
import com.zepben.ewb.services.common.extensions.getOrThrow
import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.services.networkmodelproject.NetworkModelProjectService
import java.sql.ResultSet
import java.sql.SQLException

internal class NetworkModelProjectCimReader(
    val databaseTables: NetworkModelProjectDatabaseTables
)  {

    // #######################################################
    // # Extensions IEC61970 InfPart303 NetworkModelProjects #
    // #######################################################

    fun read(service: NetworkModelProjectService, table: TableNetworkModelProjects, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val nmpMRID = setIdentifier(resultSet.getString(table.MRID.queryIndex))

        return service.addOrThrow(
            NetworkModelProject(nmpMRID).apply {
                externalStatus = resultSet.getString(table.EXTERNAL_STATUS.queryIndex)
                forecastCommissionDate = resultSet.getInstant(table.FORECAST_COMMISSION_DATE.queryIndex)
                externalDriver = resultSet.getString(table.EXTERNAL_DRIVER.queryIndex)
            }.also {
                readNetworkModelProjectComponent(service, it, table, resultSet)
            }
        )
    }

    fun readNetworkModelProjectComponent(service: NetworkModelProjectService, networkModelProjectComponent: NetworkModelProjectComponent, table: TableNetworkModelProjectComponents, resultSet: ResultSet): Boolean {
        networkModelProjectComponent.apply {
            created = resultSet.getInstant(table.CREATED.queryIndex)
            updated = resultSet.getInstant(table.UPDATED.queryIndex)
            closed = resultSet.getInstant(table.CLOSED.queryIndex)
        }.also { nmpc ->
            resultSet.getString(table.PARENT_MRID.queryIndex)?.let {
                service.ensureGet<NetworkModelProject>(it, nmpc.typeNameAndMRID())?.apply {
                    addChild(nmpc)
                }
            }
            return readIdentifiedObject(nmpc, table, resultSet)
        }
    }

    // ############################################
    // # IEC61970 InfPart303 NetworkModelProjects #
    // ############################################

    fun read(service: NetworkModelProjectService, table: TableAnnotatedProjectDependencies, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val apdMRID = setIdentifier(resultSet.getString(table.MRID.queryIndex))
        return service.addOrThrow(
            AnnotatedProjectDependency(
                apdMRID,
                DependencyKind.valueOf(resultSet.getString(table.DEPENDENCY_TYPE.queryIndex)),
                service.getOrThrow(resultSet.getString(table.DEPENDENCY_DEPENDENT_ON_STAGE_MRID.queryIndex), "annotated project dependency $apdMRID dependency dependent on stage"),
                service.getOrThrow(resultSet.getString(table.DEPENDENCY_DEPENDING_STAGE_MRID.queryIndex),"annotated project dependency $apdMRID dependency depending stage"),
            ).also {
                readIdentifiedObject(it, table, resultSet)
            }
        )
    }

    fun read(service: NetworkModelProjectService, table: TableNetworkModelProjectStages, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val nmpsMRID = setIdentifier(resultSet.getString(table.MRID.queryIndex))
        return service.addOrThrow(
            NetworkModelProjectStage(nmpsMRID).apply {
                plannedCommissionedDate = resultSet.getInstant(table.PLANNED_COMMISSION_DATE.queryIndex)
                commissionedDate = resultSet.getInstant(table.COMMISSIONED_DATE.queryIndex)
                confidenceLevel = resultSet.getNullableInt(table.CONFIDENCE_LEVEL.queryIndex)
                baseModelVersion = resultSet.getNullableString(table.BASE_MODEL_VERSION.queryIndex)
                lastConflictCheckedAt = resultSet.getInstant(table.LAST_CONFLICT_CHECKED_AT.queryIndex)
                userComments = resultSet.getNullableString(table.USER_COMMENTS.queryIndex)
                resultSet.getNullableString(table.CHANGE_SET_MRID.queryIndex)?.let {
                    // service.getChangeSet(it)
                    // TODO: cross service lookup... doesnt feel like it should be in here.
                }
            }
        )
    }

    fun read(service: NetworkModelProjectService, table: TableNetworkModelProjectStageEquipmentContainers, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val nmpsMRID = setIdentifier(resultSet.getString(table.NETWORK_MODEL_PROJECT_MRID.queryIndex))
        service.ensureGet<NetworkModelProjectStage>(nmpsMRID, "$nmpsMRID equipment container")?.apply {
            addEquipmentContainer(service.getOrThrow(resultSet.getString(table.EQUIPMENT_CONTAINER_MRID.queryIndex), "equipment container"))
        }

        return true
    }

    // FIXME: Copypasta from CimReader, propose moving generic versions to BaseReader, and tightening typing higher up.
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
        }

        return true
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