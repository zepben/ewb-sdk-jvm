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
import com.zepben.ewb.database.sql.extensions.getInstant
import com.zepben.ewb.database.sql.extensions.getNullableInt
import com.zepben.ewb.database.sql.extensions.getNullableString
import com.zepben.ewb.services.common.Resolvers
import com.zepben.ewb.services.common.extensions.ensureGet
import com.zepben.ewb.services.common.extensions.getOrThrow
import com.zepben.ewb.services.variant.VariantService
import java.sql.ResultSet

/**
 * A class for reading the [VariantService] tables from the database.
 */
internal class VariantCimReader : CimReader<VariantService>(), AutoCloseable {

    override fun close() {}

    // #######################################################
    // # Extensions IEC61970 InfPart303 NetworkModelProjects #
    // #######################################################

    // TODO: docs
    fun read(service: VariantService, table: TableNetworkModelProjects, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val nmp = NetworkModelProject(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            externalStatus = resultSet.getNullableString(table.EXTERNAL_STATUS.queryIndex)
            forecastCommissionDate = resultSet.getInstant(table.FORECAST_COMMISSION_DATE.queryIndex)
            externalDriver = resultSet.getNullableString(table.EXTERNAL_DRIVER.queryIndex)
        }

        return readNetworkModelProjectComponent(service, nmp, table, resultSet) && service.addOrThrow(nmp)
    }

    private fun readNetworkModelProjectComponent(
        service: VariantService,
        networkModelProjectComponent: NetworkModelProjectComponent,
        table: TableNetworkModelProjectComponents,
        resultSet: ResultSet
    ): Boolean {
        networkModelProjectComponent.apply {
            created = resultSet.getInstant(table.CREATED.queryIndex)
            updated = resultSet.getInstant(table.UPDATED.queryIndex)
            closed = resultSet.getInstant(table.CLOSED.queryIndex)
            service.ensureGet<NetworkModelProject>(resultSet.getString(table.PARENT_MRID.queryIndex), typeNameAndMRID())?.also {
                parent = it
                it.addChild(this)
            }
            return readIdentifiedObject(this, table, resultSet)
        }
    }

    // ############################################
    // # IEC61970 InfPart303 NetworkModelProjects #
    // ############################################

    // TODO: docs
    fun read(service: VariantService, table: TableAnnotatedProjectDependencies, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val annotatedProjectDependency = AnnotatedProjectDependency(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            dependencyType = DependencyKind.valueOf(resultSet.getString(table.DEPENDENCY_TYPE.queryIndex))
            dependencyDependentOnStage = service.getOrThrow<NetworkModelProjectStage>(
                resultSet.getString(table.DEPENDENCY_DEPENDENT_ON_STAGE_MRID.queryIndex),
                "${typeNameAndMRID()} dependent on stage"
            )
            dependencyDependentOnStage!!.addDependentOnStage(this)
            dependencyDependingStage = service.getOrThrow<NetworkModelProjectStage>(
                resultSet.getString(table.DEPENDENCY_DEPENDING_STAGE_MRID.queryIndex),
                "${typeNameAndMRID()} depending stage"
            )
            dependencyDependingStage!!.addDependingStage(this)
        }

        return readIdentifiedObject(annotatedProjectDependency, table, resultSet) && service.addOrThrow(annotatedProjectDependency)
    }

    // TODO: docs
    fun read(service: VariantService, table: TableNetworkModelProjectStages, resultSet: ResultSet, setIdentifier: (String) -> String): Boolean {
        val stage = NetworkModelProjectStage(setIdentifier(resultSet.getString(table.MRID.queryIndex))).apply {
            plannedCommissionedDate = resultSet.getInstant(table.PLANNED_COMMISSION_DATE.queryIndex)
            commissionedDate = resultSet.getInstant(table.COMMISSIONED_DATE.queryIndex)
            confidenceLevel = resultSet.getNullableInt(table.CONFIDENCE_LEVEL.queryIndex)
            baseModelVersion = resultSet.getNullableString(table.BASE_MODEL_VERSION.queryIndex)
            lastConflictCheckedAt = resultSet.getInstant(table.LAST_CONFLICT_CHECKED_AT.queryIndex)
            userComments = resultSet.getNullableString(table.USER_COMMENTS.queryIndex)
            service.resolveOrDeferReference(Resolvers.changeSet(this), resultSet.getNullableString(table.CHANGE_SET_MRID.queryIndex))
        }

        return readNetworkModelProjectComponent(service, stage, table, resultSet) && service.addOrThrow(stage)
    }

    // TODO: docs
    fun read(
        service: VariantService,
        table: TableNetworkModelProjectStageEquipmentContainers,
        resultSet: ResultSet,
        setIdentifier: (String) -> String
    ): Boolean {
        val nmpsMRID = resultSet.getString(table.NETWORK_MODEL_PROJECT_STAGE_MRID.queryIndex)
        setIdentifier("$nmpsMRID-to-UNKNOWN")
        val containerMRID = resultSet.getString(table.EQUIPMENT_CONTAINER_MRID.queryIndex)
        val id = setIdentifier("$nmpsMRID-to-$containerMRID")
        val typeNameAndMRID = "NetworkModelProjectStage to EquipmentContainer association $id"

        service.getOrThrow<NetworkModelProjectStage>(nmpsMRID, typeNameAndMRID).addContainer(containerMRID)

        return true
    }

}
