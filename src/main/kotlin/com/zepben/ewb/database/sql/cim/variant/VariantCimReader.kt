/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.variant

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectComponent
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.DependencyKind
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.database.sql.cim.CimReader
import com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.infpart303.networkmodelprojects.TableNetworkModelProjectComponents
import com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.infpart303.networkmodelprojects.TableNetworkModelProjects
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableAnnotatedProjectDependencies
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableNetworkModelProjectStageEquipmentContainers
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableNetworkModelProjectStages
import com.zepben.ewb.database.sql.extensions.getNullableInt
import com.zepben.ewb.database.sql.extensions.getNullableString
import com.zepben.ewb.services.common.extensions.ensureGet
import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.services.variant.VariantService
import java.sql.ResultSet

/**
 * A class for reading the [VariantService] tables from the database.
 */
internal class VariantCimReader : CimReader<VariantService>(), AutoCloseable{

    override fun close() {
    }

    // #######################################################
    // # Extensions IEC61970 InfPart303 NetworkModelProjects #
    // #######################################################

    // TODO: docs
    fun read(service: VariantService, table: TableNetworkModelProjects, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val nmpMRID = setIdentifier(resultSet.getString(table.MRID.queryIndex))

        return service.addOrThrow(
            NetworkModelProject(nmpMRID).apply {
                externalStatus = resultSet.getString(table.EXTERNAL_STATUS.queryIndex)
                forecastCommissionDate = resultSet.getTimestamp(table.FORECAST_COMMISSION_DATE.queryIndex)?.toInstant()
                externalDriver = resultSet.getString(table.EXTERNAL_DRIVER.queryIndex)
            }.also {
                readNetworkModelProjectComponent(service, it, table, resultSet)
            }
        )
    }

    private fun readNetworkModelProjectComponent(service: VariantService, networkModelProjectComponent: NetworkModelProjectComponent, table: TableNetworkModelProjectComponents, resultSet: ResultSet): Boolean {
        networkModelProjectComponent.apply {
            created = resultSet.getTimestamp(table.CREATED.queryIndex)?.toInstant()
            updated = resultSet.getTimestamp(table.UPDATED.queryIndex)?.toInstant()
            closed = resultSet.getTimestamp(table.CLOSED.queryIndex)?.toInstant()
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

    // TODO: docs
    fun read(service: VariantService, table: TableAnnotatedProjectDependencies, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val apdMRID = setIdentifier(resultSet.getString(table.MRID.queryIndex))
        return service.addOrThrow(
            AnnotatedProjectDependency(
                apdMRID,
            ).apply {
                dependencyType = DependencyKind.valueOf(resultSet.getString(table.DEPENDENCY_TYPE.queryIndex))
                addDependencyDependentOnStage(service.getOrThrow(resultSet.getString(table.DEPENDENCY_DEPENDENT_ON_STAGE_MRID.queryIndex), "annotated project dependency $apdMRID dependency dependent on stage"))
                addDependencyDependingStage(service.getOrThrow(resultSet.getString(table.DEPENDENCY_DEPENDING_STAGE_MRID.queryIndex),"annotated project dependency $apdMRID dependency depending stage"))
            }.also {
                readIdentifiedObject(it, table, resultSet)
            }
        )
    }

    // TODO: docs
    fun read(service: VariantService, table: TableNetworkModelProjectStages, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val nmpsMRID = setIdentifier(resultSet.getString(table.MRID.queryIndex))
        return service.addOrThrow(
            NetworkModelProjectStage(nmpsMRID).apply {
                plannedCommissionedDate = resultSet.getTimestamp(table.PLANNED_COMMISSION_DATE.queryIndex)?.toInstant()
                commissionedDate = resultSet.getTimestamp(table.COMMISSIONED_DATE.queryIndex)?.toInstant()
                confidenceLevel = resultSet.getNullableInt(table.CONFIDENCE_LEVEL.queryIndex)
                baseModelVersion = resultSet.getNullableString(table.BASE_MODEL_VERSION.queryIndex)
                lastConflictCheckedAt = resultSet.getTimestamp(table.LAST_CONFLICT_CHECKED_AT.queryIndex)?.toInstant()
                userComments = resultSet.getNullableString(table.USER_COMMENTS.queryIndex)
                resultSet.getNullableString(table.CHANGE_SET_MRID.queryIndex)?.let {
                    setChangeSetMRID(it)
                }
            }
        )
    }

    // TODO: docs
    fun read(service: VariantService, table: TableNetworkModelProjectStageEquipmentContainers, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val nmpsMRID = setIdentifier(resultSet.getString(table.NETWORK_MODEL_PROJECT_MRID.queryIndex))
        service.ensureGet<NetworkModelProjectStage>(nmpsMRID, "$nmpsMRID equipment container")?.apply {
            addEquipmentContainer(service.getOrThrow(resultSet.getString(table.EQUIPMENT_CONTAINER_MRID.queryIndex), "equipment container"))
        }

        return true
    }

}
