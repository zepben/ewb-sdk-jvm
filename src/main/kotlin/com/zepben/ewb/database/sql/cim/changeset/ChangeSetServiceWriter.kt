/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.changeset

import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectCreation
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectDeletion
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectModification
import com.zepben.ewb.database.sql.cim.BaseServiceWriter
import com.zepben.ewb.services.variant.VariantService

/**
 * A class for writing a [VariantService] into the database.
 *
 * @param databaseTables The [ChangeSetDatabaseTables] to add to the database.
 */
internal class ChangeSetServiceWriter(
    databaseTables: ChangeSetDatabaseTables,
    override val writer: ChangeSetCimWriter = ChangeSetCimWriter(databaseTables)
) : BaseServiceWriter<VariantService>(writer) {

    override fun VariantService.writeService(): Boolean =
        writeEach<ChangeSet>(writer::write) and
            writeEach<ObjectCreation>(writer::write) and
            writeEach<ObjectDeletion>(writer::write) and
            writeEach<ObjectModification>(writer::write)
}
