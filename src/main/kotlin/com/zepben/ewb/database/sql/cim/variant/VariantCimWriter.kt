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
import com.zepben.ewb.cim.iec61970.base.core.EquipmentContainer
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.database.sql.cim.CimWriter
import com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.infpart303.networkmodelprojects.TableNetworkModelProjectComponents
import com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.infpart303.networkmodelprojects.TableNetworkModelProjectNetworkModelProjectComponents
import com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.infpart303.networkmodelprojects.TableNetworkModelProjects
import com.zepben.ewb.database.sql.extensions.setNullableString
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableAnnotatedProjectDependencies
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableNetworkModelProjectStageEquipmentContainers
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableNetworkModelProjectStages
import com.zepben.ewb.database.sql.extensions.setInstant
import com.zepben.ewb.database.sql.extensions.setNullableInt
import com.zepben.ewb.services.variant.VariantService
import java.sql.PreparedStatement

/**
 * A class for writing the [VariantService] tables to the database.
 *
 * @property databaseTables The tables available in the database.
 */
class VariantCimWriter(
    override val databaseTables: VariantDatabaseTables
) : CimWriter(databaseTables) {

    // #######################################################
    // # Extensions IEC61970 InfPart303 NetworkModelProjects #
    // #######################################################

    // TODO: docs
    fun write(networkModelProject: NetworkModelProject): Boolean {
        val table = databaseTables.getTable<TableNetworkModelProjects>()
        val insert = databaseTables.getInsert<TableNetworkModelProjects>()

        insert.setNullableString(table.EXTERNAL_STATUS.queryIndex, networkModelProject.externalStatus)
        insert.setInstant(table.FORECAST_COMMISSION_DATE.queryIndex, networkModelProject.forecastCommissionDate)
        insert.setNullableString(table.EXTERNAL_DRIVER.queryIndex, networkModelProject.externalDriver)

        var status = true
        networkModelProject.children.forEach {
            status = status and writeAssociation(networkModelProject, it)
        }

        return status and writeNetworkModelProjectComponent(table, insert, networkModelProject, "network model project")

    }

    private fun writeNetworkModelProjectComponent(
        table: TableNetworkModelProjectComponents,
        insert: PreparedStatement,
        networkModelProjectComponent: NetworkModelProjectComponent,
        description: String
    ): Boolean {
        insert.setInstant(table.CREATED.queryIndex, networkModelProjectComponent.created)
        insert.setInstant(table.UPDATED.queryIndex, networkModelProjectComponent.updated)
        insert.setInstant(table.CLOSED.queryIndex, networkModelProjectComponent.closed)
        insert.setNullableString(table.PARENT_MRID.queryIndex, networkModelProjectComponent.parent?.mRID)

        return writeIdentifiedObject(table, insert, networkModelProjectComponent, description)

    }

    // ############################################
    // # IEC61970 InfPart303 NetworkModelProjects #
    // ############################################

    // TODO: docs
    fun write(annotatedProjectDependency: AnnotatedProjectDependency): Boolean {
        val table = databaseTables.getTable<TableAnnotatedProjectDependencies>()
        val insert = databaseTables.getInsert<TableAnnotatedProjectDependencies>()
        // TODO: handle allfieldsnull
        insert.setString(table.DEPENDENCY_TYPE.queryIndex, annotatedProjectDependency.dependencyType.name)
        insert.setString(table.DEPENDENCY_DEPENDENT_ON_STAGE_MRID.queryIndex, annotatedProjectDependency.dependencyDependentOnStage?.mRID)
        insert.setString(table.DEPENDENCY_DEPENDING_STAGE_MRID.queryIndex, annotatedProjectDependency.dependencyDependingStage?.mRID)

        return writeIdentifiedObject(table, insert, annotatedProjectDependency, "annotated project dependency")

    }

    // TODO: docs
    fun write(networkModelProjectStage: NetworkModelProjectStage): Boolean {
        val table = databaseTables.getTable<TableNetworkModelProjectStages>()
        val insert = databaseTables.getInsert<TableNetworkModelProjectStages>()

        insert.setInstant(table.PLANNED_COMMISSION_DATE.queryIndex, networkModelProjectStage.plannedCommissionedDate)
        insert.setInstant(table.COMMISSIONED_DATE.queryIndex, networkModelProjectStage.commissionedDate)
        insert.setNullableInt(table.CONFIDENCE_LEVEL.queryIndex, networkModelProjectStage.confidenceLevel)
        insert.setNullableString(table.BASE_MODEL_VERSION.queryIndex, networkModelProjectStage.baseModelVersion)
        insert.setInstant(table.LAST_CONFLICT_CHECKED_AT.queryIndex, networkModelProjectStage.lastConflictCheckedAt)
        insert.setNullableString(table.USER_COMMENTS.queryIndex, networkModelProjectStage.userComments)
        insert.setNullableString(table.CHANGE_SET_MRID.queryIndex, networkModelProjectStage.changeSet?.mRID)

        var status = true
        networkModelProjectStage.equipmentContainers.forEach {
            status = status and writeAssociation(networkModelProjectStage, it)
        }

        return status and writeIdentifiedObject(table, insert, networkModelProjectStage, "network model project stage")

    }

    // ######################
    // # IEC61970 Base Core #
    // ######################

    private fun writeAssociation(networkModelProject: NetworkModelProject, networkModelProjectComponent: NetworkModelProjectComponent): Boolean {
        val table = databaseTables.getTable<TableNetworkModelProjectNetworkModelProjectComponents>()
        val insert = databaseTables.getInsert<TableNetworkModelProjectNetworkModelProjectComponents>()

        insert.setString(table.NETWORK_MODEL_PROJECT_MRID.queryIndex, networkModelProject.mRID)
        insert.setString(table.NETWORK_MODEL_PROJECT_COMPONENT_MRID.queryIndex, networkModelProjectComponent.mRID)

        return insert.tryExecuteSingleUpdate("network model project to network model project component association")
    }

    private fun writeAssociation(networkModelProjectStage: NetworkModelProjectStage, equipmentContainer: EquipmentContainer): Boolean {
        val table = databaseTables.getTable<TableNetworkModelProjectStageEquipmentContainers>()
        val insert = databaseTables.getInsert<TableNetworkModelProjectStageEquipmentContainers>()

        insert.setString(table.NETWORK_MODEL_PROJECT_MRID.queryIndex, networkModelProjectStage.mRID)
        insert.setString(table.EQUIPMENT_CONTAINER_MRID.queryIndex, equipmentContainer.mRID)

        return insert.tryExecuteSingleUpdate("network model project stage to equipment container association")
    }
}
