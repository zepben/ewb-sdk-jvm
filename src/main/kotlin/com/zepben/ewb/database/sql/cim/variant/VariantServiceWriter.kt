/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.variant

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.services.variant.VariantService
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.database.sql.cim.BaseServiceWriter

/**
 * A class for writing a [VariantService] into the database.
 *
 * @param databaseTables The [VariantDatabaseTables] to add to the database.
 */
internal class VariantServiceWriter(
    databaseTables: VariantDatabaseTables,
    override val writer: VariantCimWriter = VariantCimWriter(databaseTables)
) : BaseServiceWriter<VariantService>(writer) {

    override fun VariantService.writeService(): Boolean =
        writeEach<NetworkModelProject>(writer::write) and
            writeEach<AnnotatedProjectDependency>(writer::write) and
            writeEach<NetworkModelProjectStage>(writer::write)

}
